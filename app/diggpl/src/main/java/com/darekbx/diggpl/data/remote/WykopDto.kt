package com.darekbx.diggpl.data.remote

sealed class ResponseResult<out H> {
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Failure(val error: Throwable) : ResponseResult<Nothing>()
}

data class DataWrapper<T>(val data: T)

data class ListWrapper<T>(val data: T, val pagination: Pagination)

data class Pagination(val total: Int)

data class Data(val key: String, val secret: String)

data class AuthToken(val token: String)

data class Tag(val title: String)