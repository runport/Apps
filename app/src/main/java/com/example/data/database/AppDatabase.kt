package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CustomerDao
import com.example.data.dao.CuttingDao
import com.example.data.dao.FabricDao
import com.example.data.dao.FactorySettingsDao
import com.example.data.dao.InventoryDao
import com.example.data.dao.ModelStandardDao
import com.example.data.dao.ProductionDao
import com.example.data.dao.SaleOrderDao
import com.example.data.dao.SupplierDao
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.FabricEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.ProductionEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SupplierEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
  entities = [
    FabricEntity::class,
    CuttingEntity::class,
    ProductionEntity::class,
    InventoryEntity::class,
    SaleOrderEntity::class,
    CustomerEntity::class,
    SupplierEntity::class,
    ModelStandardEntity::class,
    FactorySettingsEntity::class,
  ],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun fabricDao(): FabricDao
  abstract fun cuttingDao(): CuttingDao
  abstract fun productionDao(): ProductionDao
  abstract fun inventoryDao(): InventoryDao
  abstract fun saleOrderDao(): SaleOrderDao
  abstract fun customerDao(): CustomerDao
  abstract fun supplierDao(): SupplierDao
  abstract fun modelStandardDao(): ModelStandardDao
  abstract fun factorySettingsDao(): FactorySettingsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(
      context: Context,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    ): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "manufacturing_executive.db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    suspend fun resetDatabaseToDemo(db: AppDatabase) {
      db.clearAllTables()
      populateDatabase(db)
    }

    suspend fun populateDatabase(db: AppDatabase) {
      // 1. Model Standards
      val standards = listOf(
        ModelStandardEntity(
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          standardFabricConsumptionMeters = 1.40,
          standardWeightGrams = 370.0,
          sewingWage = 75000L,
          suggestedSalePrice = 850000L,
          baseFabricCostPerMeter = 210000L
        ),
        ModelStandardEntity(
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          standardFabricConsumptionMeters = 1.15,
          standardWeightGrams = 290.0,
          sewingWage = 55000L,
          suggestedSalePrice = 620000L,
          baseFabricCostPerMeter = 195000L
        ),
        ModelStandardEntity(
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          standardFabricConsumptionMeters = 0.85,
          standardWeightGrams = 180.0,
          sewingWage = 35000L,
          suggestedSalePrice = 390000L,
          baseFabricCostPerMeter = 160000L
        )
      )
      db.modelStandardDao().insertAll(standards)

      // 2. Fabrics (Raw materials with Meters and Kilograms)
      val fabrics = listOf(
        FabricEntity(
          name = "پنبه دورس ۳ نخ خارخورده",
          code = "M204",
          color = "مشکی زغالی",
          batchNumber = "PRT-982",
          supplierName = "نساجی تابان کاشان",
          rollCount = 8,
          totalMeters = 420.0,
          totalWeightKg = 155.0,
          buyPricePerMeter = 210000L,
          buyPricePerKg = 569000L,
          currentMarketPrice = 240000L,
          isLowStock = true, // Triggers alert as requested
          previousBuyPricePerMeter = 190000L,
          previousBuyPricePerKg = 515000L,
          previousPurchaseDate = "۱۲ بهمن",
          purchaseDate = "۱۴ اسفند"
        ),
        FabricEntity(
          name = "کتان بنگالین کشی",
          code = "M201",
          color = "سبز ارتشی",
          batchNumber = "PRT-975",
          supplierName = "نساجی جهان بافت",
          rollCount = 19,
          totalMeters = 1350.0,
          totalWeightKg = 390.0,
          buyPricePerMeter = 195000L,
          buyPricePerKg = 675000L,
          currentMarketPrice = 215000L,
          isLowStock = false,
          previousBuyPricePerMeter = 182000L,
          previousBuyPricePerKg = 630000L,
          previousPurchaseDate = "۲۵ بهمن",
          purchaseDate = "۱۳ اسفند"
        ),
        FabricEntity(
          name = "سوپر پنبه ۱۰۰٪ شانه شده",
          code = "M108",
          color = "سفید اپتیک",
          batchNumber = "PRT-960",
          supplierName = "شرکت بافندگی مهر",
          rollCount = 26,
          totalMeters = 2100.0,
          totalWeightKg = 378.0,
          buyPricePerMeter = 160000L,
          buyPricePerKg = 888000L,
          currentMarketPrice = 175000L,
          isLowStock = false,
          previousBuyPricePerMeter = 0L,
          previousBuyPricePerKg = 0L,
          previousPurchaseDate = "",
          purchaseDate = "۱۱ اسفند"
        ),
        FabricEntity(
          name = "گلکسی پنبه لاکرا دار",
          code = "M310",
          color = "طوسی ملانژ",
          batchNumber = "PRT-988",
          supplierName = "نساجی تابان کاشان",
          rollCount = 14,
          totalMeters = 980.0,
          totalWeightKg = 245.0,
          buyPricePerMeter = 225000L,
          buyPricePerKg = 900000L,
          currentMarketPrice = 250000L,
          isLowStock = false,
          previousBuyPricePerMeter = 205000L,
          previousBuyPricePerKg = 820000L,
          previousPurchaseDate = "۱۸ بهمن",
          purchaseDate = "۱۲ اسفند"
        )
      )
      db.fabricDao().insertAll(fabrics)

      // 3. Cutting Orders (with progress and audit)
      val cuttings = listOf(
        CuttingEntity(
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          fabricCode = "M204",
          targetQuantity = 1000,
          cutQuantity = 850,
          standardMetersPerItem = 1.40,
          actualMetersPerItem = 1.42,
          status = "در حال برش",
          date = "۱۴ اسفند"
        ),
        CuttingEntity(
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          fabricCode = "M201",
          targetQuantity = 600,
          cutQuantity = 480,
          standardMetersPerItem = 1.15,
          actualMetersPerItem = 1.36, // Abnormal consumption: 18% more!
          status = "در حال برش",
          date = "۱۳ اسفند"
        ),
        CuttingEntity(
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          fabricCode = "M108",
          targetQuantity = 1500,
          cutQuantity = 1500,
          standardMetersPerItem = 0.85,
          actualMetersPerItem = 0.84,
          status = "تکمیل شده",
          date = "۱۱ اسفند"
        )
      )
      db.cuttingDao().insertAll(cuttings)

      // 4. Production Records
      val productions = listOf(
        ProductionEntity(
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          quantity = 500,
          fabricRollsUsed = 10,
          fabricMetersUsed = 700.0,
          totalWeightKg = 185.0, // 370g per item
          sewingWagePerItem = 75000L,
          fabricPricePerMeter = 210000L,
          accessoriesCostPerItem = 32000L,
          status = "تکمیل شده",
          date = "۱۴ اسفند"
        ),
        ProductionEntity(
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          quantity = 400,
          fabricRollsUsed = 7,
          fabricMetersUsed = 544.0,
          totalWeightKg = 116.0,
          sewingWagePerItem = 55000L,
          fabricPricePerMeter = 195000L,
          accessoriesCostPerItem = 28000L,
          status = "در حال دوخت",
          date = "۱۳ اسفند"
        ),
        ProductionEntity(
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          quantity = 1200,
          fabricRollsUsed = 14,
          fabricMetersUsed = 1020.0,
          totalWeightKg = 216.0,
          sewingWagePerItem = 35000L,
          fabricPricePerMeter = 160000L,
          accessoriesCostPerItem = 12000L,
          status = "بسته‌بندی",
          date = "۱۲ اسفند"
        )
      )
      db.productionDao().insertAll(productions)

      // 5. Inventory Items (Warehouse)
      val inventory = listOf(
        InventoryEntity(
          name = "هودی اورسایز M204",
          code = "PRD-204",
          category = "محصولات آماده",
          readyForShipment = 320,
          reservedQuantity = 120,
          availableForSale = 200,
          unitSalePrice = 850000L,
          unitCostPrice = 478000L,
          unitWeightGrams = 370.0,
          lastUpdated = "امروز، ساعت ۱۱:۳۰"
        ),
        InventoryEntity(
          name = "شلوار اسلش کژوال M201",
          code = "PRD-201",
          category = "محصولات آماده",
          readyForShipment = 180,
          reservedQuantity = 90,
          availableForSale = 150,
          unitSalePrice = 620000L,
          unitCostPrice = 348000L,
          unitWeightGrams = 290.0,
          lastUpdated = "دیروز"
        ),
        InventoryEntity(
          name = "تی‌شرت پنبه بیسیک M108",
          code = "PRD-108",
          category = "محصولات آماده",
          readyForShipment = 450,
          reservedQuantity = 300,
          availableForSale = 620,
          unitSalePrice = 390000L,
          unitCostPrice = 183000L,
          unitWeightGrams = 180.0,
          lastUpdated = "امروز، ساعت ۰۹:۱۵"
        ),
        InventoryEntity(
          name = "زیپ دنده‌فلزی ۸۰ سانت YKK",
          code = "ACC-012",
          category = "ملزومات",
          readyForShipment = 0,
          reservedQuantity = 400,
          availableForSale = 1800,
          unitSalePrice = 28000L,
          unitCostPrice = 24000L,
          unitWeightGrams = 25.0,
          unitType = "عدد",
          lastUpdated = "امروز",
          previousCostPrice = 21500L,
          previousCostDate = "۲۵ بهمن",
          supplierName = "صنایع زیپ و یراق پارس (YKK)"
        ),
        InventoryEntity(
          name = "بند کلاه گرد بافت اعلا",
          code = "ACC-045",
          category = "ملزومات",
          readyForShipment = 0,
          reservedQuantity = 300,
          availableForSale = 2200,
          unitSalePrice = 9500L,
          unitCostPrice = 7200L,
          unitWeightGrams = 12.0,
          unitType = "متر",
          lastUpdated = "۱۳ اسفند",
          previousCostPrice = 6400L,
          previousCostDate = "۱۵ بهمن",
          supplierName = "تولیدی بند و کش الماس"
        )
      )
      db.inventoryDao().insertAll(inventory)

      // 6. Sales Orders (with timeline and delays)
      val orders = listOf(
        SaleOrderEntity(
          orderNumber = "#2048",
          customerName = "پوشاک سپهر تهران",
          customerPhone = "۰۹۱۲۳۴۵۶۷۸۹",
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          quantity = 350,
          unitPrice = 850000L,
          unitCost = 478000L,
          discountAmount = 3500000L,
          paidAmount = 180000000L,
          orderDate = "۹ اسفند",
          deliveryStatus = "در تولید",
          isDelayed = true,
          delayDays = 2 // Alert requirement: #2048 عقب افتاده ۲ روز تأخیر
        ),
        SaleOrderEntity(
          orderNumber = "#2049",
          customerName = "بوتیک زنجیره‌ای الگانس",
          customerPhone = "۰۹۱۲۹۸۷۶۵۴۳",
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          quantity = 240,
          unitPrice = 620000L,
          unitCost = 348000L,
          discountAmount = 1800000L,
          paidAmount = 147000000L,
          orderDate = "۱۲ اسفند",
          deliveryStatus = "آماده ارسال",
          isDelayed = false
        ),
        SaleOrderEntity(
          orderNumber = "#2050",
          customerName = "پخش عمده آوا شیراز",
          customerPhone = "۰۹۱۷۱۱۱۴۴۵۵",
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          quantity = 800,
          unitPrice = 390000L,
          unitCost = 183000L,
          discountAmount = 5000000L,
          paidAmount = 250000000L,
          orderDate = "۱۳ اسفند",
          deliveryStatus = "در حال تکمیل",
          isDelayed = false
        ),
        SaleOrderEntity(
          orderNumber = "#2051",
          customerName = "فروشگاه مد امروز اصفهان",
          customerPhone = "۰۹۱۳۲۲۲۶۶۷۷",
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          quantity = 180,
          unitPrice = 850000L,
          unitCost = 478000L,
          discountAmount = 0L,
          paidAmount = 153000000L,
          orderDate = "۱۴ اسفند",
          deliveryStatus = "ثبت شده",
          isDelayed = false
        ),
        SaleOrderEntity(
          orderNumber = "#2045",
          customerName = "مرکز خرید رویال تبریز",
          customerPhone = "۰۹۱۴۳۳۳۸۸۹۹",
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          quantity = 300,
          unitPrice = 620000L,
          unitCost = 348000L,
          discountAmount = 2000000L,
          paidAmount = 184000000L,
          orderDate = "۶ اسفند",
          deliveryStatus = "تحویل شده",
          isDelayed = false
        )
      )
      db.saleOrderDao().insertAll(orders)

      // 7. Customers (Mini-dashboard profiles)
      val customers = listOf(
        CustomerEntity(
          name = "پوشاک سپهر تهران (حاج احمد سپهری)",
          company = "بازرگانی سپهر پارس",
          phone = "۰۹۱۲۳۴۵۶۷۸۹",
          address = "تهران، بازار بزرگ، سرای حاج حسن، پلاک ۴۲",
          category = "عمده‌فروش",
          totalPurchases = 4850000000L,
          orderCount = 14,
          currentDebt = 114000000L,
          tier = "خرید سوم+",
          popularModels = "M204, M201",
          lastOrderDate = "۹ اسفند"
        ),
        CustomerEntity(
          name = "بوتیک زنجیره‌ای الگانس",
          company = "مجموعه فروشگاه‌های الگانس",
          phone = "۰۹۱۲۹۸۷۶۵۴۳",
          address = "تهران، شهرک غرب، مرکز خرید گلستان، طبقه اول",
          category = "فروشگاه زنجیره‌ای",
          totalPurchases = 2150000000L,
          orderCount = 8,
          currentDebt = 0L,
          tier = "خرید سوم+",
          popularModels = "M201, M108",
          lastOrderDate = "۱۲ اسفند"
        ),
        CustomerEntity(
          name = "پخش عمده آوا شیراز",
          company = "پخش پوشاک آوا",
          phone = "۰۹۱۷۱۱۱۴۴۵۵",
          address = "شیراز، بلوار مدرس، مجتمع تجاری بهار",
          category = "عمده‌فروش",
          totalPurchases = 980000000L,
          orderCount = 3,
          currentDebt = 57000000L,
          tier = "خرید دوم",
          popularModels = "M108",
          lastOrderDate = "۱۳ اسفند"
        ),
        CustomerEntity(
          name = "فروشگاه مد امروز اصفهان",
          company = "مد امروز ایران",
          phone = "۰۹۱۳۲۲۲۶۶۷۷",
          address = "اصفهان، خیابان چهارباغ بالا",
          category = "بوتیک و آنلاین",
          totalPurchases = 153000000L,
          orderCount = 1,
          currentDebt = 0L,
          tier = "خرید اول",
          popularModels = "M204",
          lastOrderDate = "۱۴ اسفند"
        )
      )
      db.customerDao().insertAll(customers)

      // 8. Suppliers
      val suppliers = listOf(
        SupplierEntity(
          name = "حاج رضا تابان",
          storeName = "نساجی تابان کاشان",
          mobile = "۰۹۱۲۱۱۱۹۹۸۸",
          phone = "۰۳۱۵۵۲۲۹۹۰۰",
          address = "کاشان، شهرک صنعتی راوند، بلوار نساجی، پلاک ۱۲",
          distributionCategory = "پارچه دورس، پنبه و ملانژ",
          description = "تأمین‌کننده اصلی پارچه‌های پاییزه و زمستانه با شرایط چکی ۶۰ روزه",
          supplyType = "پارچه دورس و پنبه",
          totalPurchases = 6200000000L,
          lastPurchaseDate = "۱۲ اسفند",
          priceHistoryNote = "افزایش ۸ درصدی در پارت جدید به علت نوسان نخ پنبه"
        ),
        SupplierEntity(
          name = "مهندس علیرضا جهان‌بخش",
          storeName = "بازرگانی نساجی جهان بافت",
          mobile = "۰۹۱۲۳۳۳۴۴۵۵",
          phone = "۰۲۱۸۸۳۳۵۵۴۴",
          address = "تهران، خیابان خیام، کوچه بازار پاچنار، پلاک ۴۸",
          distributionCategory = "پارچه کتان، بنگالین و لی کاغذی",
          description = "تأمین پارچه‌های شلواری با ضمانت ثبات رنگ و عدم آبرفت",
          supplyType = "پارچه کتان و بنگالین",
          totalPurchases = 3800000000L,
          lastPurchaseDate = "۱۰ اسفند",
          priceHistoryNote = "قیمت ثابت با تخفیف ۵ درصدی خرید تناژ"
        ),
        SupplierEntity(
          name = "محمدحسین پارسا",
          storeName = "صنایع زیپ و یراق پارس (YKK)",
          mobile = "۰۹۱۲۵۵۵۶۶۷۷",
          phone = "۰۲۱۵۵۶۶۷۷۸۸",
          address = "تهران، بازار بزرگ، سرای مشیر خلوت، طبقه همکف، پلاک ۱۰",
          distributionCategory = "زیپ، سرزیپ فلزی، دکمه و مارک",
          description = "واردات و توزیع مستقیم یراق‌آلات درجه یک پوشاک اسپرت",
          supplyType = "ملزومات (زیپ، دکمه، کش)",
          totalPurchases = 920000000L,
          lastPurchaseDate = "۷ اسفند",
          priceHistoryNote = "تحویل سریع ۲۴ ساعته با ثبات نرخ تا پایان ماه"
        ),
        SupplierEntity(
          name = "کریم اسماعیلی",
          storeName = "تولیدی بند و کش الماس",
          mobile = "۰۹۳۵۶۶۶۷۷۸۸",
          phone = "۰۲۱۶۶۷۷۸۸۹۹",
          address = "تهران، خیابان فلسطین جنوبی، کوچه مهرداد، بن‌بست اول",
          distributionCategory = "بند کلاه هودی، کش شلوار، مارک ژلاتینی",
          description = "تولید سفارشی بند گرد بافت و کش پهن با تراکم بالا",
          supplyType = "ملزومات (بند کلاه و کش شلوار)",
          totalPurchases = 410000000L,
          lastPurchaseDate = "۲ اسفند",
          priceHistoryNote = "کیفیت سوپر بدون رنگ‌دهی در شستشو"
        )
      )
      db.supplierDao().insertAll(suppliers)

      // 9. Factory Settings (Fixed Costs, Margins, Overheads & Preferences)
      db.factorySettingsDao().insertOrUpdate(
        FactorySettingsEntity(
          id = 1L,
          fixedShippingCostPerOrder = 180000L,
          fixedShippingCostPerRoll = 85000L,
          targetProfitMarginPercent = 35.0,
          overheadCostPerItem = 45000L,
          defaultAccessoriesCost = 32000L,
          isDarkTheme = true,
          companyName = "تولیدی برتر پوشاک"
        )
      )
    }
  }

  private class DatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateDatabase(database)
        }
      }
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
      super.onDestructiveMigration(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateDatabase(database)
        }
      }
    }
  }
}
