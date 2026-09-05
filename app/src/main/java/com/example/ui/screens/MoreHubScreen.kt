package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.DashboardChartType
import com.example.data.model.DashboardLayoutArrangement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.ProductionEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SupplierEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.FixedCostBenchmarkCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.MoreSubSection
import com.example.viewmodel.QuickActionType

@Composable
fun MoreHubScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val selectedSubSection by viewModel.selectedSubSection.collectAsState()
  val orders by viewModel.salesOrders.collectAsState()
  val productions by viewModel.productions.collectAsState()
  val cuttings by viewModel.cuttings.collectAsState()
  val customers by viewModel.customers.collectAsState()
  val suppliers by viewModel.suppliers.collectAsState()
  val standards by viewModel.standards.collectAsState()
  val isDarkTheme by viewModel.isDarkTheme.collectAsState()
  val selectedFont by viewModel.selectedFont.collectAsState()
  val factorySettings by viewModel.factorySettings.collectAsState()
  val context = LocalContext.current

  var showResetConfirmDialog by remember { mutableStateOf(false) }

  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = {
        Text("هشدار بازنشانی دائم داده‌ها", color = StatusDanger, fontWeight = FontWeight.Bold)
      },
      text = {
        Text(
          "آیا از پاک کردن دائم کلیه داده‌ها و بازنشانی داده‌های نمونه و دمو اولیه کارخانه مطمئن هستید؟ تمام داده‌های فعلی جایگزین خواهند شد.",
          color = customColors.textSecondary,
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            viewModel.resetToDemoData()
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
        ) {
          Text("تأیید و بازنشانی به داده‌های نمونه", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("انصراف", color = customColors.textMuted)
        }
      },
      containerColor = customColors.cardElevated
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "مدیریت ماژولار و تنظیمات کارخانه",
        style = MaterialTheme.typography.titleLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
    }

    // 2. Horizontal Sub-section Switcher
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MoreSubSection.values().forEach { sub ->
          val isSelected = sub == selectedSubSection
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) customColors.cardElevated else customColors.secondaryBg)
              .border(1.dp, if (isSelected) AccentBlue else customColors.border, RoundedCornerShape(20.dp))
              .clickable { viewModel.setSubSection(sub) }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("sub_tab_${sub.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = sub.title,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) customColors.textPrimary else customColors.textMuted,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    // 3. Sub-section Content
    when (selectedSubSection) {
      MoreSubSection.ORDERS -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("لیست سفارشات مشتریان و رهگیری", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.SALE) },
              colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("سفارش جدید", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
        items(orders) { order ->
          OrderTimelineCard(
            order = order,
            onEdit = { viewModel.startEditOrder(order) }
          )
        }
      }

      MoreSubSection.PRODUCTION -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("بچ‌های فعال تولید در کارگاه", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.PRODUCTION) },
              colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("ثبت تولید", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
        items(productions) { prod ->
          ProductionBatchCard(prod = prod)
        }
      }

      MoreSubSection.CUTTING -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("میز برش و پایش مصرف پارچه", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.MULTI_CUT) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("برش چند مدلی", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.CUTTING) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusWarning),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("برش ساده", style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }
        items(cuttings) { cut ->
          CuttingCard(cut = cut)
        }
      }

      MoreSubSection.CUSTOMERS -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("پرونده خریداران و بنکداران", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.CUSTOMER) },
              colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentPurple),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("مشتری جدید", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
        items(customers) { cust ->
          CustomerProfileCard(
            customer = cust,
            onEdit = { viewModel.startEditCustomer(cust) }
          )
        }
      }

      MoreSubSection.SUPPLIERS -> {
        item {
          Text("تأمین‌کنندگان پارچه و نخ", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        items(suppliers) { sup ->
          SupplierCard(sup = sup)
        }
      }

      MoreSubSection.SETTINGS -> {
        // 1. Theme Configuration Setting (تم تاریک و روشن)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentIndigo.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = AccentIndigo
                  )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = "ظاهر برنامه (تم تاریک / روشن)",
                    style = MaterialTheme.typography.titleSmall,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = if (isDarkTheme) "حالت فعال: تم تاریک مدیریتی (Dark Executive)" else "حالت فعال: تم روشن ادیتوریال (Editorial Light)",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                  )
                }
              }

              Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.toggleTheme() },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = AccentIndigo,
                  uncheckedThumbColor = Color.White,
                  uncheckedTrackColor = customColors.border
                )
              )
            }
          }
        }

        // 1.1 Font Selection Setting (انتخاب قلم و فونت فارسی برنامه - یکان، وزیرمتن، شبنم، سیستم)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentIndigo.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Palette, contentDescription = null, tint = AccentIndigo)
                }
                Column {
                  Text(
                    text = "قلم و فونت فارسی برنامه",
                    style = MaterialTheme.typography.titleSmall,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "قلم فعال: ${selectedFont.displayName} - استاندارد و شیک متناسب با گوشی",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                  )
                }
              }

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.example.ui.theme.PersianFont.values().forEach { fontOption ->
                  val isSelected = (selectedFont == fontOption)
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(10.dp))
                      .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.secondaryBg)
                      .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(10.dp))
                      .clickable { viewModel.setPersianFont(fontOption) }
                      .padding(horizontal = 14.dp, vertical = 10.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                          text = fontOption.displayName,
                          style = MaterialTheme.typography.bodyMedium,
                          color = if (isSelected) AccentIndigo else customColors.textPrimary,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                          text = fontOption.description,
                          style = MaterialTheme.typography.bodySmall,
                          color = customColors.textMuted,
                          fontSize = 11.sp
                        )
                      }
                      if (isSelected) {
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentIndigo)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                          Text("فعال", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // 1.2 System Notification Settings (اعلان‌ها و هشدارهای استاتوس‌بار سیستم بالای گوشی)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(AccentBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AccentBlue)
                  }
                  Column {
                    Text(
                      text = "اعلان‌ها در بالای گوشی (Status Bar)",
                      style = MaterialTheme.typography.titleSmall,
                      color = customColors.textPrimary,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "نمایش مستقیم هشدارهای کسری انبار و رویدادها در نوار بالای گوشی",
                      style = MaterialTheme.typography.bodySmall,
                      color = customColors.textMuted
                    )
                  }
                }

                Switch(
                  checked = factorySettings.systemNotificationsEnabled,
                  onCheckedChange = { viewModel.toggleSystemNotifications(it) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = customColors.border
                  )
                )
              }

              // Test notification button
              OutlinedButton(
                onClick = { viewModel.sendTestNotification(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
              ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("تست و ارسال هشدار نمونه به بالای گوشی", style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }

        // 2. Dashboard Chart Selection Setting (انتخاب نوع چارت آمارگیر صفحه اول)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.BarChart, contentDescription = null, tint = AccentBlue)
                }
                Column {
                  Text("نوع چارت آمارگیر صفحه اول", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("انتخاب میان نمودار خطی مساحتی، ستونی میله‌ای، دایره‌ای یا ترکیبی", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DashboardChartType.values().forEach { cType ->
                  val isSelected = factorySettings.dashboardChartType == cType.name
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else customColors.secondaryBg)
                      .border(1.dp, if (isSelected) AccentBlue else customColors.border, RoundedCornerShape(8.dp))
                      .clickable { viewModel.updateDashboardChartType(cType) }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = cType.title,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSelected) AccentBlue else customColors.textMuted,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        // 3. Dashboard Layout Arrangement Setting (انتخاب نوع چیدمان بخش‌ها در صفحات اول)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentCyan.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.ViewQuilt, contentDescription = null, tint = AccentCyan)
                }
                Column {
                  Text("نوع چیدمان بخش‌ها در صفحه اصلی", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("ترتیب اولویت کارت‌ها، شاخص‌ها، چارت‌ها و هشدارها در داشبورد", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DashboardLayoutArrangement.values().forEach { lType ->
                  val isSelected = factorySettings.dashboardLayout == lType.name
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else customColors.secondaryBg)
                      .border(1.dp, if (isSelected) AccentCyan else customColors.border, RoundedCornerShape(8.dp))
                      .clickable { viewModel.updateDashboardLayout(lType) }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = lType.title,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSelected) AccentCyan else customColors.textMuted,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        // 4. Stock Alerts Threshold Summary Setting (حد آستانه هشدار کسری بر اساس تعداد و وزن)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(StatusWarning.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = StatusWarning)
                  }
                  Column {
                    Text("حد آستانه هشدارها و نوتیفیکیشن کسری", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                    Text("بررسی خودکار طاقه (تعداد و وزن)، کار آماده و ملزومات", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                  }
                }

                IconButton(onClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) }) {
                  Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = AccentIndigo)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.secondaryBg)
                    .padding(8.dp)
                ) {
                  Column {
                    Text("حداقل طاقه", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                    Text("${factorySettings.minFabricRollsThreshold} طاقه (${factorySettings.minFabricWeightKgThreshold.toInt()} کیلو)", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  }
                }
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.secondaryBg)
                    .padding(8.dp)
                ) {
                  Column {
                    Text("حداقل کار آماده", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                    Text("${factorySettings.minReadyGoodsCountThreshold} عدد", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  }
                }
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.secondaryBg)
                    .padding(8.dp)
                ) {
                  Column {
                    Text("حداقل ملزومات", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                    Text("${factorySettings.minAccessoriesWeightKgThreshold.toInt()} کیلوگرم", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // 5. Data Management, Cache & Reset Setting (ذخیره داده‌ها، پاک کردن کش و بازنشانی نمونه)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentIndigo.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Settings, contentDescription = null, tint = AccentIndigo)
                }
                Column {
                  Text("مدیریت داده‌ها و نگهداری سیستم", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("پشتیبان‌گیری، پاکسازی حافظه موقت و بازنشانی دیتابیس", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
              }

              // Action Buttons
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // A. Backup / Export all data
                Button(
                  onClick = { viewModel.exportAllDataJson(context) },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                  Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("ذخیره تمام داده‌ها برای برنامه‌های دیگر (JSON / خروجی)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // B. Clear temporary cache
                OutlinedButton(
                  onClick = { viewModel.clearTemporaryCache(context) },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = customColors.textPrimary)
                ) {
                  Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentCyan)
                  Spacer(Modifier.size(8.dp))
                  Text("پاک کردن حافظه کش موقت برنامه", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // C. Permanent reset to demo data
                Button(
                  onClick = { showResetConfirmDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = StatusDanger.copy(alpha = 0.85f))
                ) {
                  Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("پاک کردن دائم داده‌ها و بازنشانی داده‌های نمونه (دمو)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // 6. Fixed Costs & Benchmarks Setting (مبالغ ثابت باربری، حاشیه سود ثابت و سربار)
        item {
          FixedCostBenchmarkCard(
            settings = factorySettings,
            onEditClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) }
          )
        }

        // 7. Standards List Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "استاندارد مدل‌ها و الگوهای مصرف",
              style = MaterialTheme.typography.titleSmall,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) },
              colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(Modifier.size(4.dp))
              Text("تنظیم کلی", style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        items(standards) { standard ->
          ModelStandardCard(standard = standard)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

/**
 * 1. Order Timeline Card with Edit Action
 */
@Composable
fun OrderTimelineCard(
  order: SaleOrderEntity,
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val timelineSteps = listOf("ثبت شده", "در تولید", "در حال تکمیل", "آماده ارسال", "ارسال شده", "تحویل شده")
  val currentIdx = timelineSteps.indexOf(order.deliveryStatus).coerceAtLeast(0)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, if (order.isDelayed) StatusDanger.copy(alpha = 0.4f) else customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onEdit)
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(order.orderNumber, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(order.customerName, style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          StatusChip(status = order.deliveryStatus)
          IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Timeline Steps Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        timelineSteps.forEachIndexed { idx, step ->
          val isDone = idx <= currentIdx
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (isDone) AccentBlue else customColors.border),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(customColors.textPrimary))
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = step,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = if (isDone) customColors.textPrimary else customColors.textMuted
            )
          }
        }
      }

      // Stock Check / Shortage logic
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(customColors.secondaryBg)
          .padding(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "تعداد: ${order.quantity} عدد • ${order.modelName}",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textPrimary
          )
          if (order.isDelayed) {
            Text(
              text = "⚠ ۲ روز تأخیر",
              style = MaterialTheme.typography.labelSmall,
              color = StatusDanger,
              fontWeight = FontWeight.Bold
            )
          } else {
            Text(
              text = "✓ بررسی موجودی: تخصیص یافته",
              style = MaterialTheme.typography.labelSmall,
              color = StatusSuccess
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("مبلغ کل: ${CurrencyHelper.formatToman(order.netTotal)}", style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
        Text("مانده: ${CurrencyHelper.formatToman(order.remainingDebt)}", style = MaterialTheme.typography.bodySmall, color = if (order.remainingDebt > 0) StatusWarning else StatusSuccess, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * 2. Production Batch Card
 */
@Composable
fun ProductionBatchCard(prod: ProductionEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text("کد مدل: ${prod.modelCode}", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text("${prod.modelName} • تاریخ: ${prod.date}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        }
        StatusChip(status = prod.status)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("تعداد کل", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${prod.quantity} عدد", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("طاقه مصرفی", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${prod.fabricRollsUsed} طاقه", style = MaterialTheme.typography.bodySmall, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("متراژ مصرفی", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${prod.fabricMetersUsed.toInt()} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("سود برآوردی", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(prod.totalProfit), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * 3. Cutting Card
 */
@Composable
fun CuttingCard(cut: CuttingEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, if (cut.isAbnormalConsumption) StatusWarning.copy(alpha = 0.4f) else customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(cut.modelName, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        StatusChip(status = cut.status)
      }

      // Progress Bar
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("پیشرفت برش: ${cut.progressPercent}٪", style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text("نیاز: ${cut.targetQuantity} • برش: ${cut.cutQuantity} • کسری: ${cut.shortageQuantity}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
        }
        LinearProgressIndicator(
          progress = { cut.progressPercent / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
          color = if (cut.progressPercent >= 100) StatusSuccess else StatusWarning,
          trackColor = customColors.border
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("مصرف استاندارد: ${cut.standardMetersPerItem} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
          Text("مصرف واقعی: ${cut.actualMetersPerItem} متر", style = MaterialTheme.typography.bodySmall, color = if (cut.isAbnormalConsumption) StatusWarning else customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
      }

      if (cut.isAbnormalConsumption) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.WarningAmber, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(16.dp))
          Text(
            text = "مصرف پارچه ${cut.fabricCode} ۱۸٪ بیشتر از استاندارد است",
            style = MaterialTheme.typography.labelSmall,
            color = StatusWarning,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

/**
 * 4. Customer Profile Card with Edit Action
 */
@Composable
fun CustomerProfileCard(
  customer: CustomerEntity,
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onEdit)
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(customer.name, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text("${customer.company} • ${customer.phone}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          StatusChip(status = customer.tier)
          IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Mini dashboard metrics
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("مجموع خرید", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(customer.totalPurchases), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("تعداد سفارش", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${customer.orderCount} فاکتور", style = MaterialTheme.typography.bodySmall, color = AccentBlue)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("بدهی جاری", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(customer.currentDebt), style = MaterialTheme.typography.bodySmall, color = if (customer.currentDebt > 0) StatusDanger else StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }

      Text(
        text = "مدل‌های محبوب: ${customer.popularModels} • آخرین خرید: ${customer.lastOrderDate}",
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textSecondary
      )
    }
  }
}

/**
 * 5. Supplier Card
 */
@Composable
fun SupplierCard(sup: SupplierEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(sup.name, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        StatusChip(status = sup.supplyType)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("مجموع خرید تا کنون:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(sup.totalPurchases), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      }
      Text("سابقه و روند قیمت: ${sup.priceHistoryNote}", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
    }
  }
}

/**
 * 6. Model Standard Card
 */
@Composable
fun ModelStandardCard(standard: ModelStandardEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(standard.modelName, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("مصرف استاندارد پارچه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text("${standard.standardFabricConsumptionMeters} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("وزن استاندارد هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text("${standard.standardWeightGrams.toInt()} گرم", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("دستمزد پایه دوخت:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(standard.sewingWage), style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("قیمت فروش پیشنهادی:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(standard.suggestedSalePrice), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
      }
    }
  }
}
