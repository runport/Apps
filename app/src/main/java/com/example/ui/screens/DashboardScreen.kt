package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChartPoint
import com.example.data.model.DashboardChartType
import com.example.data.model.DashboardLayoutArrangement
import com.example.data.model.PeriodFilter
import com.example.ui.components.AlertCenterCard
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.DarkMinimalChart
import com.example.ui.components.ExecutiveDonutChart
import com.example.ui.components.FixedCostBenchmarkCard
import com.example.ui.components.HeroKpiCard
import com.example.ui.components.PeriodSelectorPill
import com.example.ui.components.SecondaryKpiCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.ChartMetric
import com.example.viewmodel.MainTab
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.MoreSubSection
import com.example.viewmodel.QuickActionType

@Composable
fun DashboardScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val kpis by viewModel.kpiState.collectAsState()
  val periodFilter by viewModel.periodFilter.collectAsState()
  val selectedChartMetric by viewModel.selectedChartMetric.collectAsState()
  val alerts by viewModel.alerts.collectAsState()
  val orders by viewModel.salesOrders.collectAsState()
  val isDarkTheme by viewModel.isDarkTheme.collectAsState()
  val donutSlices by viewModel.inventoryDonutSlices.collectAsState()
  val factorySettings by viewModel.factorySettings.collectAsState()

  val isBarChart = factorySettings.dashboardChartType == DashboardChartType.BAR.name
  val isDonutPrimary = factorySettings.dashboardChartType == DashboardChartType.DONUT_PRIMARY.name
  val isAreaLine = factorySettings.dashboardChartType == DashboardChartType.AREA_LINE.name

  val chartPoints by viewModel.dashboardChartPoints.collectAsState()
  val customPeriodDays by viewModel.customPeriodDays.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header Section with Theme Toggle
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = "پنل مدیریت اجرایی کارخانه",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "سلام، مدیر محترم",
            style = MaterialTheme.typography.headlineSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Theme Switcher Button (تغییر تم روشن / تاریک)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
              .clickable { viewModel.toggleTheme() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("toggle_theme_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "تغییر تم",
                tint = if (isDarkTheme) Color(0xFFFBBF24) else AccentIndigo,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (isDarkTheme) "تم روشن" else "تم تاریک",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Date Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = com.example.util.PersianDateHelper.getTodayPersianDate(),
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = customColors.textSecondary,
              fontWeight = FontWeight.Normal
            )
          }
        }
      }
    }

    // 2. Period Filter Selector
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PeriodSelectorPill(
          selectedFilter = periodFilter,
          onFilterSelected = { viewModel.setPeriodFilter(it) },
          modifier = Modifier.fillMaxWidth()
        )

        // Custom period range buttons
        if (periodFilter == PeriodFilter.CUSTOM) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "طول بازه آماری:",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 11.sp
            )
            listOf(7 to "۷ روزه", 15 to "۱۵ روزه", 30 to "۳۰ روزه", 90 to "فصلی (۹۰ روز)").forEach { (days, label) ->
              val isSelected = customPeriodDays == days
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else customColors.card)
                  .border(1.dp, if (isSelected) AccentCyan else customColors.border, RoundedCornerShape(8.dp))
                  .clickable { viewModel.setCustomPeriodDays(days) }
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = if (isSelected) AccentCyan else customColors.textSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }

    // ALERTS_FIRST Layout Arrangement: Show alerts at top of dashboard
    if (factorySettings.dashboardLayout == DashboardLayoutArrangement.ALERTS_FIRST.name && alerts.isNotEmpty()) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "مرکز هشدارهای اولویت‌دار کارخانه",
              style = MaterialTheme.typography.titleMedium,
              color = StatusDanger,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${alerts.size} مورد نیازمند اقدام فوری",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted
            )
          }

          alerts.forEach { alert ->
            AlertCenterCard(alert = alert)
          }
        }
      }
    }

    // CHARTS_FIRST Layout Arrangement: Show Charts directly after header
    if (factorySettings.dashboardLayout == DashboardLayoutArrangement.CHARTS_FIRST.name) {
      if (isDonutPrimary || isAreaLine || isBarChart) {
        item {
          ExecutiveDonutChart(
            title = "تحلیل پورتفوی و چارت گرد انبار",
            subtitle = "سهم ریالی محصولات آماده، طاقه‌های پارچه، ملزومات و سفارشات",
            slices = donutSlices,
            centerTitle = "کل دارایی انبار"
          )
        }
      }

      if (!isDonutPrimary) {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = if (isBarChart) "آمارگیر و نمودار ستونی (میله‌ای)" else "روند تحلیلی و آماری دقیق (مساحتی)",
              style = MaterialTheme.typography.titleMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )

            // Chart Metric Tabs Pill
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(customColors.secondaryBg)
                .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
                .padding(3.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              ChartMetric.values().forEach { metric ->
                val isSelected = metric == selectedChartMetric
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AccentIndigo.copy(alpha = 0.16f) else Color.Transparent)
                    .clickable { viewModel.setChartMetric(metric) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = metric.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) AccentIndigo else customColors.textMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                  )
                }
              }
            }

            DarkMinimalChart(
              points = chartPoints,
              isBarChart = isBarChart,
              lineColor = when (selectedChartMetric) {
                ChartMetric.DAILY_SALES -> AccentBlue
                ChartMetric.MONTHLY_SALES -> AccentIndigo
                ChartMetric.MONTHLY_PROFIT -> StatusSuccess
                ChartMetric.MONTHLY_PRODUCTION -> AccentCyan
              }
            )
          }
        }
      }
    }

    // 3. Hero KPI Cards
    item {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HeroKpiCard(
          title = "مجموع فروش امروز",
          valueText = CurrencyHelper.formatToman(kpis.salesAmount),
          growthText = "↑ ${kpis.salesGrowthPercent}٪",
          isPositive = true,
          icon = Icons.Default.TrendingUp,
          accentColor = AccentIndigo,
          modifier = Modifier.testTag("kpi_sales_hero")
        )

        HeroKpiCard(
          title = "سود خالص دوره",
          valueText = CurrencyHelper.formatToman(kpis.netProfitAmount),
          growthText = "↑ ${kpis.profitGrowthPercent}٪",
          isPositive = true,
          icon = Icons.Default.CheckCircle,
          accentColor = StatusSuccess,
          modifier = Modifier.testTag("kpi_profit_hero")
        )
      }
    }

    // 4. Secondary Compact KPIs Grid (2 Columns)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Row 1: هزینه کل + تعداد فروش
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "هزینه کل تمام‌شده",
              valueText = CurrencyHelper.formatToman(kpis.totalCostAmount),
              subtitle = "پارچه، دوخت و باربری",
              icon = Icons.Default.AttachMoney
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "تعداد فاکتور فروش",
              valueText = "${kpis.salesCount} سفارش",
              subtitle = "میانگین هر سفارش ۱۳۷ م.ت",
              icon = Icons.Default.Receipt
            )
          }
        }

        // Row 2: تعداد تولید + آماده ارسال
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "تعداد تولید کارگاه",
              valueText = "${CurrencyHelper.formatNumber(kpis.productionCount)} عدد",
              subtitle = "۳ خط فعال دوخت",
              icon = Icons.Default.PrecisionManufacturing
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "آماده ارسال در انبار",
              valueText = "${CurrencyHelper.formatNumber(kpis.readyForShipmentCount)} عدد",
              subtitle = "بسته‌بندی و بارکد خورده",
              icon = Icons.Default.LocalShipping
            )
          }
        }

        // Row 3: تعداد طاقه + تعداد برش
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "طاقه‌های موجود پارچه",
              valueText = "${kpis.totalFabricRolls} طاقه",
              subtitle = "معادل ۴,۸۵۰ متر",
              icon = Icons.Default.Inventory
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "تعداد کل برشکاری",
              valueText = "${CurrencyHelper.formatNumber(kpis.cuttingCount)} عدد",
              subtitle = "پیشرفت میانگین ۸۹٪",
              icon = Icons.Default.ContentCut
            )
          }
        }

        // Row 4: مشتری جدید + مشتری تکراری
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "مشتریان جدید",
              valueText = "${kpis.newCustomersCount} خریدار",
              subtitle = "نرخ تبدیل ۷۵٪",
              icon = Icons.Default.PersonAdd
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "مشتریان وفادار / تکراری",
              valueText = "${kpis.repeatCustomersCount} همکار",
              subtitle = "سفارش ۲ و ۳ به بالا",
              icon = Icons.Default.People
            )
          }
        }
      }
    }

    // 5. Standard / Compact placement of Charts
    if (factorySettings.dashboardLayout != DashboardLayoutArrangement.CHARTS_FIRST.name) {
      if (isDonutPrimary || isAreaLine || isBarChart) {
        item {
          ExecutiveDonutChart(
            title = "تحلیل پورتفوی و چارت گرد انبار",
            subtitle = "سهم ریالی محصولات آماده، طاقه‌های پارچه، ملزومات و سفارشات",
            slices = donutSlices,
            centerTitle = "کل دارایی انبار"
          )
        }
      }

      // 6. Fixed Costs Benchmark Card (مبالغ ثابت باربری، حاشیه سود ثابت و سربار)
      item {
        FixedCostBenchmarkCard(
          settings = factorySettings,
          onEditClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) }
        )
      }

      // 7. Interactive Trend Linear / Bar Chart (روند تحلیلی و آماری)
      if (!isDonutPrimary) {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isBarChart) "آمارگیر و نمودار ستونی (میله‌ای)" else "روند تحلیلی و آماری دقیق (مساحتی)",
                style = MaterialTheme.typography.titleMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
            }

            // Chart Metric Tabs Pill
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(customColors.secondaryBg)
                .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
                .padding(3.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              ChartMetric.values().forEach { metric ->
                val isSelected = metric == selectedChartMetric
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AccentIndigo.copy(alpha = 0.16f) else Color.Transparent)
                    .clickable { viewModel.setChartMetric(metric) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = metric.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) AccentIndigo else customColors.textMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                  )
                }
              }
            }

            DarkMinimalChart(
              points = chartPoints,
              isBarChart = isBarChart,
              lineColor = when (selectedChartMetric) {
                ChartMetric.DAILY_SALES -> AccentBlue
                ChartMetric.MONTHLY_SALES -> AccentIndigo
                ChartMetric.MONTHLY_PROFIT -> StatusSuccess
                ChartMetric.MONTHLY_PRODUCTION -> AccentCyan
              }
            )
          }
        }
      }
    } else {
      // In CHARTS_FIRST layout, render FixedCostBenchmarkCard here
      item {
        FixedCostBenchmarkCard(
          settings = factorySettings,
          onEditClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) }
        )
      }
    }

    // 8. Alert Center Section (in standard, compact and charts-first layouts)
    if (factorySettings.dashboardLayout != DashboardLayoutArrangement.ALERTS_FIRST.name) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "مرکز هشدارهای مهم",
              style = MaterialTheme.typography.titleMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${alerts.size} مورد نیازمند توجه",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted
            )
          }

          alerts.forEach { alert ->
            AlertCenterCard(alert = alert)
          }
        }
      }
    }

    // 9. Recent Orders Feed Preview
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "آخرین وضعیت سفارشات",
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "مشاهده همه",
            style = MaterialTheme.typography.labelSmall,
            color = AccentBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            }
          )
        }

        orders.take(3).forEach { order ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
              .clickable { viewModel.startEditOrder(order) }
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = order.orderNumber,
                    style = MaterialTheme.typography.titleSmall,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  StatusChip(status = order.deliveryStatus)
                }
                Text(
                  text = "${order.customerName} • ${order.quantity} عدد ${order.modelName}",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textSecondary
                )
              }

              Text(
                text = CurrencyHelper.formatToman(order.netTotal),
                style = MaterialTheme.typography.titleSmall,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}
