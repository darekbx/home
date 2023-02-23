package com.darekbx.diggpl.data

import com.darekbx.diggpl.data.remote.*
import javax.inject.Inject

class WykopRepository @Inject constructor(private val wykopService: WykopService) {

    suspend fun getTags(
        tagName: String,
        sort: String = "all",
        page: Int = 1
    ): ResponseResult<ListWrapper<List<StreamItem>>> {
        return safeApiCall {
            wykopService.tags(tagName, sort, page)
        }
    }

    /**
     * @param type links type,to fetch homepage use this type "homepage"
     */
    suspend fun getLinks(
        type: String,
        sort: String = "newest",
        page: Int = 1
    ): ResponseResult<ListWrapper<List<StreamItem>>> {
        return safeApiCall {
            wykopService.links(type, sort, page)
        }
    }

    suspend fun getEntry(entryId: Int) = safeApiCall {
        wykopService.entry(entryId)
    }

    suspend fun getLink(linkId: Int) = safeApiCall {
        wykopService.link(linkId)
    }

    suspend fun getEntryComments(linkId: Int, page: Int):
            ResponseResult<ListWrapper<List<Comment>>> {
        return safeApiCall {
            wykopService.entryComments(linkId, page)
        }
    }

    suspend fun getLinkComments(linkId: Int, page: Int):
            ResponseResult<ListWrapper<List<Comment>>> {
        return safeApiCall {
            wykopService.linkComments(linkId, page)
        }
    }

    suspend fun getLinkRelated(linkId: Int) = safeApiCall {
        wykopService.linkRelated(linkId)
    }
}
