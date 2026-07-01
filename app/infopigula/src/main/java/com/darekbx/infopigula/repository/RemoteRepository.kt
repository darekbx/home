package com.darekbx.infopigula.repository

import com.darekbx.infopigula.model.NewsResponse
import com.darekbx.infopigula.repository.remote.InfopigulaService
import javax.inject.Inject

interface RemoteRepository {

    suspend fun getNews(): NewsResponse
}

class DefaultRemoteRepository @Inject constructor(
    private val infopigulaService: InfopigulaService
) : RemoteRepository {

    override suspend fun getNews(): NewsResponse {
        return infopigulaService.getNews()
    }
}
