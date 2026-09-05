package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Fabric roll and raw material storage (with both Meters and Kilograms)
 */
@Entity(tableName = "fabrics")
data class FabricEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val code: String,
  val color: String,
  val batchNumber: String,
  val supplierName: String,
  val rollCount: Int,
  val totalMeters: Double,
  val totalWeightKg: Double = 0.0,
  val buyPricePerMeter: Long,
  val buyPricePerKg: Long = 0L,
  val currentMarketPrice: Long,
  val isLowStock: Boolean = false,
  val previousBuyPricePerMeter: Long = 0L,
  val previousBuyPricePerKg: Long = 0L,
  val previousPurchaseDate: String = "",
  val purchaseDate: String = "",
) {
  val totalStockValue: Long get() = (totalMeters * currentMarketPrice).toLong()
  val metersPerKg: Double get() = if (totalWeightKg > 0) totalMeters / totalWeightKg else 0.0
  val kgPerRoll: Double get() = if (rollCount > 0 && totalWeightKg > 0) totalWeightKg / rollCount else 0.0
  val hasPriceChanged: Boolean get() = previousBuyPricePerMeter > 0L && previousBuyPricePerMeter != buyPricePerMeter
}

/**
 * Cutting operation tracking: need vs cut vs shortage & fabric consumption audit (Meters & Kg)
 */
@Entity(tableName = "cutting_orders")
data class CuttingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val modelCode: String,
  val modelName: String,
  val fabricCode: String,
  val targetQuantity: Int,
  val cutQuantity: Int,
  val standardMetersPerItem: Double,
  val actualMetersPerItem: Double,
  val standardWeightKgPerItem: Double = 0.0,
  val actualWeightKgPerItem: Double = 0.0,
  val status: String, // "در حال برش", "تکمیل شده", "کسری"
  val date: String,
) {
  val shortageQuantity: Int get() = (targetQuantity - cutQuantity).coerceAtLeast(0)
  val progressPercent: Int get() = if (targetQuantity > 0) ((cutQuantity.toDouble() / targetQuantity) * 100).toInt().coerceIn(0, 100) else 0
  val consumptionDeviationPercent: Double get() = if (standardMetersPerItem > 0) ((actualMetersPerItem - standardMetersPerItem) / standardMetersPerItem) * 100 else 0.0
  val isAbnormalConsumption: Boolean get() = consumptionDeviationPercent > 10.0
}

/**
 * Production batches with auto-calculated unit metrics and cost-profit analysis
 */
@Entity(tableName = "production_records")
data class ProductionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val modelCode: String,
  val modelName: String,
  val quantity: Int,
  val fabricRollsUsed: Int,
  val fabricMetersUsed: Double,
  val totalWeightKg: Double,
  val sewingWagePerItem: Long,
  val fabricPricePerMeter: Long,
  val accessoriesCostPerItem: Long,
  val status: String, // "در حال دوخت", "تکمیل شده", "بسته‌بندی"
  val date: String,
) {
  val weightPerItemGrams: Double get() = if (quantity > 0) (totalWeightKg * 1000.0) / quantity else 0.0
  val fabricMetersPerItem: Double get() = if (quantity > 0) fabricMetersUsed / quantity else 0.0
  val fabricCostPerItem: Long get() = (fabricMetersPerItem * fabricPricePerMeter).toLong()
  val unitCostPrice: Long get() = fabricCostPerItem + sewingWagePerItem + accessoriesCostPerItem
  val totalCost: Long get() = unitCostPrice * quantity
  val estimatedSalePricePerItem: Long get() = (unitCostPrice * 1.55).toLong()
  val unitProfit: Long get() = estimatedSalePricePerItem - unitCostPrice
  val totalProfit: Long get() = unitProfit * quantity
}

/**
 * Warehouse inventory products (ready, reserved, available, raw materials & accessories)
 */
