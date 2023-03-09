package com.darekbx.diggpl.data

import com.darekbx.diggpl.data.local.model.SavedEntry
import com.darekbx.diggpl.data.local.model.SavedLink
import com.darekbx.diggpl.data.local.model.SavedTag
import com.darekbx.diggpl.data.remote.*
import com.darekbx.storage.diggpl.DiggDao
import com.darekbx.storage.diggpl.SavedEntryDto
import com.darekbx.storage.diggpl.SavedLinkDto
import com.darekbx.storage.diggpl.SavedTagDto
import javax.inject.Inject

class WykopRepository @Inject constructor(
    private val wykopService: WykopService,
    private val diggDao: DiggDao
) {
    suspend fun removeLink(linkId: Int) {
        diggDao.removeSavedLink(linkId)
    }

    suspend fun removeEntry(entryId: Int) {
        diggDao.removeSavedEntry(entryId)
    }

    suspend fun saveLink(streamItem: StreamItem) {
        diggDao.add(
            SavedLinkDto(
                linkId = streamItem.id,
                title = streamItem.title,
                content = streamItem.description
            )
        )
    }

    suspend fun saveEntry(streamItem: StreamItem) {
        diggDao.add(
            SavedEntryDto(
                entryId = streamItem.id,
                title = "",
                content = streamItem.content
            )
        )
    }

    suspend fun getSavedItems(): List<Any> {
        val links = diggDao.listSavedLinks().map {
            SavedLink(it.linkId, it.title, it.content)
        }
        val entries = diggDao.listSavedEntries().map {
            SavedEntry(it.entryId, it.title, it.content)
        }
        return links + entries
    }

    suspend fun countSavedItems(): Int {
        return diggDao.countSavedItems()
    }

    suspend fun getTags(
        tagName: String,
        sort: String = "all",
        page: Int = 1
    ): ResponseResult<ListWrapper<List<StreamItem>>> {
        return safeApiCall {
            wykopService.tags(tagName, sort, page)
        }
    }

    suspend fun tagAutocomplete(
        query: String
    ): ResponseResult<ListWrapper<List<TagAutocomplete>>> {
        return safeApiCall {
            wykopService.tagAutocomplete(query)
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

    suspend fun updateTagLastId(name: String, lastDate: String) {
        diggDao.updateTag(name, lastDate)
    }

    suspend fun getTagNewCount(tagName: String, lastDate: String) = safeApiCall {
        wykopService.tagNewCount(tagName, sort = "all", lastDate)
    }

    suspend fun addRemoveSavedTag(name: String) {
        if (diggDao.containsTag(name) != 0) {
            diggDao.removeTag(name)
        } else {
            val startDate = "2023-01-01 00:00:00"
            diggDao.addTag(SavedTagDto(null, name, startDate))
        }
    }

    suspend fun getSavedTags(): List<SavedTag> {
        return diggDao.listAllTags().map {
            SavedTag(it.name, it.lastDate, 0)
        }.reversed()
    }
}
