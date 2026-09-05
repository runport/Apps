package com.example.ui.dialogs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.DashboardChartType
import com.example.data.model.DashboardLayoutArrangement
import com.example.data.model.FabricEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.SaleOrderEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.QuickActionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsModalBottomSheet(
  viewModel: ManufacturingViewModel,
  activeAction: QuickActionType,
  sheetState: SheetState,
  onDismiss: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val editingFabric by viewModel.editingFabric.collectAsState()
  val editingInventory by viewModel.editingInventory.collectAsState()
  val editingOrder by viewModel.editingOrder.collectAsState()
  val editingCustomer by viewModel.editingCustomer.collectAsState()
  val factorySettings by viewModel.factorySettings.collectAsState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = customColors.card,
    scrimColor = Color.Black.copy(alpha = 0.65f),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 10.dp)
          .width(40.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(customColors.border)
      )
    }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      when (activeAction) {
        QuickActionType.NONE -> {
          QuickWarehouseHub(
            onSelectAction = { action -> viewModel.openQuickAction(action) },
            onClose = onDismiss
          )
        }
        QuickActionType.WAREHOUSE_HUB -> {
          QuickWarehouseHub(
            onSelectAction = { action -> viewModel.openQuickAction(action) },
            onClose = onDismiss
          )
        }
        QuickActionType.FABRIC_IN -> {
          QuickFabricForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.READY_GOODS_IN -> {
          QuickReadyGoodsForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.ACCESSORY_IN -> {
          QuickAccessoryForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.CUSTOMER -> {
          QuickCustomerForm(
            onSubmit = { name, company, phone, addr, cat ->
              viewModel.submitCustomer(name, company, phone, addr, cat)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SETTINGS_EDIT -> {
          QuickSettingsForm(
            initialSettings = factorySettings,
            onSubmit = { shipOrder, shipRoll, margin, overhead, accCost, compName, chartType, layout, mRolls, mWeight, mReady, mAccWeight ->
              viewModel.saveFactorySettings(
                fixedShippingPerOrder = shipOrder,
                fixedShippingPerRoll = shipRoll,
                targetMargin = margin,
                overheadCost = overhead,
                defaultAccCost = accCost,
                companyName = compName,
                dashboardChartType = chartType,
                dashboardLayout = layout,
                minFabricRolls = mRolls,
                minFabricWeightKg = mWeight,
                minReadyGoodsCount = mReady,
                minAccessoriesWeightKg = mAccWeight
              )
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SALE -> {
          QuickSaleForm(
            isPreOrder = false,
            onSubmit = { customer, phone, modelCode, modelName, qty, price, discount, paid, cost ->
              viewModel.submitSale(customer, phone, modelCode, modelName, qty, price, discount, paid, cost)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.PRODUCTION -> {
          QuickProductionForm(
            onSubmit = { code, name, qty, rolls, meters, weight, wage, fabricPrice, accCost ->
              viewModel.submitProduction(code, name, qty, rolls, meters, weight, wage, fabricPrice, accCost)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.CUTTING -> {
          QuickCuttingForm(
            onSubmit = { code, name, fabCode, target, cut, std, act ->
              viewModel.submitCutting(code, name, fabCode, target, cut, std, act)
            },
            onOpenMultiCut = { viewModel.openQuickAction(QuickActionType.MULTI_CUT) },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.MULTI_CUT -> {
          MultiModelCuttingSheet(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) },
            onClose = onDismiss
          )
        }
        QuickActionType.EDIT_FABRIC -> {
          editingFabric?.let { fabric ->
            EditFabricForm(
              fabric = fabric,
              onUpdate = { updated -> viewModel.updateFabric(updated) },
              onDelete = { id -> viewModel.deleteFabric(id) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_INVENTORY -> {
          editingInventory?.let { item ->
            EditInventoryForm(
              item = item,
              onUpdate = { updated -> viewModel.updateInventoryItem(updated) },
              onDelete = { id -> viewModel.deleteInventoryItem(id) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_ORDER -> {
          editingOrder?.let { order ->
            EditOrderForm(
              order = order,
              onUpdate = { updated -> viewModel.updateSaleOrder(updated) },
              onDelete = { ord -> viewModel.deleteSaleOrder(ord) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_CUSTOMER -> {
          editingCustomer?.let { customer ->
            EditCustomerForm(
              customer = customer,
              onUpdate = { updated -> viewModel.updateCustomer(updated) },
              onDelete = { cust -> viewModel.deleteCustomer(cust) },
              onBack = onDismiss
            )
          }
        }
      }
    }
    Spacer(modifier = Modifier.height(28.dp))
  }
}

/**
 * Warehouse & Operations Hub (Triggered by the Prominent Center Button)
 */
@Composable
fun QuickWarehouseHub(
  onSelectAction: (QuickActionType) -> Unit,
  onClose: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "مرکز عملیات انبارداری و تولید",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
        Text(
          text = "انبار پارچه، کار آماده، ملزومات، مشتریان و هزینه‌ها",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
      }
    }

    // SECTION 1: Warehouse & Inventory Core Actions
    Text(
      text = "مدیریت انبار و موجودی کالا",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    // 1. Add Fabric Roll (Meters + Kilograms)
    QuickActionTile(
      title = "افزودن طاقه پارچه (متراژ و کیلوگرم)",
      description = "ثبت طاقه‌ها، متراژ، وزن به کیلوگرم و قیمت متری/کیلویی با تبدیل خودکار",
      icon = Icons.Default.Inventory2,
      color = AccentCyan,
      onClick = { onSelectAction(QuickActionType.FABRIC_IN) },
      tag = "action_fabric_in"
    )

    // 2. Add Ready Products (تعداد کار آماده)
    QuickActionTile(
      title = "ثبت تعداد کار آماده به انبار",
      description = "ورود محصولات تکمیل‌شده، وزن به گرم و کیلوگرم، قیمت فروش و تمام‌شده",
      icon = Icons.Default.CheckCircle,
      color = StatusSuccess,
      onClick = { onSelectAction(QuickActionType.READY_GOODS_IN) },
      tag = "action_ready_goods_in"
    )

    // 3. Add Trims & Accessories (ملزومات و خرج‌کار)
    QuickActionTile(
      title = "ورود ملزومات و خرج‌کار",
      description = "ثبت زیپ، دکمه، کش، نخ و لیبل با واحد و قیمت خرید/فروش",
      icon = Icons.Default.Category,
      color = com.example.ui.theme.AccentAmber,
      onClick = { onSelectAction(QuickActionType.ACCESSORY_IN) },
      tag = "action_accessories_in"
    )

    Spacer(modifier = Modifier.height(6.dp))

    // SECTION 2: Commerce & Customers
    Text(
      text = "امور تجاری و مشتریان",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    // 4. Add Customer
    QuickActionTile(
      title = "افزودن مشتری جدید",
      description = "ثبت پرونده مشتری، مشخصات فروشگاه، تلفن و رده خریدار",
      icon = Icons.Default.PersonAdd,
      color = com.example.ui.theme.AccentPurple,
      onClick = { onSelectAction(QuickActionType.CUSTOMER) },
      tag = "action_new_customer"
    )

    // 5. Fixed Costs & Settings
    QuickActionTile(
      title = "تنظیم مبالغ ثابت و سود (باربری و سربار)",
      description = "تعیین هزینه ثابت باربری هر سفارش/طاقه، حاشیه سود ثابت و هزینه سربار",
      icon = Icons.Default.Tune,
      color = AccentIndigo,
      onClick = { onSelectAction(QuickActionType.SETTINGS_EDIT) },
      tag = "action_fixed_costs"
    )

    // 6. Fast Sale
    QuickActionTile(
      title = "ثبت فاکتور فروش فوری",
      description = "محاسبه خودکار مبلغ، تخفیف، بدهی و سود سفارش",
      icon = Icons.Default.ShoppingCart,
      color = AccentBlue,
      onClick = { onSelectAction(QuickActionType.SALE) },
      tag = "action_quick_sale"
    )

    Spacer(modifier = Modifier.height(6.dp))

    // SECTION 3: Manufacturing Operations
    Text(
      text = "عملیات تولید و برش",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    QuickActionTile(
      title = "ثبت عملیات برشکاری ساده",
      description = "ثبت تعداد برش و پایش انحراف مصرف پارچه نسبت به استاندارد",
      icon = Icons.Default.ContentCut,
      color = StatusWarning,
      onClick = { onSelectAction(QuickActionType.CUTTING) },
      tag = "action_quick_cutting"
    )

    QuickActionTile(
      title = "برش چند مدلی از یک طاقه (پاپ‌آپ تفکیک مدل‌ها)",
      description = "تقسیم یک طاقه پارچه بین چندین مدل با مشخص کردن قد کار و تعداد هر مدل",
      icon = Icons.Default.ContentCut,
      color = AccentIndigo,
      onClick = { onSelectAction(QuickActionType.MULTI_CUT) },
      tag = "action_multi_cut"
    )

    QuickActionTile(
      title = "ثبت بچ تولید و کارگاه",
      description = "ورود متراژ و طاقه، انتقال خودکار محصول تکمیل‌شده به انبار",
      icon = Icons.Default.PrecisionManufacturing,
      color = StatusSuccess,
      onClick = { onSelectAction(QuickActionType.PRODUCTION) },
      tag = "action_quick_production"
    )
  }
}

@Composable
fun QuickActionTile(
  title: String,
  description: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit,
  tag: String
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.cardElevated)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(14.dp)
      .testTag(tag)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }
    }
  }
}

/**
 * 1. Fabric Form (With Kilograms & Meters + Auto-Popups & Trend Chart)
 */
@Composable
fun QuickFabricForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingFabrics by viewModel.fabrics.collectAsState()
  val existingSuppliers by viewModel.suppliers.collectAsState()
  val factorySettings by viewModel.factorySettings.collectAsState()

  var showItemPicker by remember { mutableStateOf(false) }
  var showSupplierPicker by remember { mutableStateOf(false) }
  var showSupplierRegisterDialog by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("دورس پنبه ۳ نخ") }
  var code by remember { mutableStateOf("M204") }
  var color by remember { mutableStateOf("سرمه‌ای تیره") }
  var batchNumber by remember { mutableStateOf("PRT-995") }
  var supplierName by remember { mutableStateOf("نساجی تابان کاشان") }
  var rollsText by remember { mutableStateOf("8") }
  var weightKgText by remember { mutableStateOf("150") }
  var metersPerKgText by remember { mutableStateOf("3.0") }
  var metersText by remember { mutableStateOf("450") }
  var buyPricePerMeterText by remember { mutableStateOf("210000") }
  var buyPricePerKgText by remember { mutableStateOf("630000") }
  var previousBuyPricePerMeter by remember { mutableStateOf(0L) }
  var previousPurchaseDate by remember { mutableStateOf("") }
  var syncWarehousePrices by remember { mutableStateOf(true) }

  // Item selection popup with "+ ثبت محصول جدید" at top
  if (showItemPicker) {
    ItemSelectionPopupDialog(
      title = "انتخاب پارچه قبلی یا ثبت جدید",
      items = existingFabrics,
      onDismiss = { showItemPicker = false },
      onAddNew = {
        name = ""
        code = ""
        color = ""
        rollsText = ""
        weightKgText = ""
        metersPerKgText = "3.0"
        metersText = ""
        buyPricePerMeterText = ""
        buyPricePerKgText = ""
        previousBuyPricePerMeter = 0L
        previousPurchaseDate = ""
      },
      onItemSelected = { selected ->
        name = selected.name
        code = selected.code
        color = selected.color
        supplierName = selected.supplierName
        metersPerKgText = if (selected.metersPerKg > 0.0) selected.metersPerKg.toString() else "3.0"
        buyPricePerMeterText = selected.buyPricePerMeter.toString()
        buyPricePerKgText = selected.buyPricePerKg.toString()
        previousBuyPricePerMeter = if (selected.previousBuyPricePerMeter > 0L) selected.previousBuyPricePerMeter else selected.buyPricePerMeter
        previousPurchaseDate = selected.previousPurchaseDate.ifBlank { selected.purchaseDate }
      },
      itemLabel = { "${it.name} - ${it.color}" },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.rollCount} طاقه (${it.totalMeters.toInt()} متر) | تأمین‌کننده: ${it.supplierName}" },
      itemPrice = { it.buyPricePerMeter }
    )
  }

  // Supplier selection popup with "+ ثبت تامین‌کننده جدید" at top
  if (showSupplierPicker) {
    SupplierSelectionPopupDialog(
      suppliers = existingSuppliers,
      onDismiss = { showSupplierPicker = false },
      onAddNewSupplier = { showSupplierRegisterDialog = true },
      onSupplierSelected = { sup ->
        supplierName = "${sup.name} (${sup.storeName.ifBlank { "دفتر مرکزی" }})"
      }
    )
  }

  // 7-Field Supplier Registration Dialog
  if (showSupplierRegisterDialog) {
    SupplierRegistrationDialog(
      onDismiss = { showSupplierRegisterDialog = false },
      onSubmit = { sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc ->
        viewModel.submitSupplier(sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc)
        supplierName = "$sName ($sStore)"
      }
    )
  }

  val rolls = rollsText.toIntOrNull() ?: 0
  val meters = metersText.toDoubleOrNull() ?: 0.0
  val weightKg = weightKgText.toDoubleOrNull() ?: 0.0
  val metersPerKg = metersPerKgText.toDoubleOrNull() ?: (if (weightKg > 0.0) meters / weightKg else 3.0)
  val priceMeter = buyPricePerMeterText.toLongOrNull() ?: 0L
  val priceKg = buyPricePerKgText.toLongOrNull() ?: 0L

  val totalVal = if (priceMeter > 0 && meters > 0) (meters * priceMeter).toLong() else (weightKg * priceKg).toLong()

  // Dynamic cost calculation based on pricing formula:
  // (Fabric Cost + Accessories + Shipping + Printing + Sewing + Fixed Profit)
  val estimatedUnitMeter = 1.4
  val fabricCostPerUnit = (priceMeter * estimatedUnitMeter).toLong()
  val accCost = factorySettings.defaultAccessoriesCost
  val shipCostPerRoll = factorySettings.fixedShippingCostPerRoll
  val estimatedPiecesPerRoll = (metersPerKg * 25 / estimatedUnitMeter).coerceAtLeast(1.0)
  val shippingPerPiece = (shipCostPerRoll / estimatedPiecesPerRoll).toLong()
  val sewingCost = 85000L
  val printingCost = 45000L
  val subTotalCost = fabricCostPerUnit + accCost + shippingPerPiece + sewingCost + printingCost
  val targetProfit = (subTotalCost * (factorySettings.targetProfitMarginPercent / 100.0)).toLong()
  val estimatedSalePrice = subTotalCost + targetProfit

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("افزودن طاقه پارچه (متراژ و کیلوگرم)", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Top action buttons for quick selection
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { showItemPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
          Text("انتخاب پارچه / جدید", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { showSupplierPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
          Text("تأمین‌کننده / ۷ گانه", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "نام/نوع پارچه", value = name, onValueChange = { name = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد پارچه", value = code, onValueChange = { code = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "رنگ", value = color, onValueChange = { color = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شماره پارت", value = batchNumber, onValueChange = { batchNumber = it })
      }
    }

    ExecutiveTextField(
      label = "تأمین‌کننده پارچه (کلیک برای انتخاب یا ثبت ۷ گانه)",
      value = supplierName,
      onValueChange = { supplierName = it }
    )

    // Quantitative metrics: Rolls, Weight, Meters per Kg -> Automatic Total Meters
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد طاقه", value = rollsText, keyboardType = KeyboardType.Number, onValueChange = { rollsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "وزن کل طاقه‌ها (کیلو)",
          value = weightKgText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            weightKgText = it
            val w = it.toDoubleOrNull() ?: 0.0
            val mpk = metersPerKgText.toDoubleOrNull() ?: 0.0
            if (w > 0.0 && mpk > 0.0) {
              metersText = String.format(java.util.Locale.US, "%.1f", w * mpk)
            }
          }
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "متراژ پارچه در کیلو (متر/کیلو)",
          value = metersPerKgText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            metersPerKgText = it
            val mpk = it.toDoubleOrNull() ?: 0.0
            val w = weightKgText.toDoubleOrNull() ?: 0.0
            if (w > 0.0 && mpk > 0.0) {
              metersText = String.format(java.util.Locale.US, "%.1f", w * mpk)
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "متراژ کل طاقه (محاسبه خودکار)",
          value = metersText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            metersText = it
            val m = it.toDoubleOrNull() ?: 0.0
            val w = weightKgText.toDoubleOrNull() ?: 0.0
            if (m > 0.0 && w > 0.0) {
              metersPerKgText = String.format(java.util.Locale.US, "%.2f", m / w)
            }
          }
        )
      }
    }

    // Pricing: Meter and Kilogram
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "قیمت خرید هر متر (تومان)",
          value = buyPricePerMeterText,
          keyboardType = KeyboardType.Number,
          onValueChange = {
            buyPricePerMeterText = it
            val pm = it.toLongOrNull() ?: 0L
            val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
            if (pm > 0L && mpk > 0.0) {
              buyPricePerKgText = (pm * mpk).toLong().toString()
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "قیمت هر کیلو (تومان)",
          value = buyPricePerKgText,
          keyboardType = KeyboardType.Number,
          onValueChange = {
            buyPricePerKgText = it
            val pkg = it.toLongOrNull() ?: 0L
            val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
            if (pkg > 0L && mpk > 0.0) {
              buyPricePerMeterText = (pkg / mpk).toLong().toString()
            }
          }
        )
      }
    }

    // Price Update in RED if price has changed from previous purchase
    PriceUpdateBadge(
      currentPrice = priceMeter,
      previousPrice = previousBuyPricePerMeter,
      previousDate = previousPurchaseDate,
      unitLabel = "هر متر"
    )

    // Line Chart showing Stock and Sales/Usage over dates with 3-way toggle
    MiniTrendLineChart(
      title = "روند موجودی و مصرف طاقه کد $code در تاریخ‌های مختلف",
      dates = listOf("۱۵ اسفند", "۱۸ اسفند", "۲۲ اسفند", "۲۸ اسفند", "امروز"),
      stockValues = listOf(120.0, 180.0, 150.0, 240.0, (rolls * 25.0).coerceAtLeast(60.0)),
      salesValues = listOf(60.0, 95.0, 110.0, 130.0, 85.0)
    )

    // Formula pricing preview & Sync Checkbox
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("ارزش کل خرید پارچه این پارت:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalVal), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای تمام‌شده هر عدد (فرمول کامل کارخانه):", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(subTotalCost), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("قیمت فروش پیشنهادی (با سود ${factorySettings.targetProfitMarginPercent.toInt()}٪):", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(estimatedSalePrice), style = MaterialTheme.typography.bodySmall, color = AccentCyan, fontWeight = FontWeight.Bold)
        }

        HorizontalDivider(color = customColors.border)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { syncWarehousePrices = !syncWarehousePrices },
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Checkbox(
            checked = syncWarehousePrices,
            onCheckedChange = { syncWarehousePrices = it },
            colors = CheckboxDefaults.colors(checkedColor = AccentCyan)
          )
          Text(
            text = "همگام‌سازی خودکار بهای تمام‌شده و قیمت فروش در انبار محصولات آماده",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textPrimary,
            fontSize = 11.sp
          )
        }
      }
    }

    Button(
      onClick = {
        viewModel.submitFabric(
          name = name,
          code = code,
          color = color,
          batchNumber = batchNumber,
          supplierName = supplierName,
          rollCount = rolls,
          totalMeters = meters,
          totalWeightKg = weightKg,
          buyPricePerMeter = priceMeter,
          buyPricePerKg = priceKg,
          syncWarehousePrices = syncWarehousePrices
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_fabric_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
    ) {
      Text("تأیید و ثبت در انبار مواد اولیه", fontWeight = FontWeight.Bold, color = Color.Black)
    }
  }
}

/**
 * 2. Ready Goods Form (تعداد کار آماده به انبار کالا + پاپ‌آپ و نمودار)
 */
@Composable
fun QuickReadyGoodsForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingItems by viewModel.inventory.collectAsState()
  val readyGoodsList = remember(existingItems) {
    existingItems.filter { it.category == "محصولات آماده" || it.readyForShipment > 0 || it.availableForSale > 0 }
  }

  var showItemPicker by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("هودی بیسیک زمستانه دورس") }
  var code by remember { mutableStateOf("HD-204") }
  var readyCountText by remember { mutableStateOf("250") }
  var availableCountText by remember { mutableStateOf("250") }
  var salePriceText by remember { mutableStateOf("720000") }
  var costPriceText by remember { mutableStateOf("435000") }
  var unitWeightGramsText by remember { mutableStateOf("680") }
  var previousCostPrice by remember { mutableStateOf(0L) }
  var previousCostDate by remember { mutableStateOf("") }

  // Item selection popup with "+ ثبت محصول جدید" at top
  if (showItemPicker) {
    ItemSelectionPopupDialog(
      title = "انتخاب محصول آماده یا ثبت جدید",
      items = readyGoodsList,
      onDismiss = { showItemPicker = false },
      onAddNew = {
        name = ""
        code = ""
        readyCountText = ""
        availableCountText = ""
        salePriceText = ""
        costPriceText = ""
        unitWeightGramsText = "500"
        previousCostPrice = 0L
        previousCostDate = ""
      },
      onItemSelected = { selected ->
        name = selected.name
        code = selected.code
        readyCountText = selected.readyForShipment.toString()
        availableCountText = selected.availableForSale.toString()
        salePriceText = selected.unitSalePrice.toString()
        costPriceText = selected.unitCostPrice.toString()
        unitWeightGramsText = selected.unitWeightGrams.toString()
        previousCostPrice = if (selected.previousCostPrice > 0L) selected.previousCostPrice else selected.unitCostPrice
        previousCostDate = selected.previousCostDate
      },
      itemLabel = { it.name },
      itemCode = { it.code },
      itemSecondary = { "موجودی آماده: ${it.readyForShipment} عدد | قیمت فروش: ${CurrencyHelper.formatToman(it.unitSalePrice)}" },
      itemPrice = { it.unitCostPrice }
    )
  }

  val ready = readyCountText.toIntOrNull() ?: 0
  val avail = availableCountText.toIntOrNull() ?: 0
  val sale = salePriceText.toLongOrNull() ?: 0L
  val cost = costPriceText.toLongOrNull() ?: 0L
  val weightGrams = unitWeightGramsText.toDoubleOrNull() ?: 0.0

  val totalWeightKg = (ready * weightGrams) / 1000.0
  val totalStockVal = ready * sale

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ثبت تعداد کار آماده به انبار", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Top action bar: Search / New button
    Button(
      onClick = { showItemPicker = true },
      modifier = Modifier.fillMaxWidth().height(42.dp),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess.copy(alpha = 0.2f))
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Search, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
        Text("انتخاب محصول از انبار / ثبت مدل جدید", color = StatusSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.5f)) {
        ExecutiveTextField(label = "نام محصول آماده", value = name, onValueChange = { name = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد محصول", value = code, onValueChange = { code = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد آماده ارسال", value = readyCountText, keyboardType = KeyboardType.Number, onValueChange = { readyCountText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "موجودی قابل فروش", value = availableCountText, keyboardType = KeyboardType.Number, onValueChange = { availableCountText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت فروش هر عدد (تومان)", value = salePriceText, keyboardType = KeyboardType.Number, onValueChange = { salePriceText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "بهای تمام‌شده هر عدد (تومان)", value = costPriceText, keyboardType = KeyboardType.Number, onValueChange = { costPriceText = it })
      }
    }

    // Price Update badge in RED if cost changed
    PriceUpdateBadge(
      currentPrice = cost,
      previousPrice = previousCostPrice,
      previousDate = previousCostDate,
      unitLabel = "بهای تمام‌شده"
    )

    ExecutiveTextField(
      label = "وزن هر عدد کار (گرم)",
      value = unitWeightGramsText,
      keyboardType = KeyboardType.Decimal,
      onValueChange = { unitWeightGramsText = it }
    )

    // Line Chart showing Stock and Sales over dates with 3 view toggles
    MiniTrendLineChart(
      title = "روند موجودی و فروش مدل $code در تاریخ‌های مختلف",
      dates = listOf("۱۵ اسفند", "۱۸ اسفند", "۲۲ اسفند", "۲۸ اسفند", "امروز"),
      stockValues = listOf(140.0, 200.0, 180.0, 260.0, ready.toDouble().coerceAtLeast(50.0)),
      salesValues = listOf(45.0, 80.0, 120.0, 90.0, 60.0)
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("وزن کل موجودی کار آماده:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(String.format("%.1f کیلوگرم", totalWeightKg), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("ارزش فروش موجودی آماده:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalStockVal), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = { viewModel.submitReadyGoods(name, code, ready, avail, sale, cost, weightGrams) },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_ready_goods_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
    ) {
      Text("تأیید و افزودن به انبار محصولات آماده", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 3. Accessories Form (ملزومات و خرج‌کار + پاپ‌آپ و تأمین‌کننده و واحد)
 */
@Composable
fun QuickAccessoryForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingItems by viewModel.inventory.collectAsState()
  val existingSuppliers by viewModel.suppliers.collectAsState()
  val factorySettings by viewModel.factorySettings.collectAsState()

  val accessoriesList = remember(existingItems) {
    existingItems.filter { it.category == "ملزومات" || it.code.startsWith("ACC") }
  }

  var showItemPicker by remember { mutableStateOf(false) }
  var showSupplierPicker by remember { mutableStateOf(false) }
  var showSupplierRegisterDialog by remember { mutableStateOf(false) }
  var showAddUnitDialog by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("زیپ استخوانی فلزی ۵۰ سانت") }
  var code by remember { mutableStateOf("ACC-ZIP-50") }
  var supplierName by remember { mutableStateOf("بازرگانی یراق البرز") }
  var qtyText by remember { mutableStateOf("1500") }
  var unitSalePriceText by remember { mutableStateOf("38000") }
  var unitCostPriceText by remember { mutableStateOf("26000") }
  var unitType by remember { mutableStateOf("عدد") }
  var metersPerKgText by remember { mutableStateOf("0.0") }
  var pricePerMeterText by remember { mutableStateOf("0") }
  var previousCostPrice by remember { mutableStateOf(0L) }
  var previousCostDate by remember { mutableStateOf("") }

  // Item selection popup with "+ ثبت ملزومات جدید" at top
  if (showItemPicker) {
    ItemSelectionPopupDialog(
      title = "انتخاب ملزومات قبلی یا ثبت جدید",
      items = accessoriesList,
      onDismiss = { showItemPicker = false },
      onAddNew = {
        name = ""
        code = ""
        qtyText = ""
        unitSalePriceText = ""
        unitCostPriceText = ""
        previousCostPrice = 0L
        previousCostDate = ""
      },
      onItemSelected = { selected ->
        name = selected.name
        code = selected.code
        supplierName = selected.supplierName
        unitType = selected.unitType
        qtyText = selected.availableForSale.toString()
        unitSalePriceText = selected.unitSalePrice.toString()
        unitCostPriceText = selected.unitCostPrice.toString()
        previousCostPrice = if (selected.previousCostPrice > 0L) selected.previousCostPrice else selected.unitCostPrice
        previousCostDate = selected.previousCostDate
      },
      itemLabel = { it.name },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.availableForSale} ${it.unitType} | قیمت خرید: ${CurrencyHelper.formatToman(it.unitCostPrice)}" },
      itemPrice = { it.unitCostPrice }
    )
  }

  // Supplier selection popup with "+ ثبت تامین‌کننده جدید" at top
  if (showSupplierPicker) {
    SupplierSelectionPopupDialog(
      suppliers = existingSuppliers,
      onDismiss = { showSupplierPicker = false },
      onAddNewSupplier = { showSupplierRegisterDialog = true },
      onSupplierSelected = { sup ->
        supplierName = "${sup.name} (${sup.storeName.ifBlank { "دفتر مرکزی" }})"
      }
    )
  }

  // 7-Field Supplier Registration Dialog
  if (showSupplierRegisterDialog) {
    SupplierRegistrationDialog(
      onDismiss = { showSupplierRegisterDialog = false },
      onSubmit = { sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc ->
        viewModel.submitSupplier(sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc)
        supplierName = "$sName ($sStore)"
      }
    )
  }

  // Custom Unit Type Dialog
  if (showAddUnitDialog) {
    CustomUnitTypeDialog(
      onDismiss = { showAddUnitDialog = false },
      onAddUnit = { newUnit ->
        viewModel.addCustomUnitType(newUnit)
        unitType = newUnit
      }
    )
  }

  val qty = qtyText.toIntOrNull() ?: 0
  val sale = unitSalePriceText.toLongOrNull() ?: 0L
  val cost = unitCostPriceText.toLongOrNull() ?: 0L
  val metersPerKg = metersPerKgText.toDoubleOrNull() ?: 0.0
  val pricePerMeter = pricePerMeterText.toLongOrNull() ?: 0L

  val availableUnits = remember(factorySettings.customUnitTypes) {
    factorySettings.customUnitTypes.split(",").map { it.trim() }.filter { it.isNotBlank() }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ورود ملزومات و خرج‌کار به انبار", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Top action buttons for quick selection
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { showItemPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Search, contentDescription = null, tint = com.example.ui.theme.AccentAmber, modifier = Modifier.size(16.dp))
          Text("انتخاب ملزومات / جدید", color = com.example.ui.theme.AccentAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { showSupplierPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
          Text("تأمین‌کننده / ۷ گانه", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.5f)) {
        ExecutiveTextField(label = "نام قلم ملزومات (زیپ، دکمه، کش، نخ)", value = name, onValueChange = { name = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد قلم", value = code, onValueChange = { code = it })
      }
    }

    ExecutiveTextField(
      label = "تأمین‌کننده خرج‌کار (کلیک برای انتخاب یا ثبت ۷ گانه)",
      value = supplierName,
      onValueChange = { supplierName = it }
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد / موجودی", value = qtyText, keyboardType = KeyboardType.Number, onValueChange = { qtyText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("واحد سنجش:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
            Text(
              text = "+ افزودن واحد",
              style = MaterialTheme.typography.labelSmall,
              color = AccentCyan,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable { showAddUnitDialog = true }
            )
          }
          ExecutiveTextField(
            label = "واحد ($unitType)",
            value = unitType,
            onValueChange = { unitType = it }
          )
        }
      }
    }

    // Quick Unit Selector Pills
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      availableUnits.take(6).forEach { u ->
        val isSelected = unitType == u
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) com.example.ui.theme.AccentAmber.copy(alpha = 0.2f) else customColors.cardElevated)
            .border(1.dp, if (isSelected) com.example.ui.theme.AccentAmber else customColors.border, RoundedCornerShape(6.dp))
            .clickable { unitType = u }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = u,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (isSelected) com.example.ui.theme.AccentAmber else customColors.textSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }

    // Pricing
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت خرید واحد (تومان)", value = unitCostPriceText, keyboardType = KeyboardType.Number, onValueChange = { unitCostPriceText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت فروش/ارزیابی (تومان)", value = unitSalePriceText, keyboardType = KeyboardType.Number, onValueChange = { unitSalePriceText = it })
      }
    }

    // Price Update badge in RED if cost changed
    PriceUpdateBadge(
      currentPrice = cost,
      previousPrice = previousCostPrice,
      previousDate = previousCostDate,
      unitLabel = "خرید هر $unitType"
    )

    // Line Chart showing Stock and Usage over dates with 3 view toggles
    MiniTrendLineChart(
      title = "روند موجودی و مصرف $name در تاریخ‌های مختلف",
      dates = listOf("۱۵ اسفند", "۱۸ اسفند", "۲۲ اسفند", "۲۸ اسفند", "امروز"),
      stockValues = listOf(500.0, 1200.0, 900.0, 1800.0, qty.toDouble().coerceAtLeast(100.0)),
      salesValues = listOf(150.0, 300.0, 450.0, 200.0, 350.0)
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("ارزش کل خرید ملزومات:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(qty * cost), style = MaterialTheme.typography.bodySmall, color = com.example.ui.theme.AccentAmber, fontWeight = FontWeight.Bold)
      }
    }

    Button(
      onClick = {
        viewModel.submitAccessory(
          name = name,
          code = code,
          quantity = qty,
          unitSalePrice = sale,
          unitCostPrice = cost,
          unitType = unitType,
          supplierName = supplierName,
          metersPerKg = metersPerKg,
          pricePerMeter = pricePerMeter
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_accessory_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber)
    ) {
      Text("تأیید و ثبت در انبار ملزومات", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 4. Fixed Costs & Margins Settings Form (باربری، حاشیه سود ثابت، سربار)
 */
@Composable
fun QuickSettingsForm(
  initialSettings: FactorySettingsEntity,
  onSubmit: (Long, Long, Double, Long, Long, String, String, String, Int, Double, Int, Double) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var shipOrderText by remember { mutableStateOf(initialSettings.fixedShippingCostPerOrder.toString()) }
  var shipRollText by remember { mutableStateOf(initialSettings.fixedShippingCostPerRoll.toString()) }
  var marginText by remember { mutableStateOf(initialSettings.targetProfitMarginPercent.toString()) }
  var overheadText by remember { mutableStateOf(initialSettings.overheadCostPerItem.toString()) }
  var defaultAccText by remember { mutableStateOf(initialSettings.defaultAccessoriesCost.toString()) }
  var companyName by remember { mutableStateOf(initialSettings.companyName) }

  // New chart and layout settings
  var selectedChartType by remember { mutableStateOf(initialSettings.dashboardChartType) }
  var selectedLayout by remember { mutableStateOf(initialSettings.dashboardLayout) }

  // New threshold settings
  var minFabricRollsText by remember { mutableStateOf(initialSettings.minFabricRollsThreshold.toString()) }
  var minFabricWeightText by remember { mutableStateOf(initialSettings.minFabricWeightKgThreshold.toInt().toString()) }
  var minReadyGoodsText by remember { mutableStateOf(initialSettings.minReadyGoodsCountThreshold.toString()) }
  var minAccessoriesWeightText by remember { mutableStateOf(initialSettings.minAccessoriesWeightKgThreshold.toInt().toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("تنظیمات کارگاه، چیدمان و هشدارهای موجودی", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام برند / مجموعه تولیدی", value = companyName, onValueChange = { companyName = it })

    // 1. Chart Type Selector (انتخاب نوع چارت آمارگیر صفحه اول)
    Text("نوع نمودار و چارت آمارگیر صفحه اول", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      DashboardChartType.values().forEach { cType ->
        val isSelected = selectedChartType == cType.name
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentIndigo.copy(alpha = 0.2f) else customColors.card)
            .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(10.dp))
            .clickable { selectedChartType = cType.name }
            .padding(vertical = 10.dp, horizontal = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = cType.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AccentIndigo else customColors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
          )
        }
      }
    }

    // 2. Layout Arrangement Selector (انتخاب نوع چیدمان بخش‌ها در صفحات اول)
    Text("نوع چیدمان بخش‌ها در صفحه اصلی", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      DashboardLayoutArrangement.values().forEach { lType ->
        val isSelected = selectedLayout == lType.name
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else customColors.card)
            .border(1.dp, if (isSelected) AccentBlue else customColors.border, RoundedCornerShape(10.dp))
            .clickable { selectedLayout = lType.name }
            .padding(vertical = 10.dp, horizontal = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = lType.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AccentBlue else customColors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
          )
        }
      }
    }

    HorizontalDivider(color = customColors.borderSubtle, thickness = 1.dp)

    // 3. Stock Alert Thresholds (حد آستانه هشدار کسری موجودی)
    Text("حد آستانه هشدار کسری و نوتیفیکیشن انبار", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار طاقه پارچه (تعداد طاقه)", value = minFabricRollsText, keyboardType = KeyboardType.Number, onValueChange = { minFabricRollsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار وزن پارچه (کیلوگرم)", value = minFabricWeightText, keyboardType = KeyboardType.Number, onValueChange = { minFabricWeightText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار کار آماده (تعداد)", value = minReadyGoodsText, keyboardType = KeyboardType.Number, onValueChange = { minReadyGoodsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار ملزومات (وزن به کیلو)", value = minAccessoriesWeightText, keyboardType = KeyboardType.Number, onValueChange = { minAccessoriesWeightText = it })
      }
    }

    HorizontalDivider(color = customColors.borderSubtle, thickness = 1.dp)

    // 4. Fixed Costs (مبالغ ثابت باربری، سود و سربار)
    Text("مبالغ ثابت باربری و سربار", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "باربری ثابت سفارش (تومان)", value = shipOrderText, keyboardType = KeyboardType.Number, onValueChange = { shipOrderText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "باربری هر طاقه (تومان)", value = shipRollText, keyboardType = KeyboardType.Number, onValueChange = { shipRollText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "حاشیه سود ثابت هدف (٪)", value = marginText, keyboardType = KeyboardType.Decimal, onValueChange = { marginText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "سربار هر کار (تومان)", value = overheadText, keyboardType = KeyboardType.Number, onValueChange = { overheadText = it })
      }
    }

    ExecutiveTextField(
      label = "هزینه پیش‌فرض ملزومات هر کار (تومان)",
      value = defaultAccText,
      keyboardType = KeyboardType.Number,
      onValueChange = { defaultAccText = it }
    )

    Button(
      onClick = {
        val sOrder = shipOrderText.toLongOrNull() ?: initialSettings.fixedShippingCostPerOrder
        val sRoll = shipRollText.toLongOrNull() ?: initialSettings.fixedShippingCostPerRoll
        val mPercent = marginText.toDoubleOrNull() ?: initialSettings.targetProfitMarginPercent
        val ovh = overheadText.toLongOrNull() ?: initialSettings.overheadCostPerItem
        val acc = defaultAccText.toLongOrNull() ?: initialSettings.defaultAccessoriesCost
        val mRolls = minFabricRollsText.toIntOrNull() ?: initialSettings.minFabricRollsThreshold
        val mWeight = minFabricWeightText.toDoubleOrNull() ?: initialSettings.minFabricWeightKgThreshold
        val mReady = minReadyGoodsText.toIntOrNull() ?: initialSettings.minReadyGoodsCountThreshold
        val mAccWeight = minAccessoriesWeightText.toDoubleOrNull() ?: initialSettings.minAccessoriesWeightKgThreshold

        onSubmit(sOrder, sRoll, mPercent, ovh, acc, companyName, selectedChartType, selectedLayout, mRolls, mWeight, mReady, mAccWeight)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("save_settings_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
    ) {
      Text("ذخیره کلیه تنظیمات کارگاه", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 5. Quick Sale Form
 */
@Composable
fun QuickSaleForm(
  isPreOrder: Boolean,
  onSubmit: (String, String, String, String, Int, Long, Long, Long, Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var customerName by remember { mutableStateOf("بوتیک آریا (احمدی)") }
  var customerPhone by remember { mutableStateOf("09121234567") }
  var modelCode by remember { mutableStateOf("HD-204") }
  var modelName by remember { mutableStateOf("هودی دورس ۳ نخ خارخورده") }
  var quantityText by remember { mutableStateOf("50") }
  var unitPriceText by remember { mutableStateOf("680000") }
  var discountText by remember { mutableStateOf("500000") }
  var prePaymentText by remember { mutableStateOf("15000000") }

  val qty = quantityText.toIntOrNull() ?: 0
  val unitPrice = unitPriceText.toLongOrNull() ?: 0L
  val discount = discountText.toLongOrNull() ?: 0L
  val prePaid = prePaymentText.toLongOrNull() ?: 0L

  val subtotal = qty * unitPrice
  val netTotal = (subtotal - discount).coerceAtLeast(0L)
  val remainingDebt = (netTotal - prePaid).coerceAtLeast(0L)
  val unitCost = 425000L
  val totalCost = qty * unitCost
  val profit = (netTotal - totalCost).coerceAtLeast(0L)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (isPreOrder) "ثبت سفارش جدید مشتری" else "ثبت فاکتور فروش فوری",
        style = MaterialTheme.typography.titleMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onBack) {
        Icon(Icons.Default.Close, contentDescription = "بازگشت", tint = customColors.textMuted)
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.2f)) {
        ExecutiveTextField(label = "نام خریدار / فروشگاه", value = customerName, onValueChange = { customerName = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "تلفن", value = customerPhone, keyboardType = KeyboardType.Phone, onValueChange = { customerPhone = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "مدل محصول", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.6f)) {
        ExecutiveTextField(label = "کد", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد (عدد)", value = quantityText, keyboardType = KeyboardType.Number, onValueChange = { quantityText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت واحد (تومان)", value = unitPriceText, keyboardType = KeyboardType.Number, onValueChange = { unitPriceText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تخفیف کل (تومان)", value = discountText, keyboardType = KeyboardType.Number, onValueChange = { discountText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "مبلغ بیعانه / دریافتی", value = prePaymentText, keyboardType = KeyboardType.Number, onValueChange = { prePaymentText = it })
      }
    }

    // Calculations Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مبلغ خالص فاکتور:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(netTotal), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مانده بدهی مشتری:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(remainingDebt), style = MaterialTheme.typography.bodySmall, color = if (remainingDebt > 0) StatusWarning else StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سود پیش‌بینی شده:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(profit), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        onSubmit(customerName, customerPhone, modelCode, modelName, qty, unitPrice, discount, prePaid, totalCost)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_sale_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
    ) {
      Text(if (isPreOrder) "تأیید و صدور سفارش" else "تأیید و صدور فاکتور نهایی", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 6. Quick Production Form
 */
@Composable
fun QuickProductionForm(
  onSubmit: (String, String, Int, Int, Double, Double, Long, Long, Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var modelName by remember { mutableStateOf("هودی کلاه‌دار پاییزه") }
  var modelCode by remember { mutableStateOf("HD-204") }
  var quantityText by remember { mutableStateOf("100") }
  var rollsText by remember { mutableStateOf("4") }
  var metersText by remember { mutableStateOf("185") }
  var weightKgText by remember { mutableStateOf("72") }
  var wagePerItemText by remember { mutableStateOf("85000") }

  val qty = quantityText.toIntOrNull() ?: 0
  val rolls = rollsText.toIntOrNull() ?: 0
  val meters = metersText.toDoubleOrNull() ?: 0.0
  val weightKg = weightKgText.toDoubleOrNull() ?: 0.0
  val wage = wagePerItemText.toLongOrNull() ?: 0L

  val fabricPrice = 210000L
  val accCost = 32000L
  val meterPerItem = if (qty > 0) meters / qty else 0.0
  val weightPerItemGrams = if (qty > 0) (weightKg * 1000) / qty else 0.0
  val fabricCostPerItem = (meterPerItem * fabricPrice).toLong()
  val unitCost = fabricCostPerItem + wage + accCost
  val salePrice = 650000L
  val totalProfit = (qty * (salePrice - unitCost)).coerceAtLeast(0L)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ثبت بچ تولید کارگاه", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.5f)) {
        ExecutiveTextField(label = "مدل کار", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.7f)) {
        ExecutiveTextField(label = "کد مدل", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد تولید (عدد)", value = quantityText, keyboardType = KeyboardType.Number, onValueChange = { quantityText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد طاقه مصرفی", value = rollsText, keyboardType = KeyboardType.Number, onValueChange = { rollsText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "متراژ مصرفی (متر)", value = metersText, keyboardType = KeyboardType.Decimal, onValueChange = { metersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "وزن کل (کیلوگرم)", value = weightKgText, keyboardType = KeyboardType.Decimal, onValueChange = { weightKgText = it })
      }
    }

    ExecutiveTextField(
      label = "دستمزد دوخت هر کار (تومان)",
      value = wagePerItemText,
      keyboardType = KeyboardType.Number,
      onValueChange = { wagePerItemText = it }
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("وزن محاسبه‌شده هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("${weightPerItemGrams.toInt()} گرم", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("قیمت تمام شده هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(unitCost), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سود برآوردی کل بچ:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalProfit), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        onSubmit(modelCode, modelName, qty, rolls, meters, weightKg, wage, fabricPrice, accCost)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_production_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
    ) {
      Text("تأیید تولید و انتقال خودکار به انبار", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 7. Cutting Operation Form
 */
@Composable
fun QuickCuttingForm(
  onSubmit: (String, String, String, Int, Int, Double, Double) -> Unit,
  onOpenMultiCut: () -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var modelName by remember { mutableStateOf("شلوار اسلش پنبه") }
  var modelCode by remember { mutableStateOf("SH-310") }
  var fabricCode by remember { mutableStateOf("M310") }
  var targetQuantityText by remember { mutableStateOf("300") }
  var cutQuantityText by remember { mutableStateOf("290") }
  var standardMetersText by remember { mutableStateOf("1.20") }
  var actualMetersText by remember { mutableStateOf("1.24") }

  val target = targetQuantityText.toIntOrNull() ?: 0
  val cut = cutQuantityText.toIntOrNull() ?: 0
  val std = standardMetersText.toDoubleOrNull() ?: 0.0
  val act = actualMetersText.toDoubleOrNull() ?: 0.0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ثبت عملیات برشکاری", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Banner Button for Multi-Model Cutting from a single fabric roll
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(AccentIndigo.copy(alpha = 0.12f))
        .border(1.dp, AccentIndigo.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
        .clickable(onClick = onOpenMultiCut)
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.ContentCut, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(24.dp))
          Column {
            Text("برش چند مدلی از یک طاقه پارچه", style = MaterialTheme.typography.labelLarge, color = AccentIndigo, fontWeight = FontWeight.Bold)
            Text("تعیین قد و تعداد کار برای چند مدل مختلف از یک طاقه", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
          }
        }
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.5f)) {
        ExecutiveTextField(label = "مدل کار", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.7f)) {
        ExecutiveTextField(label = "کد مدل", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    ExecutiveTextField(label = "کد پارچه مصرفی", value = fabricCode, onValueChange = { fabricCode = it })

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد هدف سفارش", value = targetQuantityText, keyboardType = KeyboardType.Number, onValueChange = { targetQuantityText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد برش واقعی", value = cutQuantityText, keyboardType = KeyboardType.Number, onValueChange = { cutQuantityText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "استاندارد مصرف (متر)", value = standardMetersText, keyboardType = KeyboardType.Decimal, onValueChange = { standardMetersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "مصرف واقعی هر کار (متر)", value = actualMetersText, keyboardType = KeyboardType.Decimal, onValueChange = { actualMetersText = it })
      }
    }

    Button(
      onClick = { onSubmit(modelCode, modelName, fabricCode, target, cut, std, act) },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_cutting_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = StatusWarning)
    ) {
      Text("ثبت عملیات برش", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 8. Customer Form
 */
@Composable
fun QuickCustomerForm(
  onSubmit: (String, String, String, String, String) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf("") }
  var company by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("عمده‌فروش") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ثبت مشتری جدید", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام مسئول خرید", value = name, onValueChange = { name = it })
    ExecutiveTextField(label = "نام شرکت / فروشگاه", value = company, onValueChange = { company = it })
    ExecutiveTextField(label = "شماره تماس", value = phone, keyboardType = KeyboardType.Phone, onValueChange = { phone = it })
    ExecutiveTextField(label = "آدرس", value = address, onValueChange = { address = it })
    ExecutiveTextField(label = "رده خریدار (عمده‌فروش / آنلاین / بوتیک)", value = category, onValueChange = { category = it })

    Button(
      onClick = {
        if (name.isNotBlank()) {
          onSubmit(name, company, phone, address, category)
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_customer_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentPurple)
    ) {
      Text("ایجاد پرونده مشتری", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 9. Edit Fabric Dialog (کد جوری بنویس که قابل ادیت کردن باشه)
 */
@Composable
fun EditFabricForm(
  fabric: FabricEntity,
  onUpdate: (FabricEntity) -> Unit,
  onDelete: (Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf(fabric.name) }
  var color by remember { mutableStateOf(fabric.color) }
  var rollsText by remember { mutableStateOf(fabric.rollCount.toString()) }
  var metersText by remember { mutableStateOf(fabric.totalMeters.toString()) }
  var weightKgText by remember { mutableStateOf(fabric.totalWeightKg.toString()) }
  var priceMeterText by remember { mutableStateOf(fabric.buyPricePerMeter.toString()) }
  var priceKgText by remember { mutableStateOf(fabric.buyPricePerKg.toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش اطلاعات طاقه پارچه", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام پارچه", value = name, onValueChange = { name = it })
    ExecutiveTextField(label = "رنگ", value = color, onValueChange = { color = it })

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد طاقه", value = rollsText, keyboardType = KeyboardType.Number, onValueChange = { rollsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "متراژ کل (متر)", value = metersText, keyboardType = KeyboardType.Decimal, onValueChange = { metersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "وزن کل (کیلو)", value = weightKgText, keyboardType = KeyboardType.Decimal, onValueChange = { weightKgText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت متر (تومان)", value = priceMeterText, keyboardType = KeyboardType.Number, onValueChange = { priceMeterText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت کیلو (تومان)", value = priceKgText, keyboardType = KeyboardType.Number, onValueChange = { priceKgText = it })
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val updated = fabric.copy(
            name = name,
            color = color,
            rollCount = rollsText.toIntOrNull() ?: fabric.rollCount,
            totalMeters = metersText.toDoubleOrNull() ?: fabric.totalMeters,
            totalWeightKg = weightKgText.toDoubleOrNull() ?: fabric.totalWeightKg,
            buyPricePerMeter = priceMeterText.toLongOrNull() ?: fabric.buyPricePerMeter,
            buyPricePerKg = priceKgText.toLongOrNull() ?: fabric.buyPricePerKg
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ذخیره تغییرات", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(fabric.id) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

/**
 * 10. Edit Inventory Dialog
 */
@Composable
fun EditInventoryForm(
  item: InventoryEntity,
  onUpdate: (InventoryEntity) -> Unit,
  onDelete: (Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf(item.name) }
  var readyCountText by remember { mutableStateOf(item.readyForShipment.toString()) }
  var availableCountText by remember { mutableStateOf(item.availableForSale.toString()) }
  var salePriceText by remember { mutableStateOf(item.unitSalePrice.toString()) }
  var costPriceText by remember { mutableStateOf(item.unitCostPrice.toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش قلم انبار (${item.code})", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام کالا", value = name, onValueChange = { name = it })

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "آماده ارسال", value = readyCountText, keyboardType = KeyboardType.Number, onValueChange = { readyCountText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قابل فروش", value = availableCountText, keyboardType = KeyboardType.Number, onValueChange = { availableCountText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت فروش (تومان)", value = salePriceText, keyboardType = KeyboardType.Number, onValueChange = { salePriceText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "بهای تمام‌شده (تومان)", value = costPriceText, keyboardType = KeyboardType.Number, onValueChange = { costPriceText = it })
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val updated = item.copy(
            name = name,
            readyForShipment = readyCountText.toIntOrNull() ?: item.readyForShipment,
            availableForSale = availableCountText.toIntOrNull() ?: item.availableForSale,
            unitSalePrice = salePriceText.toLongOrNull() ?: item.unitSalePrice,
            unitCostPrice = costPriceText.toLongOrNull() ?: item.unitCostPrice
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ذخیره تغییرات", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(item.id) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

/**
 * 11. Edit Order Form
 */
@Composable
fun EditOrderForm(
  order: SaleOrderEntity,
  onUpdate: (SaleOrderEntity) -> Unit,
  onDelete: (SaleOrderEntity) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var deliveryStatus by remember { mutableStateOf(order.deliveryStatus) }
  var paidAmountText by remember { mutableStateOf(order.paidAmount.toString()) }
  var discountText by remember { mutableStateOf(order.discountAmount.toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش سفارش ${order.orderNumber}", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "وضعیت سفارش (ثبت شده / در حال دوخت / آماده ارسال / تحویل شده)", value = deliveryStatus, onValueChange = { deliveryStatus = it })
    ExecutiveTextField(label = "مبلغ پرداختی مشتری (تومان)", value = paidAmountText, keyboardType = KeyboardType.Number, onValueChange = { paidAmountText = it })
    ExecutiveTextField(label = "تخفیف (تومان)", value = discountText, keyboardType = KeyboardType.Number, onValueChange = { discountText = it })

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val paid = paidAmountText.toLongOrNull() ?: order.paidAmount
          val disc = discountText.toLongOrNull() ?: order.discountAmount
          val updated = order.copy(
            deliveryStatus = deliveryStatus,
            paidAmount = paid,
            discountAmount = disc
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ثبت اصلاحات فاکتور", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(order) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

/**
 * 12. Edit Customer Form
 */
@Composable
fun EditCustomerForm(
  customer: CustomerEntity,
  onUpdate: (CustomerEntity) -> Unit,
  onDelete: (CustomerEntity) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var phone by remember { mutableStateOf(customer.phone) }
  var address by remember { mutableStateOf(customer.address) }
  var tier by remember { mutableStateOf(customer.tier) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش پرونده ${customer.name}", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "شماره تماس", value = phone, keyboardType = KeyboardType.Phone, onValueChange = { phone = it })
    ExecutiveTextField(label = "آدرس", value = address, onValueChange = { address = it })
    ExecutiveTextField(label = "رده مشتری (خرید اول / نقدی / اعتباری / VIP)", value = tier, onValueChange = { tier = it })

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val updated = customer.copy(
            phone = phone,
            address = address,
            tier = tier
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ذخیره پرونده", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(customer) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

@Composable
fun ExecutiveTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  keyboardType: KeyboardType = KeyboardType.Text,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    colors = OutlinedTextFieldDefaults.colors(
      focusedTextColor = customColors.textPrimary,
      unfocusedTextColor = customColors.textPrimary,
      focusedContainerColor = customColors.secondaryBg,
      unfocusedContainerColor = customColors.secondaryBg,
      focusedBorderColor = AccentBlue,
      unfocusedBorderColor = customColors.border,
      focusedLabelColor = AccentBlue,
      unfocusedLabelColor = customColors.textMuted
    )
  )
}

/**
 * Multi-Model Cutting Sheet (پاپ‌آپ بازشونده برش چندین مدل مختلف از یک طاقه پارچه)
 */
@Composable
fun MultiModelCuttingSheet(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit,
  onClose: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val fabrics by viewModel.fabrics.collectAsState()
  val context = androidx.compose.ui.platform.LocalContext.current

  // Selected fabric
  var selectedFabricId by remember(fabrics) {
    mutableStateOf(fabrics.firstOrNull()?.id ?: 0L)
  }
  val currentFabric = fabrics.find { it.id == selectedFabricId } ?: fabrics.firstOrNull()

  // Dynamic list of models to cut from this fabric roll
  data class CutItemState(
    var modelName: String,
    var modelCode: String,
    var heightCm: String,
    var meterPerItem: String,
    var count: String
  )

  var modelItems by remember {
    mutableStateOf(
      listOf(
        CutItemState("تیشرت لانگ اوورسایز", "TS-102", "75", "1.10", "40"),
        CutItemState("شلوارک ست راحتی", "SH-102", "50", "0.75", "40")
      )
    )
  }

  var keepRemainingInStock by remember { mutableStateOf(true) }

  // Total required meters calculation
  val totalRequiredMeters = modelItems.sumOf { item ->
    val cnt = item.count.toIntOrNull() ?: 0
    val m = item.meterPerItem.toDoubleOrNull() ?: 0.0
    cnt * m
  }

  val availableMeters = currentFabric?.totalMeters ?: 0.0
  val remainingMeters = availableMeters - totalRequiredMeters
  val isOverCapacity = remainingMeters < 0.0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "پاپ‌آپ برش چند مدلی از یک طاقه",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "استخراج همزمان چندین مدل مختلف از یک طاقه پارچه با ثبت قد و تعداد",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
      }
    }

    // 1. Fabric Selection Picker
    Text(
      text = "انتخاب طاقه پارچه مبدا برش:",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      fabrics.take(4).forEach { fab ->
        val isSelected = (fab.id == currentFabric?.id)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.secondaryBg)
            .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(10.dp))
            .clickable { selectedFabricId = fab.id }
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${fab.name} (${fab.color}) - کد: ${fab.code}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "موجودی: ${fab.totalMeters.toInt()} متر | ${fab.totalWeightKg.toInt()} کیلو (${fab.rollCount} طاقه)",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            if (isSelected) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
            }
          }
        }
      }
    }

    HorizontalDivider(color = customColors.border, thickness = 1.dp)

    // 2. Models cut from this roll
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "مدل‌های برش‌خورده از این طاقه:",
        style = MaterialTheme.typography.labelMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = {
          modelItems = modelItems + CutItemState("مدل جدید", "M-${modelItems.size + 1}", "70", "1.00", "20")
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("+ افزودن مدل دیگر", style = MaterialTheme.typography.labelSmall)
      }
    }

    modelItems.forEachIndexed { index, item ->
      val itemMeters = (item.count.toIntOrNull() ?: 0) * (item.meterPerItem.toDoubleOrNull() ?: 0.0)

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.secondaryBg)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "مدل شماره ${index + 1}: ${item.modelName}",
              style = MaterialTheme.typography.labelMedium,
              color = AccentIndigo,
              fontWeight = FontWeight.Bold
            )
            if (modelItems.size > 1) {
              IconButton(
                onClick = {
                  modelItems = modelItems.toMutableList().also { it.removeAt(index) }
                },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = StatusDanger, modifier = Modifier.size(18.dp))
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1.4f)) {
              ExecutiveTextField(
                label = "نام مدل",
                value = item.modelName,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(modelName = newV) }
                }
              )
            }
            Box(modifier = Modifier.weight(0.8f)) {
              ExecutiveTextField(
                label = "کد مدل",
                value = item.modelCode,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(modelCode = newV) }
                }
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "قد کار (سانتی‌متر)",
                value = item.heightCm,
                keyboardType = KeyboardType.Number,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(heightCm = newV) }
                }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "مصرف هر کار (متر)",
                value = item.meterPerItem,
                keyboardType = KeyboardType.Decimal,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(meterPerItem = newV) }
                }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "تعداد کار (عدد)",
                value = item.count,
                keyboardType = KeyboardType.Number,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(count = newV) }
                }
              )
            }
          }

          Text(
            text = "متراژ پارچه مصرفی این مدل: ${String.format(java.util.Locale.US, "%.1f", itemMeters)} متر",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
        }
      }
    }

    // 3. Live Balance & Remaining Fabric Calculator Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(if (isOverCapacity) StatusDanger.copy(alpha = 0.1f) else customColors.secondaryBg)
        .border(1.dp, if (isOverCapacity) StatusDanger else customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("متراژ کل موجود در طاقه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("${availableMeters.toInt()} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مجموع متراژ مورد نیاز مدل‌ها:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("${String.format(java.util.Locale.US, "%.1f", totalRequiredMeters)} متر", style = MaterialTheme.typography.bodySmall, color = AccentIndigo, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("باقی‌مانده پارچه در طاقه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(
            text = if (isOverCapacity) "کسری متراژ: ${String.format(java.util.Locale.US, "%.1f", -remainingMeters)} متر!" else "${String.format(java.util.Locale.US, "%.1f", remainingMeters)} متر موجودی باقی‌مانده",
            style = MaterialTheme.typography.bodySmall,
            color = if (isOverCapacity) StatusDanger else StatusSuccess,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 4. Keep remaining fabric switch
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "نگهداری باقیمانده طاقه در انبار به عنوان پارچه خرد",
        style = MaterialTheme.typography.bodySmall,
        color = customColors.textSecondary
      )
      androidx.compose.material3.Switch(
        checked = keepRemainingInStock,
        onCheckedChange = { keepRemainingInStock = it }
      )
    }

    // Submit Button
    Button(
      onClick = {
        if (currentFabric != null && !isOverCapacity && modelItems.isNotEmpty()) {
          val cuts = modelItems.map { state ->
            com.example.data.model.MultiCutModelItem(
              modelName = state.modelName,
              modelCode = state.modelCode,
              heightCm = state.heightCm.toIntOrNull() ?: 70,
              metersPerItem = state.meterPerItem.toDoubleOrNull() ?: 1.0,
              cutQuantity = state.count.toIntOrNull() ?: 1
            )
          }
          viewModel.submitMultiModelCutting(
            fabricId = currentFabric.id,
            modelCuts = cuts,
            keepRemainingInStock = keepRemainingInStock,
            context = context
          )
        }
      },
      enabled = !isOverCapacity && currentFabric != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("submit_multi_cut_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isOverCapacity) customColors.textMuted else AccentIndigo
      )
    ) {
      Text(
        text = if (isOverCapacity) "کسری متراژ در طاقه - لطفاً اصلاح کنید" else "تأیید و اجرای برش چند مدلی طاقه",
        fontWeight = FontWeight.Bold
      )
    }
  }
}
