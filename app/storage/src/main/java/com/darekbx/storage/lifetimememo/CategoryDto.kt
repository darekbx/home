package com.darekbx.storage.lifetimememo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
class CategoryDto(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String
)

data class ExtendedCategoryDto(val uid: String, val name: String, val count: Int) {
    fun categoryDto() = CategoryDto(uid, name)
}
