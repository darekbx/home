package com.darekbx.infopigula.domain

import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.model.Creator
import com.darekbx.infopigula.repository.RemoteRepository
import javax.inject.Inject

class GetCreatorsUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) {
    suspend operator fun invoke(): Result<List<Creator>> {
        try {
            val response = remoteRepository.getCreators()
            return Result.success(response.map {
                Creator(it.name, it.description, it.flagged == "1", it.logo)
            })
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return Result.failure(e)
        }
    }
}