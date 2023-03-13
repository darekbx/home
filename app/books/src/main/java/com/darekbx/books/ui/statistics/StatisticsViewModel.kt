package com.darekbx.books.ui.statistics

import androidx.lifecycle.ViewModel
import com.darekbx.books.data.BooksRepository
import com.darekbx.books.data.model.StatisticsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    fun statisticsData() = booksRepository
        .getBooks()
        .map { books ->
            books
                .groupBy { it.year }
                .map { entry ->
                    StatisticsItem(
                        entry.key,
                        entry.value.size,
                        entry.value.filter { it.isInEnglish() }.size,
                        entry.value.filterNot { it.isInEnglish() }.size,
                    )
                }
                .reversed()
        }
}
