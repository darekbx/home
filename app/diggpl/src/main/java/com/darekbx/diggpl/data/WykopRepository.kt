package com.darekbx.diggpl.data

import com.darekbx.diggpl.data.remote.*
import javax.inject.Inject

class WykopRepository @Inject constructor(private val wykopService: WykopService) {

    suspend fun getTags(tagName: String, page: Int = 1): ResponseResult<ListWrapper<List<Tag>>> {
        return safeApiCall {
            wykopService.tags(tagName, page)
        }
    }
}
