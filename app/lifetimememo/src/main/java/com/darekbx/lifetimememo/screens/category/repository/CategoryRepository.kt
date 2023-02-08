package com.darekbx.lifetimememo.screens.category.repository

import com.darekbx.lifetimememo.screens.category.model.Category
import com.darekbx.lifetimememo.screens.category.model.Category.Companion.toDomain
import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.storage.lifetimememo.CategoryDto
import com.darekbx.storage.lifetimememo.ExtendedCategoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val memoDao: MemoDao
) {

    fun categories(): Flow<List<Category>> =
        memoDao
            .categories()
            .map { result -> mapToDomain(result) }

    suspend fun add(category: Category) {
        withContext(Dispatchers.IO) {
            memoDao.add(category.mapToDto())
        }
    }

    suspend fun delete(uid: String) {
        memoDao.deleteCategory(uid)
    }

    private fun mapToDomain(result: List<ExtendedCategoryDto>) =
        result.map { item ->
            item.categoryDto()
                .toDomain()
                .apply { entriesCount = item.count }
        }

    private fun Category.mapToDto(): CategoryDto {
        return CategoryDto(this.uid, name)
    }
}