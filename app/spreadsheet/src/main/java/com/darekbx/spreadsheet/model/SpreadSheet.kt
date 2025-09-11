package com.darekbx.spreadsheet.model

import androidx.compose.runtime.Stable
import com.darekbx.spreadsheet.utils.TimestampFormatter
import com.darekbx.storage.spreadsheet.entities.SpreadSheetDto

@Stable
data class SpreadSheet(
    val uid: String,
    val name: String,
    val parentName: String,
    val created: String,
    val updated: String,
    val parentUid: String? = null
) {

    var childrenNames: List<String> = emptyList()

    companion object {
        fun SpreadSheetDto.fromEntity(): SpreadSheet {
            return SpreadSheet(
                uid = uid,
                name = name,
                parentName = parentName,
                created = TimestampFormatter.formatToDate(createdTimestamp),
                updated = TimestampFormatter.formatToDate(updatedTimestamp),
                parentUid = parentUid
            )
        }
    }
}
