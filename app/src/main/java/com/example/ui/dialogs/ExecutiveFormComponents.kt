package com.example.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FabricEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.SupplierEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

/**
 * 1. Dialog for selecting an existing item (Fabric, Ready Goods, or Accessory)
 * or creating a new one via "+ ثبت محصول جدید" at the top.
 */
@Composable
fun <T> ItemSelectionPopupDialog(
  title: String,
  items: List<T>,
  onDismiss: () -> Unit,
  onAddNew: () -> Unit,
  onItemSelected: (T) -> Unit,
  itemLabel: (T) -> String,
  itemCode: (T) -> String,
  itemSecondary: (T) -> String = { "" },
  itemPrice: (T) -> Long = { 0L }
) {
  val customColors = LocalCustomColors.current
  var searchQuery by remember { mutableStateOf("") }

  val filteredItems = remember(items, searchQuery) {
    if (searchQuery.isBlank()) items
    else items.filter {
      itemLabel(it).contains(searchQuery, ignoreCase = true) ||
        itemCode(it).contains(searchQuery, ignoreCase = true)
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp)),
      colors = CardDefaults.cardColors(containerColor = customColors.card)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = customColors.textPrimary
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
          }
        }

        // Top "+ ثبت محصول جدید" Button as requested
        Button(
          onClick = {
            onAddNew()
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("btn_add_new_product_top"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Text(
              text = "+ ثبت محصول و کد جدید",
              color = Color.Black,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        // Search Input
        ExecutiveTextField(
          label = "جستجو بر اساس نام یا کد...",
          value = searchQuery,
          onValueChange = { searchQuery = it }
        )

        // Item List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (filteredItems.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "موردی یافت نشد. دکمه «ثبت محصول جدید» را بزنید.",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textMuted
                )
              }
            }
          } else {
            items(filteredItems) { item ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(customColors.cardElevated)
                  .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
                  .clickable {
                    onItemSelected(item)
                    onDismiss()
                  }
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = itemLabel(item),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                      )
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(AccentIndigo.copy(alpha = 0.15f))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = itemCode(item),
                          style = MaterialTheme.typography.labelSmall,
                          color = AccentIndigo,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }

                    val sec = itemSecondary(item)
                    if (sec.isNotBlank()) {
                      Text(
                        text = sec,
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted,
                        fontSize = 11.sp
                      )
                    }
                  }

                  val price = itemPrice(item)
                  if (price > 0L) {
                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = CurrencyHelper.formatToman(price),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess,
                        fontSize = 12.sp
                      )
                      Text(
                        text = "آخرین قیمت",
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.textMuted,
                        fontSize = 9.sp
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 2. Supplier Selection Popup with "+ ثبت تامین‌کننده جدید" button at top.
 */
@Composable
fun SupplierSelectionPopupDialog(
  suppliers: List<SupplierEntity>,
  onDismiss: () -> Unit,
  onAddNewSupplier: () -> Unit,
  onSupplierSelected: (SupplierEntity) -> Unit
) {
  val customColors = LocalCustomColors.current
  var searchQuery by remember { mutableStateOf("") }

  val filteredSuppliers = remember(suppliers, searchQuery) {
    if (searchQuery.isBlank()) suppliers
    else suppliers.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
        it.storeName.contains(searchQuery, ignoreCase = true) ||
        it.distributionCategory.contains(searchQuery, ignoreCase = true)
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp)),
      colors = CardDefaults.cardColors(containerColor = customColors.card)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "انتخاب تأمین‌کننده",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = customColors.textPrimary
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
          }
        }

        // Top "+ ثبت تامین‌کننده جدید" Button as requested
        Button(
          onClick = {
            onAddNewSupplier()
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("btn_add_new_supplier_top"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
            Text(
              text = "+ ثبت تأمین‌کننده جدید (فرم ۷ گانه)",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        // Search Input
        ExecutiveTextField(
          label = "جستجوی تأمین‌کننده، نام فروشگاه یا رسته...",
          value = searchQuery,
          onValueChange = { searchQuery = it }
        )

        // List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (filteredSuppliers.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "تأمین‌کننده‌ای یافت نشد. دکمه بالا را برای ثبت جدید بزنید.",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textMuted
                )
              }
            }
          } else {
            items(filteredSuppliers) { sup ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(customColors.cardElevated)
                  .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
                  .clickable {
                    onSupplierSelected(sup)
                    onDismiss()
                  }
                  .padding(12.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "${sup.name} - ${sup.storeName.ifBlank { "دفتر مرکزی" }}",
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = customColors.textPrimary
                    )
                    if (sup.distributionCategory.isNotBlank()) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(AccentCyan.copy(alpha = 0.15f))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = sup.distributionCategory,
                          style = MaterialTheme.typography.labelSmall,
                          color = AccentCyan,
                          fontSize = 10.sp
                        )
                      }
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "موبایل: ${sup.mobile.ifBlank { "ثبت نشده" }}",
                      style = MaterialTheme.typography.bodySmall,
                      color = customColors.textMuted,
                      fontSize = 11.sp
                    )
                    if (sup.address.isNotBlank()) {
                      Text(
                        text = sup.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted,
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
    }
  }
}