@Entity(tableName = "inventory_items")
data class InventoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val code: String,
  val category: String, // "محصولات آماده", "مواد اولیه", "ملزومات"
  val readyForShipment: Int,
  val reservedQuantity: Int,
  val availableForSale: Int,
  val unitSalePrice: Long,
  val unitCostPrice: Long,
  val unitWeightGrams: Double,
  val totalWeightKg: Double = 0.0,
  val unitType: String = "عدد", // "عدد", "کیلوگرم", "متر", "دوک و قرقره", "توپی", "بسته", ...
  val lastUpdated: String,
  val previousCostPrice: Long = 0L,
  val previousCostDate: String = "",
  val supplierName: String = "",
  val metersPerKg: Double = 0.0, // برای ملزومات بر پایه کیلو: متراژ در کیلو
  val pricePerMeter: Long = 0L, // قیمت محاسبه‌شده هر متر
) {
  val totalStock: Int get() = readyForShipment + reservedQuantity + availableForSale
  val totalStockValue: Long get() = totalStock * unitSalePrice
  val hasPriceChanged: Boolean get() = previousCostPrice > 0L && previousCostPrice != unitCostPrice
}

/**
 * Sales and client orders with timeline status and stock fulfillment check
 */
@Entity(tableName = "sales_orders")
data class SaleOrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderNumber: String,
  val customerName: String,
  val customerPhone: String,
  val modelCode: String,
  val modelName: String,
  val quantity: Int,
  val unitPrice: Long,
  val unitCost: Long,
  val discountAmount: Long = 0L,
  val paidAmount: Long = 0L,
  val orderDate: String,
  val deliveryStatus: String, // "ثبت شده", "در تولید", "در حال تکمیل", "آماده ارسال", "ارسال شده", "تحویل شده"
  val isDelayed: Boolean = false,
  val delayDays: Int = 0,
) {
  val grossTotal: Long get() = quantity * unitPrice
  val netTotal: Long get() = (grossTotal - discountAmount).coerceAtLeast(0L)
  val remainingDebt: Long get() = (netTotal - paidAmount).coerceAtLeast(0L)
  val totalCost: Long get() = quantity * unitCost
  val totalProfit: Long get() = (netTotal - totalCost).coerceAtLeast(0L)
}

/**
 * Customer profile with mini-analytics and purchase history tiers
 */
@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val company: String,
  val phone: String,
  val address: String,
  val category: String, // "عمده‌فروش", "فروشگاه زنجیره‌ای", "بوتیک و آنلاین"
  val totalPurchases: Long,
  val orderCount: Int,
  val currentDebt: Long,
  val tier: String, // "خرید اول", "خرید دوم", "خرید سوم+"
  val popularModels: String,
  val lastOrderDate: String,
)

/**
 * Supplier entity for fabrics and accessories with unified comprehensive profile
 */
@Entity(tableName = "suppliers")
data class SupplierEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // نام و نام خانوادگی
  val storeName: String = "", // اسم فروشگاه
  val mobile: String = "", // شماره موبایل
  val phone: String = "", // شماره ثابت
  val address: String = "", // آدرس
  val distributionCategory: String = "", // توزیع‌کننده چی هست (پارچه، زیپ، دکمه، نخ، ملزومات)
  val description: String = "", // توضیحات
  val supplyType: String = "پارچه و ملزومات",
  val totalPurchases: Long = 0L,
  val lastPurchaseDate: String = "",
  val priceHistoryNote: String = "",
)

/**
 * System settings and production model benchmarks
 */
@Entity(tableName = "model_standards")
data class ModelStandardEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val modelCode: String,
  val modelName: String,
  val standardFabricConsumptionMeters: Double,
  val standardWeightGrams: Double,
  val sewingWage: Long,
  val suggestedSalePrice: Long,
  val baseFabricCostPerMeter: Long,
)

enum class PeriodFilter(val title: String) {
  TODAY("امروز"),
  MONTH("ماه"),
  YEAR("سال"),
  CUSTOM("بازه دلخواه")
}

data class AlertItem(
  val id: String,
  val title: String,
  val description: String,
  val type: AlertType,
  val tag: String,
)

enum class AlertType {
  DANGER,
  WARNING,
  INFO
}

data class ChartPoint(
  val label: String,
  val value: Long,
  val formattedValue: String,
)

/**
 * Chart Type option for first page dashboard
 */
enum class DashboardChartType(val title: String, val desc: String) {
  AREA_LINE("نمودار خطی پیوسته (موجی)", "نمایش روند زمانی فروش و سود با خط هموار"),
  BAR("نمودار میله‌ای آماری (ستونی)", "مقایسه دقیق مقادیر در قالب ستون‌های آماری"),
  DONUT_PRIMARY("چارت گرد اختصاصی (دونات)", "تمرکز روی سهم پرتفوی انبار و ارزش موجودی")
}

