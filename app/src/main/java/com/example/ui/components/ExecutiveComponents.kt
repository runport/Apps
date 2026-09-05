package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertItem
import com.example.data.model.AlertType
import com.example.data.model.ChartPoint
import com.example.data.model.PeriodFilter
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueGlow
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderSubtle
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkSecondaryBg
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusInfoBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.DecimalFormat

object CurrencyHelper {
  private val formatter = DecimalFormat("#,###")

  fun formatToman(amount: Long): String {
    return "${formatter.format(amount)} تومان"
  }

  fun formatNumber(number: Number): String {
    return formatter.format(number)
  }
}

/**
 * Period Selector Pill (Editorial Aesthetic: individual rounded-xl pills)
 */
@Composable
fun PeriodSelectorPill(
  selectedFilter: PeriodFilter,
  onFilterSelected: (PeriodFilter) -> Unit,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    PeriodFilter.values().forEach { filter ->
      val isSelected = filter == selectedFilter
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(if (isSelected) AccentIndigo else customColors.secondaryBg)
          .border(
            width = 1.dp,
            color = if (isSelected) AccentIndigo else customColors.border,
            shape = RoundedCornerShape(12.dp)
          )
          .clickable { onFilterSelected(filter) }
          .padding(vertical = 9.dp)
          .testTag("period_tab_${filter.name.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = filter.title,
          style = MaterialTheme.typography.labelSmall,
          color = if (isSelected) Color.White else customColors.textSecondary,
          fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
      }
    }
  }
}

/**
 * Hero KPI Card (Editorial Aesthetic: rounded-3xl with micro bar indicators & glow)
 */
@Composable
fun HeroKpiCard(
  title: String,
  valueText: String,
  growthText: String,
  isPositive: Boolean = true,
  icon: ImageVector? = null,
  accentColor: Color = AccentIndigo,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(24.dp))
      .padding(20.dp)
  ) {
    // Subtle top corner ambient glow
    Box(
      modifier = Modifier
        .size(90.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(AccentIndigo.copy(alpha = if (customColors.isDark) 0.08f else 0.05f), Color.Transparent)
          )
        )
        .align(Alignment.TopStart)
    )

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          color = customColors.textSecondary,
          fontWeight = FontWeight.Medium
        )

        // Pill badge (e.g. ↑ 12.4%)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPositive) StatusSuccessBg else StatusDangerBg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = growthText,
            style = MaterialTheme.typography.labelSmall,
            color = if (isPositive) StatusSuccess else StatusDanger,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Large bold editorial value
      Text(
        text = valueText,
        style = MaterialTheme.typography.headlineLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.5).sp
      )

      // Micro bar chart rhythm (consistent in both Light and Dark themes)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        val barHeights = listOf(0.40f, 0.60f, 0.45f, 0.75f, 0.92f, 0.55f, 0.40f)
        barHeights.forEachIndexed { index, ratio ->
          val isHighlighted = index == 4
          val inactiveBarColor = if (customColors.isDark) customColors.border else customColors.borderSubtle
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .height((50 * ratio).dp)
              .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
              .background(if (isHighlighted) AccentIndigo else inactiveBarColor)
          )
        }
      }
    }
  }
}

/**
 * Secondary Compact KPI Card (Editorial Aesthetic: rounded-2xl with clean hierarchy)
 */
@Composable
fun SecondaryKpiCard(
  title: String,
  valueText: String,
  subtitle: String? = null,
  icon: ImageVector? = null,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted,
          maxLines = 1
        )
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = customColors.textMuted,
            modifier = Modifier.size(16.dp)
          )
        }
      }
      Text(
        text = valueText,
        style = MaterialTheme.typography.titleLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall,
          fontSize = 10.sp,
          color = customColors.textSecondary
        )
      }
    }
  }
}

/**
 * Modern Editorial Minimal Chart with Interactive Touch Tooltip (supports both Light & Dark themes)
 */
