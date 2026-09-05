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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChartPoint
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.DarkMinimalChart
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel

enum class AnalyticsSection(val title: String) {
  SALES("فروش"),
  PRODUCTION("تولید"),
  PROFIT("سود و بهای تمام‌شده"),
  CUSTOMERS("مشتریان")
}

@Composable
fun AnalyticsScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  var activeSection by remember { mutableStateOf(AnalyticsSection.SALES) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "مرکز گزارشات و آنالیز مدیریتی",
          style = MaterialTheme.typography.titleLarge,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "تحلیل داده‌محور سود، جریان سفارشات، بهره‌وری تولید و وفاداری مشتریان",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted
        )
      }
    }

    // 2. Section Selector Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.secondaryBg)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        AnalyticsSection.values().forEach { section ->
          val isSelected = section == activeSection
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) customColors.cardElevated else Color.Transparent)
              .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) customColors.border else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
              )
              .clickable { activeSection = section }
              .padding(vertical = 9.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = section.title,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) customColors.textPrimary else customColors.textMuted,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              maxLines = 1
            )
          }
        }
      }
    }

    // 3. Dynamic Section Content
    when (activeSection) {
      AnalyticsSection.SALES -> {
        item {
          SalesAnalyticsView()
        }
      }
      AnalyticsSection.PRODUCTION -> {
        item {
          ProductionAnalyticsView()
        }
      }
      AnalyticsSection.PROFIT -> {
        item {
          ProfitAndCostAnalyticsView()
        }
      }
      AnalyticsSection.CUSTOMERS -> {
        item {
          CustomerAnalyticsView()
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

/**
 * 1. Sales Analytics Module
 */
@Composable
fun SalesAnalyticsView() {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    val monthlyTrend = listOf(
      ChartPoint("فروردین", 16500000000L, "۱۶.۵ م.ت"),
      ChartPoint("اردیبهشت", 19200000000L, "۱۹.۲ م.ت"),
      ChartPoint("خرداد", 22800000000L, "۲۲.۸ م.ت"),
      ChartPoint("تیر", 25400000000L, "۲۵.۴ م.ت"),
      ChartPoint("مرداد", 28900000000L, "۲۸.۹ م.ت"),
      ChartPoint("شهریور", 34200000000L, "۳۴.۲ م.ت")
    )
    DarkMinimalChart(points = monthlyTrend, lineColor = AccentBlue)

    // Sales by Model
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "سهم فروش بر اساس مدل‌های تولیدی",
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )

        ModelShareRow("هودی اورسایز M204", 48, 1480000000L, AccentBlue)
        ModelShareRow("شلوار اسلش کژوال M201", 32, 980000000L, AccentIndigo)
        ModelShareRow("تی‌شرت بیسیک پنبه M108", 20, 620000000L, AccentCyan)
      }
    }

    // Top Purchasing Clients
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "بیشترین حجم خرید مشتریان (عمده)",
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )

        CustomerRankRow(1, "پوشاک سپهر تهران", "۴,۸۵۰,۰۰۰,۰۰۰ تومان", "۱۴ سفارش")
        CustomerRankRow(2, "بوتیک زنجیره‌ای الگانس", "۲,۱۵۰,۰۰۰,۰۰۰ تومان", "۸ سفارش")
        CustomerRankRow(3, "پخش عمده آوا شیراز", "۹۸۰,۰۰۰,۰۰۰ تومان", "۳ سفارش")
      }
    }
  }
}

/**
 * 2. Production Analytics Module
 */
@Composable
fun ProductionAnalyticsView() {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
          text = "وضعیت تحقق برنامه تولید و دوخت",
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )

        ProductionProgressRow("برنامه کل هدف‌گذاری ماه", "۵,۰۰۰ عدد", 1.0f, customColors.textSecondary)
        ProductionProgressRow("برش پارچه (تکمیل شده)", "۴,۲۵۰ عدد", 0.85f, StatusWarning)
        ProductionProgressRow("دوخت و مونتاژ نهایی", "۳,۴۰۰ عدد", 0.68f, AccentBlue)
        ProductionProgressRow("کنترل کیفیت و بسته‌بندی", "۲,۸۰۰ عدد", 0.56f, AccentIndigo)
        ProductionProgressRow("آماده ارسال در انبار", "۲,۱۰۰ عدد", 0.42f, StatusSuccess)
      }
    }

    // Efficiency & Quality KPI Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("شاخص‌های راندمان کارگاهی", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("راندمان خطوط چرخکاری:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("۹۱.۴٪ (عالی)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("نرخ ضایعات برش:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("۲.۱٪ (زیر سقف استاندارد ۳٪)", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
        }
      }
    }
  }
}

