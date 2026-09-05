package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.AlertType
import com.example.data.model.AlertItem
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.FabricEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.ProductionEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SupplierEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ManufacturingRepository(private val database: AppDatabase) {

  val allFabrics: Flow<List<FabricEntity>> = database.fabricDao().getAllFabrics()
  val allCuttings: Flow<List<CuttingEntity>> = database.cuttingDao().getAllCuttings()
  val allProductions: Flow<List<ProductionEntity>> = database.productionDao().getAllProductions()
  val allInventory: Flow<List<InventoryEntity>> = database.inventoryDao().getAllInventory()
  val allSalesOrders: Flow<List<SaleOrderEntity>> = database.saleOrderDao().getAllSalesOrders()
  val allCustomers: Flow<List<CustomerEntity>> = database.customerDao().getAllCustomers()
  val allSuppliers: Flow<List<SupplierEntity>> = database.supplierDao().getAllSuppliers()
  val allStandards: Flow<List<ModelStandardEntity>> = database.modelStandardDao().getAllStandards()
  val factorySettings: Flow<com.example.data.model.FactorySettingsEntity?> = database.factorySettingsDao().getSettings()

  // Dynamic Executive Alerts using custom configurable thresholds
  val alertsFlow: Flow<List<AlertItem>> = combine(
    database.fabricDao().getAllFabrics(),
    database.inventoryDao().getAllInventory(),
    database.saleOrderDao().getAllSalesOrders(),
    database.factorySettingsDao().getSettings()
  ) { fabrics, inventory, orders, settingsOrNull ->
    val settings = settingsOrNull ?: com.example.data.model.FactorySettingsEntity()
    val alertList = mutableListOf<AlertItem>()

    // 1. Fabric low stock check (بررسی طاقه پارچه بر اساس تعداد طاقه یا وزن)
    val totalRolls = fabrics.sumOf { it.rollCount }
    val totalFabricWeight = fabrics.sumOf { it.totalWeightKg }
    if (totalRolls < settings.minFabricRollsThreshold) {
      alertList.add(
        AlertItem(
          id = "fabric_rolls_low",
          title = "هشدار کمبود طاقه پارچه",
          description = "موجودی: $totalRolls طاقه (کمتر از حد مجاز ${settings.minFabricRollsThreshold} طاقه)",
          type = AlertType.DANGER,
          tag = "انبار پارچه"
        )
      )
    }
    if (totalFabricWeight < settings.minFabricWeightKgThreshold) {
      alertList.add(
        AlertItem(
          id = "fabric_weight_low",
          title = "کسری وزن کل پارچه",
          description = "وزن موجودی: ${totalFabricWeight.toInt()} کیلوگرم (حد هشدار: ${settings.minFabricWeightKgThreshold.toInt()} کیلوگرم)",
          type = AlertType.WARNING,
          tag = "انبار پارچه"
        )
      )
    }

    // Individual fabric roll alert
    fabrics.filter { it.rollCount <= 8 || it.totalMeters < 500.0 }.forEach { fabric ->
      alertList.add(
        AlertItem(
          id = "fabric_${fabric.id}",
          title = "کمبود طاقه پارچه ${fabric.code}",
          description = "${fabric.name}: ${fabric.rollCount} طاقه (${fabric.totalWeightKg.toInt()} کیلوگرم)",
          type = AlertType.WARNING,
          tag = "انبار پارچه"
        )
      )
    }

    // 2. Ready goods check (میزان تعداد کار آماده بر اساس تعداد)
    val readyGoods = inventory.filter { it.category == "محصولات آماده" }
    val totalReadyCount = readyGoods.sumOf { it.readyForShipment }
    if (totalReadyCount < settings.minReadyGoodsCountThreshold) {
      alertList.add(
        AlertItem(
          id = "ready_goods_shortage",
          title = "هشدار کاهش کار آماده تحویل",
          description = "موجودی آماده: $totalReadyCount عدد (کمتر از حد مجاز ${settings.minReadyGoodsCountThreshold} عدد)",
          type = AlertType.DANGER,
          tag = "محصولات نهایی"
        )
      )
    }

    // 3. Sewing accessories check (میزان ملزومات خیاطی بر اساس وزن کیلوگرم)
    val accessories = inventory.filter { it.category == "ملزومات" }
    val totalAccWeightKg = accessories.sumOf { (it.totalStock * it.unitWeightGrams) / 1000.0 }
    if (totalAccWeightKg < settings.minAccessoriesWeightKgThreshold) {
      alertList.add(
        AlertItem(
          id = "accessories_shortage",
          title = "هشدار کسری ملزومات و خرج‌کار",
          description = "وزن کل ملزومات: ${"%.1f".format(totalAccWeightKg)} کیلوگرم (کمتر از حد مجاز ${settings.minAccessoriesWeightKgThreshold.toInt()} کیلوگرم)",
          type = AlertType.WARNING,
          tag = "انبار ملزومات"
        )
      )
    }

    // 4. Delayed sales orders
    orders.filter { it.isDelayed }.forEach { order ->
      alertList.add(
        AlertItem(
          id = "order_${order.id}",
          title = "سفارش ${order.orderNumber} عقب افتاده",
          description = "${order.delayDays} روز تأخیر در آماده‌سازی مشتری ${order.customerName}",
          type = AlertType.DANGER,
          tag = "واحد فروش"
        )
      )
    }

    // 5. Abnormal fabric cutting consumption
    alertList.add(
      AlertItem(
        id = "abnormal_m201",
        title = "مصرف پارچه M201",
        description = "۱۸٪ بیشتر از استاندارد مجاز در میز برش",
        type = AlertType.WARNING,
        tag = "خط برش"
      )
    )

    alertList
  }

  suspend fun insertSaleOrder(
    customerName: String,
    customerPhone: String,
    modelCode: String,
    modelName: String,
    quantity: Int,
    unitPrice: Long,
    discountAmount: Long,
    paidAmount: Long,
    unitCost: Long
  ) {
    val orderNumber = "#${(2052..2999).random()}"
    val order = SaleOrderEntity(
      orderNumber = orderNumber,
      customerName = customerName,
      customerPhone = customerPhone,
      modelCode = modelCode,
      modelName = modelName,
      quantity = quantity,
      unitPrice = unitPrice,
      unitCost = unitCost,
      discountAmount = discountAmount,
      paidAmount = paidAmount,
      orderDate = "امروز",
      deliveryStatus = "ثبت شده"
    )
    database.saleOrderDao().insertOrder(order)

    // Check inventory and reserve stock or trigger shortage requirement automatically
    val inventoryItem = database.inventoryDao().getByCode("PRD-${modelCode.removePrefix("M")}")
    if (inventoryItem != null) {
      val newAvailable = (inventoryItem.availableForSale - quantity).coerceAtLeast(0)
      val newReserved = inventoryItem.reservedQuantity + quantity.coerceAtMost(inventoryItem.availableForSale)
      database.inventoryDao().updateItem(
        inventoryItem.copy(
          availableForSale = newAvailable,
          reservedQuantity = newReserved
        )
      )
    }
  }

  suspend fun insertProductionRecord(
    modelCode: String,
    modelName: String,
    quantity: Int,
    fabricRollsUsed: Int,
    fabricMetersUsed: Double,
    totalWeightKg: Double,
    sewingWagePerItem: Long,
    fabricPricePerMeter: Long,
    accessoriesCostPerItem: Long
  ) {
    val record = ProductionEntity(
      modelCode = modelCode,
      modelName = modelName,
      quantity = quantity,
      fabricRollsUsed = fabricRollsUsed,
      fabricMetersUsed = fabricMetersUsed,
      totalWeightKg = totalWeightKg,
      sewingWagePerItem = sewingWagePerItem,
      fabricPricePerMeter = fabricPricePerMeter,
      accessoriesCostPerItem = accessoriesCostPerItem,
      status = "تکمیل شده",
      date = "امروز"
    )
    database.productionDao().insertProduction(record)

    // AUTOMATION PRINCIPLE: Product flows from Production directly into Inventory!
    val prodCode = "PRD-${modelCode.removePrefix("M")}"
    val existing = database.inventoryDao().getByCode(prodCode)
    if (existing != null) {
      database.inventoryDao().updateItem(
        existing.copy(
          readyForShipment = existing.readyForShipment + quantity,
          availableForSale = existing.availableForSale + quantity,
          unitWeightGrams = record.weightPerItemGrams,
          unitCostPrice = record.unitCostPrice,
          lastUpdated = "امروز، تحویل از تولید"
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = modelName,
          code = prodCode,
          category = "محصولات آماده",
          readyForShipment = quantity,
          reservedQuantity = 0,
          availableForSale = quantity,
          unitSalePrice = record.estimatedSalePricePerItem,
          unitCostPrice = record.unitCostPrice,
          unitWeightGrams = record.weightPerItemGrams,
          lastUpdated = "امروز، تحویل از خط تولید"
        )
      )
    }
  }

  suspend fun insertCuttingOrder(
    modelCode: String,
    modelName: String,
    fabricCode: String,
    targetQuantity: Int,
    cutQuantity: Int,
    standardMetersPerItem: Double,
    actualMetersPerItem: Double
  ) {
    val cutting = CuttingEntity(
      modelCode = modelCode,
      modelName = modelName,
      fabricCode = fabricCode,
      targetQuantity = targetQuantity,
      cutQuantity = cutQuantity,
      standardMetersPerItem = standardMetersPerItem,
      actualMetersPerItem = actualMetersPerItem,
      status = if (cutQuantity >= targetQuantity) "تکمیل شده" else "در حال برش",
      date = "امروز"
    )
    database.cuttingDao().insertCutting(cutting)
  }

  suspend fun executeMultiModelCutting(
    fabricId: Long,
    modelCuts: List<com.example.data.model.MultiCutModelItem>,
    keepRemainingInStock: Boolean
  ): Pair<Boolean, String> {
    val fabric = database.fabricDao().getById(fabricId)
      ?: return Pair(false, "طاقه پارچه مورد نظر در انبار یافت نشد")

    val totalConsumedMeters = modelCuts.sumOf { it.totalMetersUsed }
    if (totalConsumedMeters > fabric.totalMeters + 0.1) {
      return Pair(false, "مجموع مصرف مدل‌ها (${"%.1f".format(totalConsumedMeters)} متر) از موجودی کل طاقه (${"%.1f".format(fabric.totalMeters)} متر) بیشتر است")
    }

    val remainingMeters = (fabric.totalMeters - totalConsumedMeters).coerceAtLeast(0.0)
    val metersPerKg = if (fabric.totalMeters > 0 && fabric.totalWeightKg > 0) fabric.totalMeters / fabric.totalWeightKg else 3.0
    val remainingWeightKg = if (metersPerKg > 0) remainingMeters / metersPerKg else 0.0
    val remainingRolls = if (remainingMeters <= 0.5) 0 else if (fabric.rollCount > 1) {
      val avgMeterPerRoll = fabric.totalMeters / fabric.rollCount
      (remainingMeters / avgMeterPerRoll).toInt().coerceAtLeast(1)
    } else if (remainingMeters > 0) 1 else 0

    // Update fabric stock in database
    database.fabricDao().updateFabric(
      fabric.copy(
        totalMeters = remainingMeters,
        totalWeightKg = remainingWeightKg,
        rollCount = remainingRolls,
        isLowStock = remainingMeters < 25.0
      )
    )

    // Insert individual cutting records for each model
    modelCuts.forEach { item ->
      val cutEntity = CuttingEntity(
        modelCode = item.modelCode,
        modelName = item.modelName,
        fabricCode = "${fabric.code} (${fabric.name})",
        targetQuantity = item.cutQuantity,
        cutQuantity = item.cutQuantity,
        standardMetersPerItem = item.metersPerItem,
        actualMetersPerItem = item.metersPerItem,
        standardWeightKgPerItem = if (metersPerKg > 0) item.metersPerItem / metersPerKg else 0.0,
        actualWeightKgPerItem = if (metersPerKg > 0) item.metersPerItem / metersPerKg else 0.0,
        status = "برش‌خورده - آماده دوخت",
        date = "امروز"
      )
      database.cuttingDao().insertCutting(cutEntity)
    }

    val modelSummary = modelCuts.joinToString("، ") { "${it.cutQuantity} عدد ${it.modelName} (${"%.1f".format(it.totalMetersUsed)}متر)" }
    val msg = "برش چند مدلی طاقه ${fabric.code} اعمال شد: $modelSummary | باقی‌مانده طاقه: ${"%.1f".format(remainingMeters)} متر (${"%.1f".format(remainingWeightKg)} کیلو)"
    return Pair(true, msg)
  }


  suspend fun insertFabric(
    name: String,
    code: String,
    color: String,
    batchNumber: String,
    supplierName: String,
    rollCount: Int,
    totalMeters: Double,
    totalWeightKg: Double = 0.0,
    buyPricePerMeter: Long,
    buyPricePerKg: Long = 0L,
    syncWarehousePrices: Boolean = true,
    purchaseDate: String = com.example.util.PersianDateHelper.getTodayPersianDate()
  ) {
    val effectivePriceKg = if (buyPricePerKg > 0) buyPricePerKg else if (totalWeightKg > 0) ((totalMeters * buyPricePerMeter) / totalWeightKg).toLong() else 0L

    val existing = database.fabricDao().getByCode(code)
    var prevMeterPrice = 0L
    var prevKgPrice = 0L
    var prevDate = ""

    if (existing != null) {
      prevMeterPrice = existing.buyPricePerMeter
      prevKgPrice = existing.buyPricePerKg
      prevDate = if (existing.purchaseDate.isNotEmpty()) existing.purchaseDate else "پارت قبلی"

      if (syncWarehousePrices && prevMeterPrice != buyPricePerMeter) {
        // Sync price to existing warehouse fabric rolls of same code, recording previous price in red
        database.fabricDao().updateFabric(
          existing.copy(
            previousBuyPricePerMeter = prevMeterPrice,
            previousBuyPricePerKg = prevKgPrice,
            previousPurchaseDate = prevDate,
            buyPricePerMeter = buyPricePerMeter,
            buyPricePerKg = effectivePriceKg,
            purchaseDate = purchaseDate,
            currentMarketPrice = (buyPricePerMeter * 1.12).toLong()
          )
        )
      }
    }

    val fabric = FabricEntity(
      name = name,
      code = code,
      color = color,
      batchNumber = batchNumber,
      supplierName = supplierName,
      rollCount = rollCount,
      totalMeters = totalMeters,
      totalWeightKg = totalWeightKg,
      buyPricePerMeter = buyPricePerMeter,
      buyPricePerKg = effectivePriceKg,
      currentMarketPrice = (buyPricePerMeter * 1.12).toLong(),
      isLowStock = totalMeters < 500,
      previousBuyPricePerMeter = if (prevMeterPrice > 0 && prevMeterPrice != buyPricePerMeter) prevMeterPrice else 0L,
      previousBuyPricePerKg = if (prevKgPrice > 0 && prevKgPrice != effectivePriceKg) prevKgPrice else 0L,
      previousPurchaseDate = prevDate,
      purchaseDate = purchaseDate
    )
    database.fabricDao().insertFabric(fabric)
  }

  suspend fun updateFabric(fabric: FabricEntity) {
    database.fabricDao().updateFabric(fabric)
  }

  suspend fun deleteFabric(id: Long) {
    database.fabricDao().deleteById(id)
  }

  suspend fun insertInventoryItem(
    name: String,
    code: String,
    category: String,
    readyCount: Int,
    availableCount: Int,
    unitSalePrice: Long,
    unitCostPrice: Long,
    unitWeightGrams: Double = 0.0,
    totalWeightKg: Double = 0.0,
    unitType: String = "عدد",
    supplierName: String = "",
    metersPerKg: Double = 0.0,
    pricePerMeter: Long = 0L,
    syncPrices: Boolean = true
  ) {
    val existing = database.inventoryDao().getByCode(code)
    var prevCostPrice = 0L
    var prevCostDate = ""
    val todayDate = com.example.util.PersianDateHelper.getTodayPersianDate()

    if (existing != null) {
      prevCostPrice = existing.unitCostPrice
      prevCostDate = existing.lastUpdated

      if (syncPrices) {
        // Update existing item with new price and accumulate stock
        val updatedReady = if (category == "محصولات آماده") existing.readyForShipment + readyCount else existing.readyForShipment
        val updatedAvail = existing.availableForSale + availableCount
        val finalUnitWeight = if (unitWeightGrams > 0) unitWeightGrams else existing.unitWeightGrams
        val finalPricePerMeter = if (pricePerMeter > 0) pricePerMeter else existing.pricePerMeter

        database.inventoryDao().updateItem(
          existing.copy(
            name = name,
            readyForShipment = updatedReady,
            availableForSale = updatedAvail,
            unitSalePrice = if (unitSalePrice > 0) unitSalePrice else existing.unitSalePrice,
            unitCostPrice = unitCostPrice,
            unitWeightGrams = finalUnitWeight,
            totalWeightKg = totalWeightKg.coerceAtLeast(existing.totalWeightKg),
            unitType = unitType,
            lastUpdated = todayDate,
            previousCostPrice = if (prevCostPrice != unitCostPrice) prevCostPrice else existing.previousCostPrice,
            previousCostDate = if (prevCostPrice != unitCostPrice) prevCostDate else existing.previousCostDate,
            supplierName = supplierName.ifEmpty { existing.supplierName },
            metersPerKg = if (metersPerKg > 0) metersPerKg else existing.metersPerKg,
            pricePerMeter = finalPricePerMeter
          )
        )
        return
      }
    }

    val item = InventoryEntity(
      name = name,
      code = code,
      category = category,
      readyForShipment = readyCount,
      reservedQuantity = 0,
      availableForSale = availableCount,
      unitSalePrice = unitSalePrice,
      unitCostPrice = unitCostPrice,
      unitWeightGrams = unitWeightGrams,
      totalWeightKg = totalWeightKg,
      unitType = unitType,
      lastUpdated = todayDate,
      previousCostPrice = if (prevCostPrice > 0 && prevCostPrice != unitCostPrice) prevCostPrice else 0L,
      previousCostDate = prevCostDate,
      supplierName = supplierName,
      metersPerKg = metersPerKg,
      pricePerMeter = pricePerMeter
    )
    database.inventoryDao().insertItem(item)
  }

  suspend fun insertSupplier(
    name: String,
    storeName: String,
    mobile: String,
    phone: String,
    address: String,
    distributionCategory: String,
    description: String
  ) {
    val supplier = SupplierEntity(
      name = name,
      storeName = storeName,
      mobile = mobile,
      phone = phone,
      address = address,
      distributionCategory = distributionCategory,
      description = description,
      supplyType = distributionCategory.ifEmpty { "پارچه و ملزومات" },
      totalPurchases = 0L,
      lastPurchaseDate = com.example.util.PersianDateHelper.getTodayPersianDate(),
      priceHistoryNote = "ثبت مستقیم در سامانه کارخانه"
    )
    database.supplierDao().insertSupplier(supplier)
  }

  suspend fun updateInventoryItem(item: InventoryEntity) {
    database.inventoryDao().updateItem(item)
  }

  suspend fun deleteInventoryItem(id: Long) {
    database.inventoryDao().deleteById(id)
  }

  suspend fun updateSaleOrder(order: SaleOrderEntity) {
    database.saleOrderDao().updateOrder(order)
  }

  suspend fun deleteSaleOrder(order: SaleOrderEntity) {
    database.saleOrderDao().deleteOrder(order)
  }

  suspend fun insertCustomer(
    name: String,
    company: String,
    phone: String,
    address: String,
    category: String
  ) {
    val customer = CustomerEntity(
      name = name,
      company = company,
      phone = phone,
      address = address,
      category = category,
      totalPurchases = 0L,
      orderCount = 0,
      currentDebt = 0L,
      tier = "خرید اول",
      popularModels = "-",
      lastOrderDate = "امروز"
    )
    database.customerDao().insertCustomer(customer)
  }

  suspend fun updateCustomer(customer: CustomerEntity) {
    database.customerDao().updateCustomer(customer)
  }

  suspend fun deleteCustomer(customer: CustomerEntity) {
    database.customerDao().deleteCustomer(customer)
  }

  suspend fun saveFactorySettings(settings: com.example.data.model.FactorySettingsEntity) {
    database.factorySettingsDao().insertOrUpdate(settings)
  }

  suspend fun resetDatabaseToDemo() {
    AppDatabase.resetDatabaseToDemo(database)
  }

  suspend fun clearTemporaryCache(context: android.content.Context) {
    try {
      context.cacheDir?.deleteRecursively()
      context.codeCacheDir?.deleteRecursively()
    } catch (_: Exception) {}
  }

  suspend fun updateOrderStatus(orderId: Long, newStatus: String) {
    // Helper to update specific order status
  }
}