@Composable
fun DarkMinimalChart(
  points: List<ChartPoint>,
  lineColor: Color = AccentBlue,
  glowColor: Color = AccentBlueGlow,
  isBarChart: Boolean = false,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current
  var selectedIndex by remember { mutableStateOf<Int?>(null) }

  if (points.isEmpty()) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text("داده‌ای برای نمایش یافت نشد", color = customColors.textMuted)
    }
    return
  }

  val maxVal = remember(points) { points.maxOfOrNull { it.value }?.toDouble() ?: 1.0 }
  val minVal = remember(points, isBarChart) { if (isBarChart) 0.0 else (points.minOfOrNull { it.value }?.toDouble() ?: 0.0) * 0.7 }
  val range = (maxVal - minVal).coerceAtLeast(1.0)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Column {
      // Interactive Tooltip Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val activePoint = selectedIndex?.let { points.getOrNull(it) } ?: points.last()
        Text(
          text = activePoint.label,
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textSecondary
        )
        AnimatedVisibility(
          visible = true,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(lineColor)
            )
            Text(
              text = activePoint.formattedValue,
              style = MaterialTheme.typography.labelLarge,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Canvas Chart
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
          .pointerInput(points, isBarChart) {
            detectTapGestures(
              onPress = { offset ->
                val slotCount = if (isBarChart) points.size.coerceAtLeast(1) else (points.size - 1).coerceAtLeast(1)
                val step = size.width / slotCount
                val index = (offset.x / step).toInt().coerceIn(0, points.size - 1)
                selectedIndex = index
              }
            )
          }
      ) {
        val width = size.width
        val height = size.height
        val gridLineColor = if (customColors.isDark) DarkBorderSubtle else customColors.borderSubtle

        // Draw 3 Subtle Horizontal Grid lines
        val gridLines = 3
        for (i in 0..gridLines) {
          val y = height * (i.toFloat() / gridLines)
          drawLine(
            color = gridLineColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
          )
        }

        if (isBarChart) {
          // Bar Chart Rendering
          val barCount = points.size.coerceAtLeast(1)
          val totalBarWidthSlot = width / barCount
          val barWidth = (totalBarWidthSlot * 0.55f).coerceIn(12.dp.toPx(), 42.dp.toPx())
          val slotBgColor = if (customColors.isDark) DarkBorderSubtle.copy(alpha = 0.35f) else customColors.secondaryBg

          points.forEachIndexed { index, point ->
            val isSelected = (selectedIndex ?: (points.size - 1)) == index
            val normY = ((point.value - minVal) / range).coerceIn(0.0, 1.0)
            val barHeight = (normY * (height - 24.dp.toPx())).toFloat().coerceAtLeast(8.dp.toPx())
            val left = index * totalBarWidthSlot + (totalBarWidthSlot - barWidth) / 2f
            val top = height - barHeight

            // Bar background slot
            drawRoundRect(
              color = slotBgColor,
              topLeft = Offset(left, 0f),
              size = Size(barWidth, height),
              cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Active bar with vertical gradient
            drawRoundRect(
              brush = Brush.verticalGradient(
                colors = listOf(
                  if (isSelected) lineColor else lineColor.copy(alpha = 0.85f),
                  if (isSelected) lineColor.copy(alpha = 0.5f) else lineColor.copy(alpha = 0.25f)
                ),
                startY = top,
                endY = height
              ),
              topLeft = Offset(left, top),
              size = Size(barWidth, barHeight),
              cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            if (isSelected) {
              drawCircle(
                color = customColors.textPrimary,
                radius = 3.dp.toPx(),
                center = Offset(left + barWidth / 2f, top - 6.dp.toPx())
              )
            }
          }
        } else {
          // Area Line Chart Rendering
          val stepX = width / (points.size - 1).coerceAtLeast(1)
          val path = Path()
          val fillPath = Path()
          val coordinates = mutableListOf<Offset>()

          points.forEachIndexed { index, point ->
            val x = index * stepX
            val normY = ((point.value - minVal) / range).coerceIn(0.0, 1.0)
            val y = (height - (normY * (height - 24.dp.toPx())) - 12.dp.toPx()).toFloat()
            coordinates.add(Offset(x, y))

            if (index == 0) {
              path.moveTo(x, y)
              fillPath.moveTo(x, height)
              fillPath.lineTo(x, y)
            } else {
              val prev = coordinates[index - 1]
              val cx1 = (prev.x + x) / 2
              path.cubicTo(cx1, prev.y, cx1, y, x, y)
              fillPath.cubicTo(cx1, prev.y, cx1, y, x, y)
            }
          }

          fillPath.lineTo(width, height)
          fillPath.close()

          // Gradient Area Fill
          drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                lineColor.copy(alpha = if (customColors.isDark) 0.25f else 0.18f),
                lineColor.copy(alpha = 0.04f),
                Color.Transparent
              )
            )
          )

          // Crisp Main Stroke
          drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
          )

          // Point highlight
          val activeIdx = selectedIndex ?: (points.size - 1)
          coordinates.getOrNull(activeIdx)?.let { activeOffset ->
            // Outer Glow
            drawCircle(
              color = glowColor.copy(alpha = 0.35f),
              radius = 9.dp.toPx(),
              center = activeOffset
            )
            // Middle
            drawCircle(
              color = lineColor,
              radius = 5.dp.toPx(),
              center = activeOffset
            )
            // Center Core
            drawCircle(
              color = customColors.textPrimary,
              radius = 2.5.dp.toPx(),
              center = activeOffset
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom X Axis Labels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        points.forEachIndexed { idx, p ->
          val isSelected = (selectedIndex ?: (points.size - 1)) == idx
          Text(
            text = p.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) customColors.textPrimary else customColors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }
  }
}

/**
 * Alert Center Card item with Editorial Aesthetic styling
 */
@Composable
fun AlertCenterCard(
  alert: AlertItem,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current
  val (borderColor, bgColor, iconColor) = when (alert.type) {
    AlertType.DANGER -> Triple(StatusDanger.copy(alpha = 0.25f), StatusDangerBg, StatusDanger)
    AlertType.WARNING -> Triple(StatusWarning.copy(alpha = 0.25f), StatusWarningBg, StatusWarning)
    AlertType.INFO -> Triple(AccentIndigo.copy(alpha = 0.25f), AccentIndigo.copy(alpha = 0.12f), AccentIndigo)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(customColors.card)
      .border(1.dp, borderColor, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(bgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.WarningAmber,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = alert.title,
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = alert.tag,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = customColors.textMuted
          )
        }
        Text(
          text = alert.description,
          style = MaterialTheme.typography.bodySmall,
          fontSize = 11.sp,
          color = customColors.textMuted
        )
      }
    }
  }
}

