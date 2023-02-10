package com.darekbx.diggpl.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> safeApiCall(crossinline body: suspend () -> T): ResponseResult<T> {
    return try {
        val users = withContext(Dispatchers.IO) {
            body()
        }
        ResponseResult.Success(users)
    } catch (e: Exception) {
        ResponseResult.Failure(e)
    }
}
