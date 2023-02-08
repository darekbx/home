package com.darekbx.lifetimememo.screens.memos.repository

import kotlinx.coroutines.flow.Flow

abstract class BaseMemosRepository {

    abstract fun countMemos(): Flow<Int>

    abstract fun countContainers(): Flow<Int>
}