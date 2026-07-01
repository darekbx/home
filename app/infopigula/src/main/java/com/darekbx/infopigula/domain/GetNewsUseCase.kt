package com.darekbx.infopigula.domain

import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.model.NewsResponse
import com.darekbx.infopigula.repository.RemoteRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) {
    suspend operator fun invoke(): Result<NewsResponse> {
        try {
            val response = remoteRepository.getNews()
            return Result.success(response)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return Result.failure(e)
        }
    }
}