/**
 * Status Chip with subtle background
 */
@Composable
fun StatusChip(
  status: String,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current
  val (bgColor, textColor, border) = when (status) {
    "تحویل شده", "تکمیل شده" -> Triple(StatusSuccessBg, StatusSuccess, StatusSuccess.copy(alpha = 0.3f))
    "در حال برش", "در تولید", "در حال دوخت", "در حال تکمیل" -> Triple(StatusInfoBg, StatusInfo, StatusInfo.copy(alpha = 0.3f))
    "آماده ارسال", "بسته‌بندی" -> Triple(StatusWarningBg, StatusWarning, StatusWarning.copy(alpha = 0.3f))
    "ثبت شده" -> Triple(customColors.secondaryBg, customColors.textSecondary, customColors.border)
    "کسری" -> Triple(StatusDangerBg, StatusDanger, StatusDanger.copy(alpha = 0.3f))
    else -> Triple(customColors.secondaryBg, customColors.textSecondary, customColors.border)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .border(1.dp, border, RoundedCornerShape(8.dp))
      .padding(horizontal = 9.dp, vertical = 4.dp)
  ) {
    Text(
      text = status,
      style = MaterialTheme.typography.labelSmall,
      color = textColor,
      fontWeight = FontWeight.Medium
    )
  }
}

/**
 * Executive Donut / Circular Chart (Editorial Circular Stock & Portfolio Breakdown)
 */
