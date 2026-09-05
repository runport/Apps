package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.FabricEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.ProductionEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FabricDao {
  @Query("SELECT * FROM fabrics ORDER BY id DESC")
  fun getAllFabrics(): Flow<List<FabricEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFabric(fabric: FabricEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(fabrics: List<FabricEntity>)

  @Update
  suspend fun updateFabric(fabric: FabricEntity)

  @Delete
  suspend fun deleteFabric(fabric: FabricEntity)

  @Query("DELETE FROM fabrics WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM fabrics WHERE code = :code LIMIT 1")
  suspend fun getByCode(code: String): FabricEntity?

  @Query("SELECT * FROM fabrics WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): FabricEntity?
}

@Dao
interface CuttingDao {
  @Query("SELECT * FROM cutting_orders ORDER BY id DESC")
  fun getAllCuttings(): Flow<List<CuttingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCutting(cutting: CuttingEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(cuttings: List<CuttingEntity>)

  @Update
  suspend fun updateCutting(cutting: CuttingEntity)

  @Delete
  suspend fun deleteCutting(cutting: CuttingEntity)
}

@Dao
interface ProductionDao {
  @Query("SELECT * FROM production_records ORDER BY id DESC")
  fun getAllProductions(): Flow<List<ProductionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduction(production: ProductionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(productions: List<ProductionEntity>)

  @Update
  suspend fun updateProduction(production: ProductionEntity)

  @Delete
  suspend fun deleteProduction(production: ProductionEntity)
}

@Dao
interface InventoryDao {
  @Query("SELECT * FROM inventory_items ORDER BY id DESC")
  fun getAllInventory(): Flow<List<InventoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: InventoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<InventoryEntity>)

  @Update
  suspend fun updateItem(item: InventoryEntity)

  @Delete
  suspend fun deleteItem(item: InventoryEntity)

  @Query("DELETE FROM inventory_items WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM inventory_items WHERE code = :code LIMIT 1")
  suspend fun getByCode(code: String): InventoryEntity?

  @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): InventoryEntity?
}

@Dao
interface SaleOrderDao {
  @Query("SELECT * FROM sales_orders ORDER BY id DESC")
  fun getAllSalesOrders(): Flow<List<SaleOrderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: SaleOrderEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(orders: List<SaleOrderEntity>)

  @Update
  suspend fun updateOrder(order: SaleOrderEntity)

  @Delete
  suspend fun deleteOrder(order: SaleOrderEntity)
}

@Dao
interface CustomerDao {
  @Query("SELECT * FROM customers ORDER BY totalPurchases DESC")
  fun getAllCustomers(): Flow<List<CustomerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(customers: List<CustomerEntity>)

  @Update
  suspend fun updateCustomer(customer: CustomerEntity)

  @Delete
  suspend fun deleteCustomer(customer: CustomerEntity)
}

@Dao
interface SupplierDao {
  @Query("SELECT * FROM suppliers ORDER BY totalPurchases DESC")
  fun getAllSuppliers(): Flow<List<SupplierEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSupplier(supplier: SupplierEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(suppliers: List<SupplierEntity>)

  @Update
  suspend fun updateSupplier(supplier: SupplierEntity)
}

@Dao
interface ModelStandardDao {
  @Query("SELECT * FROM model_standards ORDER BY id ASC")
  fun getAllStandards(): Flow<List<ModelStandardEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStandard(standard: ModelStandardEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(standards: List<ModelStandardEntity>)

  @Update
  suspend fun updateStandard(standard: ModelStandardEntity)
}

@Dao
interface FactorySettingsDao {
  @Query("SELECT * FROM factory_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<FactorySettingsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: FactorySettingsEntity)

  @Query("DELETE FROM factory_settings")
  suspend fun deleteAll()
}

