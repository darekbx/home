package com.darekbx.lifetimememo.screens.settings.model

import com.darekbx.storage.lifetimememo.CategoryDto
import com.darekbx.lifetimememo.data.dto.ContainerDto
import com.darekbx.storage.lifetimememo.LocationDto
import com.darekbx.storage.lifetimememo.MemoDto

data class BackupModel(
    val containers: List<ContainerDto>,
    val memos: List<MemoDto>,
    var categories: List<CategoryDto> = emptyList(),
    var locations: List<LocationDto> = emptyList()
)