@Composable
fun ExecutiveDonutChart(
  title: String,
  subtitle: String,
  slices: List<com.example.data.model.DonutSlice>,
  centerTitle: String = "ارزش کل موجودی",
  centerValue: String = "",
  onSliceClick: ((com.example.data.model.DonutSlice) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current
  val total = slices.sumOf { it.value }.coerceAtLeast(1.0)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(24.dp))
      .padding(20.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted,
            fontSize = 11.sp
          )
        }

        // Mini status pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(com.example.ui.theme.AccentIndigo.copy(alpha = 0.12f))
            .border(1.dp, com.example.ui.theme.AccentIndigo.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "تفکیک هوشمند",
            style = MaterialTheme.typography.labelSmall,
            color = com.example.ui.theme.AccentIndigo,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }

      // Circular Donut and Center Text
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(
          modifier = Modifier.size(175.dp)
        ) {
          val strokeWidth = 26.dp.toPx()
          val radius = (size.minDimension - strokeWidth) / 2
          val centerOffset = Offset(size.width / 2, size.height / 2)
          val arcSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
          val arcTopLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)

          // Background track
          drawArc(
            color = customColors.border,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth)
          )

          var currentStartAngle = -90f
          val gapDegrees = if (slices.size > 1) 3f else 0f

          slices.forEach { slice ->
            val sliceRatio = (slice.value / total).toFloat()
            val sweepAngle = (sliceRatio * 360f - gapDegrees).coerceAtLeast(1f)

            drawArc(
              color = slice.color,
              startAngle = currentStartAngle,
              sweepAngle = sweepAngle,
              useCenter = false,
              topLeft = arcTopLeft,
              size = arcSize,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            currentStartAngle += sweepAngle + gapDegrees
          }
        }

        // Center Information
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(2.dp),
          modifier = Modifier.padding(horizontal = 24.dp)
        ) {
          Text(
            text = centerTitle,
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted,
            fontSize = 10.sp
          )
          Text(
            text = centerValue.ifEmpty { CurrencyHelper.formatToman(total.toLong()) },
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }

      // Breakdown Legend Items (Exact Statistics)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        slices.forEach { slice ->
          val percentage = ((slice.value / total) * 100).toInt()

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(customColors.secondaryBg.copy(alpha = 0.5f))
              .clickable { onSliceClick?.invoke(slice) }
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(slice.color)
              )
              Text(
                text = slice.label,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              )
            }

            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${CurrencyHelper.formatNumber(slice.count)} ${slice.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(slice.color.copy(alpha = 0.15f))
                  .padding(horizontal = 7.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "$percentage٪",
                  style = MaterialTheme.typography.labelSmall,
                  color = slice.color,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Fixed Cost and Benchmark Card (باربری، حاشیه سود، سربار و ملزومات)
 */
@Composable
fun FixedCostBenchmarkCard(
  settings: com.example.data.model.FactorySettingsEntity,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val customColors = com.example.ui.theme.LocalCustomColors.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
      .padding(18.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = "هزینه‌های ثابت و مبنای سود کارگاه",
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "محاسبه خودکار در بهای تمام‌شده و سود خالص",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted,
            fontSize = 11.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(com.example.ui.theme.AccentIndigo.copy(alpha = 0.15f))
            .clickable { onEditClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = "ویرایش مبالغ",
            style = MaterialTheme.typography.labelSmall,
            color = com.example.ui.theme.AccentIndigo,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Shipping
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(customColors.secondaryBg)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "باربری ثابت / سفارش",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 10.sp
            )
            Text(
              text = "${CurrencyHelper.formatNumber(settings.fixedShippingCostPerOrder)} ت",
              style = MaterialTheme.typography.bodyMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        // Profit target
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(customColors.secondaryBg)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "حاشیه سود ثابت هدف",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 10.sp
            )
            Text(
              text = "${settings.targetProfitMarginPercent.toInt()}٪ ثابت",
              style = MaterialTheme.typography.bodyMedium,
              color = com.example.ui.theme.StatusSuccess,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Overhead
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(customColors.secondaryBg)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "سربار و اجاره هر کار",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 10.sp
            )
            Text(
              text = "${CurrencyHelper.formatNumber(settings.overheadCostPerItem)} ت",
              style = MaterialTheme.typography.bodyMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        // Trims
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(customColors.secondaryBg)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "هزینه ملزومات پایه",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 10.sp
            )
            Text(
              text = "${CurrencyHelper.formatNumber(settings.defaultAccessoriesCost)} ت",
              style = MaterialTheme.typography.bodyMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

