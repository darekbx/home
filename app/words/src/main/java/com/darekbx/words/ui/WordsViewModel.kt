package com.darekbx.words.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.storage.words.WordDao
import com.darekbx.storage.words.WordDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val wordDao: WordDao
) : ViewModel() {

    fun words() = wordDao.fetchWords()

    fun add(word: String, translation: String) {
        viewModelScope.launch {
            wordDao.addWord(WordDto(null, word, translation, 0, false, System.currentTimeMillis()))
        }
    }

    fun moveToArchived(wordDto: WordDto): Boolean {
        viewModelScope.launch {
            if (wordDto.isArchived) {
                wordDao.deleteWord(wordDto.id!!)
            } else {
                delay(150)
                wordDao.setArchived(wordDto.id!!, isArchived = true)
            }
        }
        return true
    }

    fun moveToActive(wordDto: WordDto): Boolean {
        viewModelScope.launch {
            delay(150)
            wordDao.setArchived(wordDto.id!!, isArchived = false)
        }
        return true
    }

    fun increaseCount(wordDto: WordDto) {
        viewModelScope.launch {
            wordDao.setCount(wordDto.id!!, wordDto.checkedCount + 1)
        }
    }
}