/**
 * 3. Full 7-Field Supplier Registration Dialog:
 * ۱. نام و نام خانوادگی
 * ۲. اسم فروشگاه
 * ۳. شماره موبایل
 * ۴. تلفن ثابت
 * ۵. آدرس
 * ۶. توزیع‌کننده چی هست (زمینه فعالیت)
 * ۷. توضیحات و شرایط
 */
@Composable
fun SupplierRegistrationDialog(
  onDismiss: () -> Unit,
  onSubmit: (name: String, storeName: String, mobile: String, phone: String, address: String, distributionCategory: String, description: String) -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf("") }
  var storeName by remember { mutableStateOf("") }
  var mobile by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var distributionCategory by remember { mutableStateOf("نساجی و پارچه") }
  var description by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp)),
      colors = CardDefaults.cardColors(containerColor = customColors.card)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AccentIndigo.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
            }
            Text(
              text = "ثبت مشخصات تأمین‌کننده جدید",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = customColors.textPrimary
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
          }
        }

        HorizontalDivider(color = customColors.border)

        // 1. نام و نام خانوادگی
        ExecutiveTextField(
          label = "۱. نام و نام خانوادگی مسئول",
          value = name,
          onValueChange = { name = it }
        )

        // 2. اسم فروشگاه
        ExecutiveTextField(
          label = "۲. اسم فروشگاه / کارخانه / دفتر بازرگانی",
          value = storeName,
          onValueChange = { storeName = it }
        )

        // 3 & 4. شماره موبایل و تلفن ثابت
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(modifier = Modifier.weight(1f)) {
            ExecutiveTextField(
              label = "۳. شماره موبایل",
              value = mobile,
              onValueChange = { mobile = it }
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            ExecutiveTextField(
              label = "۴. تلفن ثابت",
              value = phone,
              onValueChange = { phone = it }
            )
          }
        }

        // 5. آدرس
        ExecutiveTextField(
          label = "۵. آدرس کامل فروشگاه یا کارخانه",
          value = address,
          onValueChange = { address = it }
        )

        // 6. توزیع‌کننده چی هست (زمینه فعالیت)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "۶. زمینه توزیع (رسته فعالیت):",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("نساجی و پارچه", "کش و زیپ", "دکمه و یراق", "نخ و لایی").forEach { cat ->
              val isSelected = distributionCategory == cat
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else customColors.cardElevated)
                  .border(1.dp, if (isSelected) AccentCyan else customColors.border, RoundedCornerShape(8.dp))
                  .clickable { distributionCategory = cat }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Text(
                  text = cat,
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = if (isSelected) AccentCyan else customColors.textSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // 7. توضیحات و شرایط پرداخت
        ExecutiveTextField(
          label = "۷. توضیحات (شرایط پرداخت چک/نقد، سقف اعتبار و...)",
          value = description,
          onValueChange = { description = it }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            if (name.isNotBlank() || storeName.isNotBlank()) {
              onSubmit(name, storeName, mobile, phone, address, distributionCategory, description)
              onDismiss()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_save_supplier"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
          enabled = name.isNotBlank() || storeName.isNotBlank()
        ) {
          Text("تأیید و ثبت نهایی تأمین‌کننده", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

/**
 * 4. Price Update Badge: Displays price changes in RED with previous date as requested.
 */
@Composable
fun PriceUpdateBadge(
  currentPrice: Long,
  previousPrice: Long,
  previousDate: String,
  unitLabel: String = "تومان",
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  if (previousPrice > 0L && currentPrice != previousPrice) {
    val diff = currentPrice - previousPrice
    val diffText = if (diff > 0) "+${CurrencyHelper.formatToman(diff)}" else CurrencyHelper.formatToman(diff)

    Box(
      modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(StatusDanger.copy(alpha = 0.12f))
        .border(1.dp, StatusDanger.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
          Text(
            text = "قیمت قبلی: ${CurrencyHelper.formatToman(previousPrice)} ($unitLabel)",
            style = MaterialTheme.typography.labelSmall,
            color = StatusDanger,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        Text(
          text = "تغییر: $diffText (${previousDate.ifBlank { "ثبت گذشته" }})",
          style = MaterialTheme.typography.labelSmall,
          color = StatusDanger,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }
  }
}

/**
 * 5. Mini Trend Line Chart for displaying prices or stock over dates with 3 view toggles:
 * - فقط موجودی
 * - فقط فروش / مصرف
 * - هر دو با هم
 */
enum class ChartViewMode(val title: String) {
  STOCK_ONLY("فقط موجودی"),
  SALES_ONLY("فقط فروش/مصرف"),
  BOTH("هر دو با هم")
}

@Composable
fun MiniTrendLineChart(
  title: String,
  dates: List<String>,
  stockValues: List<Double>,
  salesValues: List<Double>,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  var viewMode by remember { mutableStateOf(ChartViewMode.BOTH) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(customColors.cardElevated)
      .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Header & View Mode Switcher
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.ShowChart, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = customColors.textPrimary
          )
        }

        // 3 view toggles
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          ChartViewMode.values().forEach { mode ->
            val isSelected = viewMode == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else customColors.card)
                .border(1.dp, if (isSelected) AccentCyan else customColors.border, RoundedCornerShape(6.dp))
                .clickable { viewMode = mode }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Text(
                text = mode.title,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = if (isSelected) AccentCyan else customColors.textMuted,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      // Dynamic Canvas Line Chart
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp)
          .padding(vertical = 4.dp)
      ) {
        val w = size.width
        val h = size.height

        val maxVal = maxOf(
          stockValues.maxOrNull() ?: 10.0,
          salesValues.maxOrNull() ?: 10.0
        ).coerceAtLeast(1.0)

        val stepX = if (dates.size > 1) w / (dates.size - 1) else w

        // Draw Stock line if needed
        if (viewMode == ChartViewMode.STOCK_ONLY || viewMode == ChartViewMode.BOTH) {
          val stockPath = Path()
          stockValues.forEachIndexed { i, v ->
            val x = i * stepX
            val y = h - ((v / maxVal) * (h * 0.85f)).toFloat()
            if (i == 0) stockPath.moveTo(x, y) else stockPath.lineTo(x, y)
          }
          drawPath(
            path = stockPath,
            color = AccentCyan,
            style = Stroke(width = 3.dp.toPx())
          )
          // Draw dots
          stockValues.forEachIndexed { i, v ->
            val x = i * stepX
            val y = h - ((v / maxVal) * (h * 0.85f)).toFloat()
            drawCircle(color = AccentCyan, radius = 4.dp.toPx(), center = Offset(x, y))
          }
        }

        // Draw Sales line if needed
        if (viewMode == ChartViewMode.SALES_ONLY || viewMode == ChartViewMode.BOTH) {
          val salesPath = Path()
          salesValues.forEachIndexed { i, v ->
            val x = i * stepX
            val y = h - ((v / maxVal) * (h * 0.85f)).toFloat()
            if (i == 0) salesPath.moveTo(x, y) else salesPath.lineTo(x, y)
          }
          drawPath(
            path = salesPath,
            color = StatusWarning,
            style = Stroke(width = 3.dp.toPx())
          )
          // Draw dots
          salesValues.forEachIndexed { i, v ->
            val x = i * stepX
            val y = h - ((v / maxVal) * (h * 0.85f)).toFloat()
            drawCircle(color = StatusWarning, radius = 4.dp.toPx(), center = Offset(x, y))
          }
        }
      }

      // Date Labels Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        dates.forEach { d ->
          Text(
            text = d,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = customColors.textMuted
          )
        }
      }
    }
  }
}

/**
 * 6. Custom Unit Type Addition Dialog
 */
@Composable
fun CustomUnitTypeDialog(
  onDismiss: () -> Unit,
  onAddUnit: (String) -> Unit
) {
  val customColors = LocalCustomColors.current
  var unitName by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(14.dp)),
      colors = CardDefaults.cardColors(containerColor = customColors.card)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "افزودن واحد سنجش جدید",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = customColors.textPrimary
        )

        ExecutiveTextField(
          label = "نام واحد (مثال: دوک، قواره، بسته، توپی)",
          value = unitName,
          onValueChange = { unitName = it }
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("انصراف")
          }
          Button(
            onClick = {
              if (unitName.isNotBlank()) {
                onAddUnit(unitName.trim())
                onDismiss()
              }
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
            enabled = unitName.isNotBlank()
          ) {
            Text("ثبت واحد", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
