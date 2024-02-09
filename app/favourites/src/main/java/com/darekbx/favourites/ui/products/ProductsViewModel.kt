package com.darekbx.favourites.ui.products

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.storage.favourites.FavouriteCategoryDto
import com.darekbx.storage.favourites.FavouriteItemDto
import com.darekbx.storage.favourites.FavouritesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val favouritesDao: FavouritesDao
) : ViewModel() {

    var isLoading = mutableStateOf(false)

    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            isLoading.value = true
            delay(300)
            favouritesDao.addCategory(FavouriteCategoryDto(null, categoryName, index = -1))
            isLoading.value = false
        }
    }

    fun deleteCategory(category: FavouriteCategoryDto): Boolean {
        viewModelScope.launch {
            favouritesDao.deleteCategoryItems(category.id!!)
            favouritesDao.deleteCategory(category.id!!)
        }
        return true
    }

    fun deleteItem(item: FavouriteItemDto): Boolean {
        viewModelScope.launch {
            favouritesDao.deleteItem(item.id!!)
        }
        return true
    }

    fun addItem(categoryId: Long, name: String, comment: String, rating: Float) {
        viewModelScope.launch {
            isLoading.value = true
            delay(300)
            favouritesDao.addItem(
                FavouriteItemDto(
                    null,
                    categoryId,
                    name,
                    rating,
                    comment,
                    System.currentTimeMillis()
                )
            )
            isLoading.value = false
        }
    }

    fun categories() = favouritesDao.fetchCategories()

    fun products(categoryId: Long) = favouritesDao.fetchProducts(categoryId)
}