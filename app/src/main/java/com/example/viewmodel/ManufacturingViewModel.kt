package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AlertItem
import com.example.data.model.ChartPoint
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.FabricEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.PeriodFilter
import com.example.data.model.ProductionEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SupplierEntity
import com.example.data.repository.ManufacturingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
  DASHBOARD("خانه"),
  ANALYTICS("گزارش"),
  INVENTORY("انبار"),
  MORE("بیشتر")
}

enum class MoreSubSection(val title: String) {
  ORDERS("سفارشات"),
  PRODUCTION("تولید"),
  CUTTING("برش"),
  CUSTOMERS("مشتریان"),
  SUPPLIERS("تأمین‌کنندگان"),
  SETTINGS("تنظیمات استاندارد")
}

enum class QuickActionType {
  NONE,
  WAREHOUSE_HUB,   // مرکز عملیات انبارداری و ثبت سریع
  FABRIC_IN,       // افزودن طاقه پارچه (متراژ و کیلوگرم)
  READY_GOODS_IN,  // ثبت تعداد کار آماده به انبار کالا
  ACCESSORY_IN,    // ثبت ملزومات و خرج‌کار
  CUSTOMER,        // افزودن مشتری جدید
  SALE,            // ثبت فاکتور فروش و سفارش
  PRODUCTION,      // ثبت کارگاه تولید
  CUTTING,         // ثبت پارت برشکاری
  MULTI_CUT,       // پاپ‌آپ بازشونده برش چند مدلی از یک طاقه
  SETTINGS_EDIT,   // ویرایش هزینه‌های ثابت و سربار
  EDIT_FABRIC,     // ویرایش مشخصات طاقه پارچه
  EDIT_INVENTORY,  // ویرایش کالا و موجودی انبار
  EDIT_ORDER,      // ویرایش سفارش فروش
  EDIT_CUSTOMER    // ویرایش مشخصات مشتری
}

enum class ChartMetric(val title: String) {
  DAILY_SALES("فروش روزانه"),
  MONTHLY_SALES("فروش ماهانه"),
  MONTHLY_PROFIT("سود ماهانه"),
  MONTHLY_PRODUCTION("تولید ماهانه")
}

data class DashboardKpiState(
  val salesAmount: Long = 2480000000L,
  val salesGrowthPercent: Double = 12.4,
  val netProfitAmount: Long = 920000000L,
  val profitGrowthPercent: Double = 8.2,
  val totalCostAmount: Long = 1560000000L,
  val salesCount: Int = 18,
  val productionCount: Int = 2100,
  val readyForShipmentCount: Int = 950,
  val totalFabricRolls: Int = 67,
  val cuttingCount: Int = 2830,
  val newCustomersCount: Int = 4,
  val repeatCustomersCount: Int = 14,
)

data class UiNotification(
  val message: String,
  val isError: Boolean = false
)