/**
 * 3. Profit and Cost Analytics Module
 */
@Composable
fun ProfitAndCostAnalyticsView() {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "ساختار بهای تمام‌شده و هزینه‌های تولید",
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )

        CostTreeItem("هزینه پارچه و مواد اصلی", "۶۱٪", "۹۵۱,۶۰۰,۰۰۰ تومان", AccentBlue)
        CostTreeItem("دستمزد دوخت و چرخکاری", "۲۲٪", "۳۴۳,۲۰۰,۰۰۰ تومان", AccentIndigo)
        CostTreeItem("ملزومات (زیپ، کش، مارک، خرج‌کار)", "۹٪", "۱۴۰,۴۰۰,۰۰۰ تومان", AccentCyan)
        CostTreeItem("باربری و حمل طاقه‌ها", "۵٪", "۷۸,۰۰۰,۰۰۰ تومان", StatusWarning)
        CostTreeItem("ضایعات و اتلاف کارگاه", "۳٪", "۴۶,۸۰۰,۰۰۰ تومان", StatusDanger)
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("سودآوری خالص هر مدل کالا", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هودی کلاه‌دار اورسایز M204", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.SemiBold)
          Text("۳۷۲,۰۰۰ تومان / هر عدد (۴۳.۷٪)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("شلوار اسلش کژوال M201", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.SemiBold)
          Text("۲۷۲,۰۰۰ تومان / هر عدد (۴۳.۸٪)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("تی‌شرت بیسیک پنبه M108", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.SemiBold)
          Text("۲۰۷,۰۰۰ تومان / هر عدد (۵۳.۰٪)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * 4. Customer Analytics Module
 */
@Composable
fun CustomerAnalyticsView() {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "بخش‌بندی مشتریان و نرخ تکرار خرید",
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )

        CustomerRetentionRow("خرید سوم+ (همکاران وفادار عمده)", 14, 0.70f, StatusSuccess)
        CustomerRetentionRow("خرید دوم (در مسیر وفادارسازی)", 4, 0.20f, AccentBlue)
        CustomerRetentionRow("خرید اول (ورودی جدید)", 2, 0.10f, StatusWarning)
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("محبوب‌ترین مدل‌ها میان مشتریان فعال", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("رتبه ۱:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("هودی اورسایز M204 (۸۲٪ ترجیح مشتریان)", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.SemiBold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("رتبه ۲:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("شلوار اسلش کژوال M201 (۶۴٪ ترجیح مشتریان)", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
fun ModelShareRow(name: String, percent: Int, amount: Long, color: Color) {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
      Text(name, style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      Text("$percent٪ • ${CurrencyHelper.formatToman(amount)}", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
    }
    LinearProgressIndicator(
      progress = { percent / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(CircleShape),
      color = color,
      trackColor = customColors.border,
    )
  }
}

@Composable
fun CustomerRankRow(rank: Int, name: String, amount: String, orders: String) {
  val customColors = LocalCustomColors.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(customColors.cardElevated),
        contentAlignment = Alignment.Center
      ) {
        Text("$rank", style = MaterialTheme.typography.labelSmall, color = AccentBlue, fontWeight = FontWeight.Bold)
      }
      Column {
        Text(name, style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.SemiBold)
        Text(orders, style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
      }
    }
    Text(amount, style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
  }
}

@Composable
fun ProductionProgressRow(title: String, quantity: String, progress: Float, color: Color) {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
      Text(title, style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      Text(quantity, style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
    }
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(CircleShape),
      color = color,
      trackColor = customColors.border,
    )
  }
}

@Composable
fun CostTreeItem(title: String, share: String, amount: String, color: Color) {
  val customColors = LocalCustomColors.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(color)
      )
      Text(title, style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
    }
    Text("$share ($amount)", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
  }
}

@Composable
fun CustomerRetentionRow(title: String, count: Int, progress: Float, color: Color) {
  val customColors = LocalCustomColors.current
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
      Text(title, style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      Text("$count خریدار", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
    }
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(CircleShape),
      color = color,
      trackColor = customColors.border,
    )
  }
}
