package com.darekbx.storage.favourites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {

    @Insert
    suspend fun addCategory(favouriteCategoryDto: FavouriteCategoryDto)

    @Insert
    suspend fun addItem(favouriteItemDto: FavouriteItemDto)

    @Query("SELECT * FROM favourite_category ORDER BY `index`")
    fun fetchCategories(): Flow<List<FavouriteCategoryDto>>

    @Query("SELECT * FROM favourite_item WHERE category_id = :categoryId")
    fun fetchProducts(categoryId: Long): Flow<List<FavouriteItemDto>>

    @Query("DELETE FROM favourite_category WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)

    @Query("DELETE FROM favourite_item WHERE category_id = :categoryId")
    suspend fun deleteCategoryItems(categoryId: Long)

    @Query("DELETE FROM favourite_item WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)
}