/**
 * Layout arrangement option for first page dashboard
 */
enum class DashboardLayoutArrangement(val title: String, val desc: String) {
  STANDARD("استاندارد اجرایی", "کارت‌های شاخص -> نمودارها -> هشدارها -> سفارشات"),
  CHARTS_FIRST("نمودار محور و آماری", "نمودار تحلیلی و چارت گرد در ابتدا -> سپس آمار و ارقام"),
  ALERTS_FIRST("عملیاتی و هشدار محور", "مرکز هشدارهای کمبود در بالا -> وضعیت سفارشات -> نمودارها")
}

/**
 * Fixed cost benchmarks, overheads, alert thresholds & factory default settings
 */
@Entity(tableName = "factory_settings")
data class FactorySettingsEntity(
  @PrimaryKey val id: Long = 1L,
  val fixedShippingCostPerOrder: Long = 180000L, // هزینه باربری ثابت
  val fixedShippingCostPerRoll: Long = 85000L,   // هزینه باربری به ازای هر طاقه
  val targetProfitMarginPercent: Double = 35.0,  // درصد حاشیه سود ثابت هدف
  val overheadCostPerItem: Long = 45000L,        // هزینه سربار و اجاره کارگاه به ازای هر کار
  val defaultAccessoriesCost: Long = 32000L,     // هزینه ملزومات پایه
  val isDarkTheme: Boolean = true,               // تم انتخابی (تاریک/روشن)
  val companyName: String = "تولیدی برتر پوشاک",
  // New user requested features:
  val dashboardChartType: String = DashboardChartType.AREA_LINE.name, // انتخاب نوع چارت آمارگیر
  val dashboardLayout: String = DashboardLayoutArrangement.STANDARD.name, // انتخاب نوع چیدمان بخش‌ها
  // Stock alert thresholds (حد هشدار کمبود موجودی جهت ایجاد نوتیفیکیشن)
  val minFabricRollsThreshold: Int = 10,       // حداقل تعداد طاقه پارچه
  val minFabricWeightKgThreshold: Double = 200.0, // حداقل وزن پارچه (کیلوگرم)
  val minReadyGoodsCountThreshold: Int = 300,  // حداقل تعداد کار آماده (تعداد)
  val minAccessoriesWeightKgThreshold: Double = 15.0, // حداقل وزن ملزومات خیاطی (کیلوگرم)
  val selectedFontCode: String = "yekan", // فونت پیش‌فرض یکان و قابل تغییر به وزیرمتن، شبنم و سیستم
  val systemNotificationsEnabled: Boolean = true, // ارسال هشدارهای فوری به نوتیفیکیشن بالای گوشی
  val autoSyncRollPrices: Boolean = true, // اعمال تغییرات قیمت طاقه/ملزومات بر موجودی انبار با ثبت قیمت قبلی قرمز
  val customUnitTypes: String = "عدد,کیلوگرم,متر,دوک و قرقره,توپی" // واحدهای پیش‌فرض و افزوده شده
)

/**
 * Donut / Pie slice data structure for Circular Chart
 */
data class DonutSlice(
  val label: String,
  val value: Double,
  val count: Int,
  val unit: String,
  val color: androidx.compose.ui.graphics.Color,
  val formattedValue: String,
)

/**
 * Item specification for multi-model roll cutting operation
 */
data class MultiCutModelItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val modelName: String,
  val modelCode: String,
  val cutQuantity: Int,
  val metersPerItem: Double, // قد کار یا متراژ مصرفی هر عدد (متر)
  val heightCm: Int = 0,     // قد کار به سانتی‌متر
  val trimsUsedNote: String = "",
) {
  val totalMetersUsed: Double get() = cutQuantity * metersPerItem
}

/**
 * Trims and accessories bill-of-materials entry
 */
data class TrimsConsumptionItem(
  val accessoryCode: String,
  val accessoryName: String,
  val unit: String,
  val quantityPerGarment: Double,
  val unitCostPrice: Long,
) {
  val totalCostPerGarment: Long get() = (quantityPerGarment * unitCostPrice).toLong()
}

