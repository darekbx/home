package com.darekbx.lifetimememo.screens.memo.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.darekbx.lifetimememo.screens.memos.model.Memo
import com.darekbx.lifetimememo.screens.memos.viewmodel.MemosViewModel

@Stable
class MemoState {
    var title by mutableStateOf("")
    var titleValid by mutableStateOf(true)
    var subtitle by mutableStateOf("")
    var description by mutableStateOf("")
    var link by mutableStateOf("")
    var hasLocation by mutableStateOf(false)
    var categoryId by mutableStateOf("")
    var categoryValid by mutableStateOf(true)
    var important by mutableStateOf(false)
    var sticked by mutableStateOf(false)
    var parentId by mutableStateOf<String?>(null)

    var lat by mutableStateOf<Double?>(null)
    var lng by mutableStateOf<Double?>(null)

    fun loadState(memo: Memo) {
        title = memo.title
        memo.parentUid?.let { parentId = it }
        memo.subtitle?.let { subtitle = it }
        memo.description?.let { description = it }
        memo.link?.let { link = it }
        memo.flag?.let { memoFlag ->
            when (memoFlag) {
                Memo.Flag.STICKED.value -> sticked = true
                Memo.Flag.IMPORTANT.value -> important = true
            }
        }
        hasLocation = memo.hasLocation
        categoryId = memo.categoryUid
    }

    fun update(memoId: String, memosViewModel: MemosViewModel) {
        memosViewModel.update(
            memoId,
            title,
            subtitle,
            description,
            categoryId,
            computeFlag(important, sticked),
            link
        )

        lat?.let { safeLat ->
            lng?.let { safeLng ->
                if (hasLocation) {
                    memosViewModel.updateLocation(memoId, safeLat, safeLng)
                } else {
                    memosViewModel.addLocation(memoId, safeLat, safeLng)
                }
            }
        }
    }

    fun add(parentId: String?, memosViewModel: MemosViewModel) {
        val memoId = memosViewModel.add(
            title,
            subtitle,
            description,
            categoryId,
            computeFlag(important, sticked),
            link,
            parentId = parentId
        )

        lat?.let { safeLat ->
            lng?.let { safeLng ->
                memosViewModel.addLocation(memoId, safeLat, safeLng)
            }
        }
    }

    private fun computeFlag(important: Boolean, sticked: Boolean): Int? {
        return when {
            important -> Memo.Flag.IMPORTANT.value
            sticked -> Memo.Flag.STICKED.value
            else -> null
        }
    }
}