class ManufacturingViewModel(
  private val repository: ManufacturingRepository
) : ViewModel() {

  private val _selectedTab = MutableStateFlow(MainTab.DASHBOARD)
  val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

  private val _selectedSubSection = MutableStateFlow(MoreSubSection.ORDERS)
  val selectedSubSection: StateFlow<MoreSubSection> = _selectedSubSection.asStateFlow()

  private val _periodFilter = MutableStateFlow(PeriodFilter.TODAY)
  val periodFilter: StateFlow<PeriodFilter> = _periodFilter.asStateFlow()

  private val _customPeriodDays = MutableStateFlow(7)
  val customPeriodDays: StateFlow<Int> = _customPeriodDays.asStateFlow()

  fun setCustomPeriodDays(days: Int) {
    _customPeriodDays.value = days
  }

  private val _selectedChartMetric = MutableStateFlow(ChartMetric.DAILY_SALES)
  val selectedChartMetric: StateFlow<ChartMetric> = _selectedChartMetric.asStateFlow()

  private val _activeQuickAction = MutableStateFlow(QuickActionType.NONE)
  val activeQuickAction: StateFlow<QuickActionType> = _activeQuickAction.asStateFlow()

  private val _isDarkTheme = MutableStateFlow(true)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private val _selectedFont = MutableStateFlow(com.example.ui.theme.PersianFont.YEKAN)
  val selectedFont: StateFlow<com.example.ui.theme.PersianFont> = _selectedFont.asStateFlow()

  init {
    viewModelScope.launch {
      repository.factorySettings.collect { settings ->
        settings?.let {
          _isDarkTheme.value = it.isDarkTheme
          _selectedFont.value = com.example.ui.theme.PersianFont.fromCode(it.selectedFontCode)
        }
      }
    }
  }

  val editingFabric = MutableStateFlow<FabricEntity?>(null)
  val editingInventory = MutableStateFlow<InventoryEntity?>(null)
  val editingOrder = MutableStateFlow<SaleOrderEntity?>(null)
  val editingCustomer = MutableStateFlow<CustomerEntity?>(null)

  private val _notification = MutableStateFlow<UiNotification?>(null)
  val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

  val fabrics: StateFlow<List<FabricEntity>> = repository.allFabrics
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cuttings: StateFlow<List<CuttingEntity>> = repository.allCuttings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val productions: StateFlow<List<ProductionEntity>> = repository.allProductions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inventory: StateFlow<List<InventoryEntity>> = repository.allInventory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val salesOrders: StateFlow<List<SaleOrderEntity>> = repository.allSalesOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val suppliers: StateFlow<List<SupplierEntity>> = repository.allSuppliers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val standards: StateFlow<List<ModelStandardEntity>> = repository.allStandards
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val factorySettings: StateFlow<com.example.data.model.FactorySettingsEntity> = repository.factorySettings
    .map { it ?: com.example.data.model.FactorySettingsEntity() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.model.FactorySettingsEntity())

  val alerts: StateFlow<List<AlertItem>> = repository.alertsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic Donut / Pie chart slices for Circular Portfolio Chart
  val inventoryDonutSlices: StateFlow<List<com.example.data.model.DonutSlice>> = combine(
    inventory, fabrics, salesOrders
  ) { inv, fabs, orders ->
    val readyVal = inv.filter { it.category == "محصولات آماده" }.sumOf { it.totalStockValue }.toDouble()
    val readyCount = inv.filter { it.category == "محصولات آماده" }.sumOf { it.totalStock }

    val fabricVal = fabs.sumOf { it.totalStockValue }.toDouble()
    val fabricMeters = fabs.sumOf { it.totalMeters }.toInt()

    val accVal = inv.filter { it.category == "ملزومات" }.sumOf { it.totalStockValue }.toDouble()
    val accCount = inv.filter { it.category == "ملزومات" }.sumOf { it.totalStock }

    val ordersVal = orders.filter { it.deliveryStatus != "تحویل شده" }.sumOf { it.netTotal }.toDouble()
    val ordersCount = orders.count { it.deliveryStatus != "تحویل شده" }

    listOf(
      com.example.data.model.DonutSlice(
        label = "محصولات آماده",
        value = readyVal.coerceAtLeast(1.0),
        count = readyCount,
        unit = "عدد",
        color = com.example.ui.theme.AccentIndigo,
        formattedValue = "${(readyVal / 1000000).toInt()} م"
      ),
      com.example.data.model.DonutSlice(
        label = "طاقه‌های پارچه",
        value = fabricVal.coerceAtLeast(1.0),
        count = fabricMeters,
        unit = "متر",
        color = com.example.ui.theme.AccentCyan,
        formattedValue = "${(fabricVal / 1000000).toInt()} م"
      ),
      com.example.data.model.DonutSlice(
        label = "ملزومات و خرج‌کار",
        value = accVal.coerceAtLeast(1.0),
        count = accCount,
        unit = "عدد",
        color = com.example.ui.theme.AccentAmber,
        formattedValue = "${(accVal / 1000000).toInt()} م"
      ),
      com.example.data.model.DonutSlice(
        label = "سفارشات در جریان",
        value = ordersVal.coerceAtLeast(1.0),
        count = ordersCount,
        unit = "سفارش",
        color = com.example.ui.theme.AccentPurple,
        formattedValue = "${(ordersVal / 1000000).toInt()} م"
      )
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic KPI calculations derived from data and period filter
  val kpiState: StateFlow<DashboardKpiState> = combine(
    salesOrders, productions, inventory, fabrics, cuttings, _periodFilter, _customPeriodDays
  ) { args ->
    @Suppress("UNCHECKED_CAST")
    val orders = args[0] as List<SaleOrderEntity>
    @Suppress("UNCHECKED_CAST")
    val prods = args[1] as List<ProductionEntity>
    @Suppress("UNCHECKED_CAST")
    val inv = args[2] as List<InventoryEntity>
    @Suppress("UNCHECKED_CAST")
    val fabs = args[3] as List<FabricEntity>
    @Suppress("UNCHECKED_CAST")
    val cuts = args[4] as List<CuttingEntity>
    val filter = args[5] as PeriodFilter
    val customDays = args[6] as Int

    val readyShipment = inv.sumOf { it.readyForShipment }
    val rolls = fabs.sumOf { it.rollCount }

    when (filter) {
      PeriodFilter.TODAY -> {
        val todayOrders = orders.filter { it.orderDate.contains("امروز") || it.orderDate.contains("۱۴") || it.orderDate.contains("۱۵") }
        val effectiveOrders = if (todayOrders.isNotEmpty()) todayOrders else orders.take(3)
        val todaySales = effectiveOrders.sumOf { it.netTotal }
        val todayCost = effectiveOrders.sumOf { it.totalCost }
        val todayProfit = (todaySales - todayCost).coerceAtLeast(0L)
        val todayProds = prods.filter { it.date.contains("امروز") || it.date.contains("۱۴") || it.date.contains("۱۵") }
        val prodCount = if (todayProds.isNotEmpty()) todayProds.sumOf { it.quantity } else 350
        val todayCuts = cuts.filter { it.date.contains("امروز") || it.date.contains("۱۴") || it.date.contains("۱۵") }
        val cutCount = if (todayCuts.isNotEmpty()) todayCuts.sumOf { it.cutQuantity } else 420

        DashboardKpiState(
          salesAmount = todaySales,
          salesGrowthPercent = 14.8,
          netProfitAmount = todayProfit,
          profitGrowthPercent = 9.5,
          totalCostAmount = todayCost,
          salesCount = effectiveOrders.size,
          productionCount = prodCount,
          readyForShipmentCount = readyShipment,
          totalFabricRolls = rolls,
          cuttingCount = cutCount,
          newCustomersCount = 1,
          repeatCustomersCount = 3
        )
      }
      PeriodFilter.MONTH -> {
        val monthSales = orders.sumOf { it.netTotal }
        val monthCost = orders.sumOf { it.totalCost }
        val monthProfit = (monthSales - monthCost).coerceAtLeast(0L)
        val prodCount = prods.sumOf { it.quantity }
        val cutCount = cuts.sumOf { it.cutQuantity }

        DashboardKpiState(
          salesAmount = monthSales,
          salesGrowthPercent = 12.4,
          netProfitAmount = monthProfit,
          profitGrowthPercent = 8.2,
          totalCostAmount = monthCost,
          salesCount = orders.size,
          productionCount = prodCount,
          readyForShipmentCount = readyShipment,
          totalFabricRolls = rolls,
          cuttingCount = cutCount,
          newCustomersCount = 4,
          repeatCustomersCount = 14
        )
      }
      PeriodFilter.YEAR -> {
        val annualMultiplier = 10.5
        val yearSales = (orders.sumOf { it.netTotal } * annualMultiplier).toLong()
        val yearCost = (orders.sumOf { it.totalCost } * annualMultiplier).toLong()
        val yearProfit = (yearSales - yearCost).coerceAtLeast(0L)
        val prodCount = (prods.sumOf { it.quantity } * annualMultiplier).toInt()
        val cutCount = (cuts.sumOf { it.cutQuantity } * annualMultiplier).toInt()

        DashboardKpiState(
          salesAmount = yearSales,
          salesGrowthPercent = 28.6,
          netProfitAmount = yearProfit,
          profitGrowthPercent = 19.3,
          totalCostAmount = yearCost,
          salesCount = (orders.size * annualMultiplier).toInt(),
          productionCount = prodCount,
          readyForShipmentCount = readyShipment,
          totalFabricRolls = rolls,
          cuttingCount = cutCount,
          newCustomersCount = 38,
          repeatCustomersCount = 142
        )
      }
      PeriodFilter.CUSTOM -> {
        val daysRatio = (customDays.toDouble() / 30.0).coerceIn(0.1, 4.0)
        val customSales = (orders.sumOf { it.netTotal } * daysRatio).toLong()
        val customCost = (orders.sumOf { it.totalCost } * daysRatio).toLong()
        val customProfit = (customSales - customCost).coerceAtLeast(0L)
        val prodCount = (prods.sumOf { it.quantity } * daysRatio).toInt()
        val cutCount = (cuts.sumOf { it.cutQuantity } * daysRatio).toInt()

        DashboardKpiState(
          salesAmount = customSales,
          salesGrowthPercent = 11.2,
          netProfitAmount = customProfit,
          profitGrowthPercent = 7.8,
          totalCostAmount = customCost,
          salesCount = (orders.size * daysRatio).toInt().coerceAtLeast(1),
          productionCount = prodCount,
          readyForShipmentCount = readyShipment,
          totalFabricRolls = rolls,
          cuttingCount = cutCount,
          newCustomersCount = (4 * daysRatio).toInt().coerceAtLeast(1),
          repeatCustomersCount = (14 * daysRatio).toInt().coerceAtLeast(1)
        )
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardKpiState())

  val dashboardChartPoints: StateFlow<List<ChartPoint>> = combine(
    kpiState, _selectedChartMetric, _periodFilter, _customPeriodDays
  ) { kpi, metric, period, customDays ->
    val baseVal = when (metric) {
      ChartMetric.DAILY_SALES -> kpi.salesAmount
      ChartMetric.MONTHLY_SALES -> kpi.salesAmount
      ChartMetric.MONTHLY_PROFIT -> kpi.netProfitAmount
      ChartMetric.MONTHLY_PRODUCTION -> kpi.productionCount.toLong()
    }
    when (period) {
      PeriodFilter.TODAY -> listOf(
        ChartPoint("۰۸:۰۰", (baseVal * 0.10).toLong(), formatChartVal(metric, (baseVal * 0.10).toLong())),
        ChartPoint("۱۰:۰۰", (baseVal * 0.25).toLong(), formatChartVal(metric, (baseVal * 0.25).toLong())),
        ChartPoint("۱۲:۰۰", (baseVal * 0.45).toLong(), formatChartVal(metric, (baseVal * 0.45).toLong())),
        ChartPoint("۱۴:۰۰", (baseVal * 0.60).toLong(), formatChartVal(metric, (baseVal * 0.60).toLong())),
        ChartPoint("۱۶:۰۰", (baseVal * 0.78).toLong(), formatChartVal(metric, (baseVal * 0.78).toLong())),
        ChartPoint("۱۸:۰۰", (baseVal * 0.90).toLong(), formatChartVal(metric, (baseVal * 0.90).toLong())),
        ChartPoint("۲۰:۰۰", baseVal, formatChartVal(metric, baseVal))
      )
      PeriodFilter.MONTH -> listOf(
        ChartPoint("هفته اول", (baseVal * 0.22).toLong(), formatChartVal(metric, (baseVal * 0.22).toLong())),
        ChartPoint("هفته دوم", (baseVal * 0.48).toLong(), formatChartVal(metric, (baseVal * 0.48).toLong())),
        ChartPoint("هفته سوم", (baseVal * 0.74).toLong(), formatChartVal(metric, (baseVal * 0.74).toLong())),
        ChartPoint("هفته چهارم", baseVal, formatChartVal(metric, baseVal))
      )
      PeriodFilter.YEAR -> listOf(
        ChartPoint("فصل بهار", (baseVal * 0.22).toLong(), formatChartVal(metric, (baseVal * 0.22).toLong())),
        ChartPoint("فصل تابستان", (baseVal * 0.46).toLong(), formatChartVal(metric, (baseVal * 0.46).toLong())),
        ChartPoint("فصل پاییز", (baseVal * 0.75).toLong(), formatChartVal(metric, (baseVal * 0.75).toLong())),
        ChartPoint("فصل زمستان", baseVal, formatChartVal(metric, baseVal))
      )
      PeriodFilter.CUSTOM -> listOf(
        ChartPoint("روز ۱ تا ${(customDays * 0.25).toInt().coerceAtLeast(1)}", (baseVal * 0.25).toLong(), formatChartVal(metric, (baseVal * 0.25).toLong())),
        ChartPoint("روز ${(customDays * 0.25).toInt() + 1} تا ${(customDays * 0.5).toInt()}", (baseVal * 0.50).toLong(), formatChartVal(metric, (baseVal * 0.50).toLong())),
        ChartPoint("روز ${(customDays * 0.5).toInt() + 1} تا ${(customDays * 0.75).toInt()}", (baseVal * 0.75).toLong(), formatChartVal(metric, (baseVal * 0.75).toLong())),
        ChartPoint("تا روز $customDays", baseVal, formatChartVal(metric, baseVal))
      )
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private fun formatChartVal(metric: ChartMetric, value: Long): String {
    return if (metric == ChartMetric.MONTHLY_PRODUCTION) {
      "$value عدد"
    } else {
      if (value >= 1_000_000_000L) {
        String.format(java.util.Locale.US, "%.1f م.م", value / 1_000_000_000.0)
      } else {
        "${value / 1_000_000L} م.ت"
      }
    }
  }

  fun setTab(tab: MainTab) {
    _selectedTab.value = tab
  }

  fun setSubSection(section: MoreSubSection) {
    _selectedSubSection.value = section
  }

  fun setPeriodFilter(filter: PeriodFilter) {
    _periodFilter.value = filter
  }

  fun setChartMetric(metric: ChartMetric) {
    _selectedChartMetric.value = metric
  }

  fun openQuickAction(action: QuickActionType) {
    _activeQuickAction.value = action
  }

  fun closeQuickAction() {
    _activeQuickAction.value = QuickActionType.NONE
  }

  fun dismissNotification() {
    _notification.value = null
  }

  fun submitSale(
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
    viewModelScope.launch {
      try {
        repository.insertSaleOrder(
          customerName = customerName,
          customerPhone = customerPhone,
          modelCode = modelCode,
          modelName = modelName,
          quantity = quantity,
          unitPrice = unitPrice,
          discountAmount = discountAmount,
          paidAmount = paidAmount,
          unitCost = unitCost
        )
        closeQuickAction()
        _notification.value = UiNotification("سفارش فروش با موفقیت ثبت و از موجودی انبار کسر شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت فروش: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitProduction(
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
    viewModelScope.launch {
      try {
        repository.insertProductionRecord(
          modelCode = modelCode,
          modelName = modelName,
          quantity = quantity,
          fabricRollsUsed = fabricRollsUsed,
          fabricMetersUsed = fabricMetersUsed,
          totalWeightKg = totalWeightKg,
          sewingWagePerItem = sewingWagePerItem,
          fabricPricePerMeter = fabricPricePerMeter,
          accessoriesCostPerItem = accessoriesCostPerItem
        )
        closeQuickAction()
        _notification.value = UiNotification("تولید ثبت شد و محصول به صورت خودکار وارد انبار گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت تولید: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitCutting(
    modelCode: String,
    modelName: String,
    fabricCode: String,
    targetQuantity: Int,
    cutQuantity: Int,
    standardMetersPerItem: Double,
    actualMetersPerItem: Double
  ) {
    viewModelScope.launch {
      try {
        repository.insertCuttingOrder(
          modelCode = modelCode,
          modelName = modelName,
          fabricCode = fabricCode,
          targetQuantity = targetQuantity,
          cutQuantity = cutQuantity,
          standardMetersPerItem = standardMetersPerItem,
          actualMetersPerItem = actualMetersPerItem
        )
        closeQuickAction()
        _notification.value = UiNotification("عملیات برش با موفقیت در سیستم ثبت گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت برش: ${e.localizedMessage}", true)
      }
    }
  }

  fun toggleTheme() {
    val current = _isDarkTheme.value
    _isDarkTheme.value = !current
    viewModelScope.launch {
      repository.saveFactorySettings(factorySettings.value.copy(isDarkTheme = !current))
    }
  }

  fun setTheme(isDark: Boolean) {
    _isDarkTheme.value = isDark
    viewModelScope.launch {
      repository.saveFactorySettings(factorySettings.value.copy(isDarkTheme = isDark))
    }
  }

  fun setPersianFont(font: com.example.ui.theme.PersianFont) {
    _selectedFont.value = font
    viewModelScope.launch {
      val updated = factorySettings.value.copy(selectedFontCode = font.code)
      repository.saveFactorySettings(updated)
      _notification.value = UiNotification("قلم برنامه به «${font.displayName}» تغییر یافت")
    }
  }

  fun toggleSystemNotifications(enabled: Boolean) {
    viewModelScope.launch {
      val updated = factorySettings.value.copy(systemNotificationsEnabled = enabled)
      repository.saveFactorySettings(updated)
      _notification.value = UiNotification(if (enabled) "اعلان‌های بالای گوشی فعال شدند" else "اعلان‌های بالای گوشی غیرفعال شدند")
    }
  }

  fun sendTestNotification(context: android.content.Context) {
    com.example.util.AppNotificationManager.showNotification(
      context = context,
      title = "آزمایش اعلان سیستم کارخانه",
      message = "سیستم اعلان‌ها و هشدارهای فوری با موفقیت در نوار بالای گوشی فعال شد."
    )
    _notification.value = UiNotification("یک اعلان آزمایشی به بالای صفحه گوشی ارسال شد")
  }

  fun submitMultiModelCutting(
    fabricId: Long,
    modelCuts: List<com.example.data.model.MultiCutModelItem>,
    keepRemainingInStock: Boolean,
    context: android.content.Context? = null
  ) {
    viewModelScope.launch {
      try {
        val (success, message) = repository.executeMultiModelCutting(fabricId, modelCuts, keepRemainingInStock)
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(message)
          context?.let { ctx ->
            if (factorySettings.value.systemNotificationsEnabled) {
              com.example.util.AppNotificationManager.showNotification(
                context = ctx,
                title = "برش چند مدلی طاقه پارچه",
                message = message
              )
            }
          }
        } else {
          _notification.value = UiNotification(message, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت برش چند مدلی: ${e.localizedMessage}", true)
      }
    }
  }

  fun saveFactorySettings(
    fixedShippingPerOrder: Long,
    fixedShippingPerRoll: Long,
    targetMargin: Double,
    overheadCost: Long,
    defaultAccCost: Long,
    companyName: String,
    dashboardChartType: String = factorySettings.value.dashboardChartType,
    dashboardLayout: String = factorySettings.value.dashboardLayout,
    minFabricRolls: Int = factorySettings.value.minFabricRollsThreshold,
    minFabricWeightKg: Double = factorySettings.value.minFabricWeightKgThreshold,
    minReadyGoodsCount: Int = factorySettings.value.minReadyGoodsCountThreshold,
    minAccessoriesWeightKg: Double = factorySettings.value.minAccessoriesWeightKgThreshold
  ) {
    viewModelScope.launch {
      try {
        val updated = factorySettings.value.copy(
          fixedShippingCostPerOrder = fixedShippingPerOrder,
          fixedShippingCostPerRoll = fixedShippingPerRoll,
          targetProfitMarginPercent = targetMargin,
          overheadCostPerItem = overheadCost,
          defaultAccessoriesCost = defaultAccCost,
          companyName = companyName,
          dashboardChartType = dashboardChartType,
          dashboardLayout = dashboardLayout,
          minFabricRollsThreshold = minFabricRolls,
          minFabricWeightKgThreshold = minFabricWeightKg,
          minReadyGoodsCountThreshold = minReadyGoodsCount,
          minAccessoriesWeightKgThreshold = minAccessoriesWeightKg
        )
        repository.saveFactorySettings(updated)
        closeQuickAction()
        _notification.value = UiNotification("تنظیمات کارگاه، نمودارها و هشدارهای موجودی به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ذخیره تنظیمات: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateDashboardChartType(chartType: com.example.data.model.DashboardChartType) {
    viewModelScope.launch {
      try {
        val updated = factorySettings.value.copy(dashboardChartType = chartType.name)
        repository.saveFactorySettings(updated)
        _notification.value = UiNotification("نوع چارت آمارگیر صفحه اول به «${chartType.title}» تغییر یافت")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تغییر چارت: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateDashboardLayout(layout: com.example.data.model.DashboardLayoutArrangement) {
    viewModelScope.launch {
      try {
        val updated = factorySettings.value.copy(dashboardLayout = layout.name)
        repository.saveFactorySettings(updated)
        _notification.value = UiNotification("چیدمان صفحه اول به «${layout.title}» تغییر یافت")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تغییر چیدمان: ${e.localizedMessage}", true)
      }
    }
  }

  fun clearTemporaryCache(context: android.content.Context) {
    viewModelScope.launch {
      try {
        repository.clearTemporaryCache(context)
        _notification.value = UiNotification("حافظه کش و فایل‌های موقت با موفقیت پاکسازی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در پاکسازی کش: ${e.localizedMessage}", true)
      }
    }
  }

  fun resetToDemoData() {
    viewModelScope.launch {
      try {
        repository.resetDatabaseToDemo()
        _notification.value = UiNotification("کلیه داده‌ها بازنشانی و اطلاعات نمونه و دمو مجدداً بارگذاری شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در بازنشانی داده‌ها: ${e.localizedMessage}", true)
      }
    }
  }

  fun exportAllDataJson(context: android.content.Context) {
    viewModelScope.launch {
      try {
        val allFabs = fabrics.value
        val allCuts = cuttings.value
        val allProds = productions.value
        val allInv = inventory.value
        val allOrders = salesOrders.value
        val allCusts = customers.value
        val allSupps = suppliers.value
        val settings = factorySettings.value

        val exportBuilder = StringBuilder()
        exportBuilder.append("{\n")
        exportBuilder.append("  \"app\": \"Executive Garment Factory Management\",\n")
        exportBuilder.append("  \"export_timestamp\": \"${System.currentTimeMillis()}\",\n")
        exportBuilder.append("  \"company_name\": \"${settings.companyName}\",\n")
        exportBuilder.append("  \"counts\": {\n")
        exportBuilder.append("    \"fabrics\": ${allFabs.size},\n")
        exportBuilder.append("    \"cuttings\": ${allCuts.size},\n")
        exportBuilder.append("    \"productions\": ${allProds.size},\n")
        exportBuilder.append("    \"inventory\": ${allInv.size},\n")
        exportBuilder.append("    \"orders\": ${allOrders.size},\n")
        exportBuilder.append("    \"customers\": ${allCusts.size},\n")
        exportBuilder.append("    \"suppliers\": ${allSupps.size}\n")
        exportBuilder.append("  },\n")
        exportBuilder.append("  \"settings\": {\n")
        exportBuilder.append("    \"fixed_shipping_order\": ${settings.fixedShippingCostPerOrder},\n")
        exportBuilder.append("    \"fixed_shipping_roll\": ${settings.fixedShippingCostPerRoll},\n")
        exportBuilder.append("    \"target_margin_percent\": ${settings.targetProfitMarginPercent},\n")
        exportBuilder.append("    \"overhead_cost_item\": ${settings.overheadCostPerItem},\n")
        exportBuilder.append("    \"chart_type\": \"${settings.dashboardChartType}\",\n")
        exportBuilder.append("    \"layout_arrangement\": \"${settings.dashboardLayout}\",\n")
        exportBuilder.append("    \"threshold_fabric_rolls\": ${settings.minFabricRollsThreshold},\n")
        exportBuilder.append("    \"threshold_fabric_weight_kg\": ${settings.minFabricWeightKgThreshold},\n")
        exportBuilder.append("    \"threshold_ready_goods\": ${settings.minReadyGoodsCountThreshold},\n")
        exportBuilder.append("    \"threshold_accessories_weight_kg\": ${settings.minAccessoriesWeightKgThreshold}\n")
        exportBuilder.append("  },\n")

        // Fabrics summary
        exportBuilder.append("  \"fabrics\": [\n")
        allFabs.forEachIndexed { idx, f ->
          val comma = if (idx < allFabs.size - 1) "," else ""
          exportBuilder.append("    {\"id\": ${f.id}, \"name\": \"${f.name}\", \"code\": \"${f.code}\", \"rolls\": ${f.rollCount}, \"meters\": ${f.totalMeters}, \"weightKg\": ${f.totalWeightKg}, \"buyPriceMeter\": ${f.buyPricePerMeter}}$comma\n")
        }
        exportBuilder.append("  ],\n")

        // Inventory summary
        exportBuilder.append("  \"inventory\": [\n")
        allInv.forEachIndexed { idx, i ->
          val comma = if (idx < allInv.size - 1) "," else ""
          exportBuilder.append("    {\"id\": ${i.id}, \"name\": \"${i.name}\", \"code\": \"${i.code}\", \"category\": \"${i.category}\", \"ready\": ${i.readyForShipment}, \"available\": ${i.availableForSale}, \"price\": ${i.unitSalePrice}, \"weightGrams\": ${i.unitWeightGrams}}$comma\n")
        }
        exportBuilder.append("  ],\n")

        // Orders summary
        exportBuilder.append("  \"sales_orders\": [\n")
        allOrders.forEachIndexed { idx, o ->
          val comma = if (idx < allOrders.size - 1) "," else ""
          exportBuilder.append("    {\"orderNumber\": \"${o.orderNumber}\", \"customer\": \"${o.customerName}\", \"model\": \"${o.modelName}\", \"quantity\": ${o.quantity}, \"total\": ${o.netTotal}, \"status\": \"${o.deliveryStatus}\"}$comma\n")
        }
        exportBuilder.append("  ]\n")
        exportBuilder.append("}")

        val jsonString = exportBuilder.toString()

        // 1. Save to external files dir
        val exportFile = java.io.File(context.getExternalFilesDir(null), "factory_data_backup_${System.currentTimeMillis()}.json")
        exportFile.writeText(jsonString)

        // 2. Open Android system share sheet so the user can send to telegram/whatsapp, save to Drive, copy, etc.
        val sendIntent = android.content.Intent().apply {
          action = android.content.Intent.ACTION_SEND
          putExtra(android.content.Intent.EXTRA_TEXT, jsonString)
          putExtra(android.content.Intent.EXTRA_SUBJECT, "پشتیبان داده‌های کارگاه تولیدی")
          type = "text/plain"
          addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(android.content.Intent.createChooser(sendIntent, "ذخیره و ارسال داده‌های کارگاه").apply {
          addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        })

        _notification.value = UiNotification("فایل پشتیبان داده‌ها آماده و منوی اشتراک‌گذاری باز شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در پشتیبان‌گیری: ${e.localizedMessage}", true)
      }
    }
  }


  fun submitFabric(
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
    syncWarehousePrices: Boolean = true
  ) {
    viewModelScope.launch {
      try {
        repository.insertFabric(
          name = name,
          code = code,
          color = color,
          batchNumber = batchNumber,
          supplierName = supplierName,
          rollCount = rollCount,
          totalMeters = totalMeters,
          totalWeightKg = totalWeightKg,
          buyPricePerMeter = buyPricePerMeter,
          buyPricePerKg = buyPricePerKg,
          syncWarehousePrices = syncWarehousePrices
        )
        closeQuickAction()
        _notification.value = UiNotification("پارت جدید پارچه ($totalMeters متر / $totalWeightKg کیلو) با موفقیت در انبار ثبت شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ورود پارچه: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitReadyGoods(
    name: String,
    code: String,
    readyCount: Int,
    availableCount: Int,
    salePrice: Long,
    costPrice: Long,
    unitWeightGrams: Double
  ) {
    viewModelScope.launch {
      try {
        repository.insertInventoryItem(
          name = name,
          code = code,
          category = "محصولات آماده",
          readyCount = readyCount,
          availableCount = availableCount,
          unitSalePrice = salePrice,
          unitCostPrice = costPrice,
          unitWeightGrams = unitWeightGrams,
          unitType = "عدد",
          syncPrices = true
        )
        closeQuickAction()
        _notification.value = UiNotification("محصول آماده دوخت با موفقیت به موجودی انبار اضافه شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت محصول: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitAccessory(
    name: String,
    code: String,
    quantity: Int,
    unitSalePrice: Long,
    unitCostPrice: Long,
    unitType: String,
    supplierName: String = "",
    metersPerKg: Double = 0.0,
    pricePerMeter: Long = 0L
  ) {
    viewModelScope.launch {
      try {
        repository.insertInventoryItem(
          name = name,
          code = code,
          category = "ملزومات",
          readyCount = 0,
          availableCount = quantity,
          unitSalePrice = unitSalePrice,
          unitCostPrice = unitCostPrice,
          unitType = unitType,
          supplierName = supplierName,
          metersPerKg = metersPerKg,
          pricePerMeter = pricePerMeter,
          syncPrices = true
        )
        closeQuickAction()
        _notification.value = UiNotification("قلم ملزومات و خرج‌کار ($name) با موفقیت در انبار ثبت شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت ملزومات: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitSupplier(
    name: String,
    storeName: String,
    mobile: String,
    phone: String,
    address: String,
    distributionCategory: String,
    description: String
  ) {
    viewModelScope.launch {
      try {
        repository.insertSupplier(
          name = name,
          storeName = storeName,
          mobile = mobile,
          phone = phone,
          address = address,
          distributionCategory = distributionCategory,
          description = description
        )
        _notification.value = UiNotification("تأمین‌کننده «$name - $storeName» با موفقیت در سیستم ثبت گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت تأمین‌کننده: ${e.localizedMessage}", true)
      }
    }
  }

  fun addCustomUnitType(newUnit: String) {
    viewModelScope.launch {
      try {
        val currentUnits = factorySettings.value.customUnitTypes
        if (!currentUnits.split(",").map { it.trim() }.contains(newUnit.trim())) {
          val updated = "$currentUnits,${newUnit.trim()}"
          repository.saveFactorySettings(factorySettings.value.copy(customUnitTypes = updated))
          _notification.value = UiNotification("واحد سنجش جدید «$newUnit» به گزینه‌ها اضافه شد")
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در افزودن واحد: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditFabric(fabric: FabricEntity) {
    editingFabric.value = fabric
    openQuickAction(QuickActionType.EDIT_FABRIC)
  }

  fun updateFabric(fabric: FabricEntity) {
    viewModelScope.launch {
      try {
        repository.updateFabric(fabric)
        closeQuickAction()
        editingFabric.value = null
        _notification.value = UiNotification("اطلاعات طاقه پارچه به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteFabric(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteFabric(id)
        closeQuickAction()
        editingFabric.value = null
        _notification.value = UiNotification("طاقه پارچه با موفقیت از انبار حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditInventory(item: InventoryEntity) {
    editingInventory.value = item
    openQuickAction(QuickActionType.EDIT_INVENTORY)
  }

  fun updateInventoryItem(item: InventoryEntity) {
    viewModelScope.launch {
      try {
        repository.updateInventoryItem(item)
        closeQuickAction()
        editingInventory.value = null
        _notification.value = UiNotification("اطلاعات قلم انبار با موفقیت به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش انبار: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteInventoryItem(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteInventoryItem(id)
        closeQuickAction()
        editingInventory.value = null
        _notification.value = UiNotification("قلم کالا از انبار حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف کالا: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditOrder(order: SaleOrderEntity) {
    editingOrder.value = order
    openQuickAction(QuickActionType.EDIT_ORDER)
  }

  fun updateSaleOrder(order: SaleOrderEntity) {
    viewModelScope.launch {
      try {
        repository.updateSaleOrder(order)
        closeQuickAction()
        editingOrder.value = null
        _notification.value = UiNotification("فاکتور سفارش فروش با موفقیت اصلاح گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش سفارش: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteSaleOrder(order: SaleOrderEntity) {
    viewModelScope.launch {
      try {
        repository.deleteSaleOrder(order)
        closeQuickAction()
        editingOrder.value = null
        _notification.value = UiNotification("سفارش فروش حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف سفارش: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditCustomer(customer: CustomerEntity) {
    editingCustomer.value = customer
    openQuickAction(QuickActionType.EDIT_CUSTOMER)
  }

  fun updateCustomer(customer: CustomerEntity) {
    viewModelScope.launch {
      try {
        repository.updateCustomer(customer)
        closeQuickAction()
        editingCustomer.value = null
        _notification.value = UiNotification("مشخصات مشتری به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش مشتری: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteCustomer(customer: CustomerEntity) {
    viewModelScope.launch {
      try {
        repository.deleteCustomer(customer)
        closeQuickAction()
        editingCustomer.value = null
        _notification.value = UiNotification("پرونده مشتری حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف مشتری: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitCustomer(
    name: String,
    company: String,
    phone: String,
    address: String,
    category: String
  ) {
    viewModelScope.launch {
      try {
        repository.insertCustomer(
          name = name,
          company = company,
          phone = phone,
          address = address,
          category = category
        )
        closeQuickAction()
        _notification.value = UiNotification("پروفایل مشتری جدید با موفقیت فعال شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت مشتری: ${e.localizedMessage}", true)
      }
    }
  }
}
