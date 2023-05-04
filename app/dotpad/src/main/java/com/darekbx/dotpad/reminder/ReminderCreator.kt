package com.darekbx.dotpad.reminder

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Reminders
import android.provider.CalendarContract.Calendars
import com.darekbx.dotpad.ui.dots.Dot
import com.darekbx.dotpad.ui.dots.toIntColor
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderCreator(
    private val contentResolver: ContentResolver,
    private val calendarEmailAddress: String
) {

    data class ReminderInfo(val eventId: Long, val reminderId: Long)

    companion object {
        private val NOTIFICATION_LIFETIME = TimeUnit.HOURS.toMillis(12)
        private const val CALENDAR_EVENTS_URI = "content://com.android.calendar/events/"
        private const val CALENDAR_REMINDERS_URI = "content://com.android.calendar/reminders//"
    }

    fun addReminder(dot: Dot): ReminderInfo {
        if (calendarEmailAddress.isBlank()) {
            throw IllegalStateException("User email is null, please set proper value in local.properties, e.g. default.user.email=\"user@email.com\"")
        }
        val eventId = createEvent(dot)
        val reminderId = createReminder(eventId)
        return ReminderInfo(eventId, reminderId)
    }

    fun removeReminder(dot: Dot) {
        contentResolver.delete(
            ContentUris.withAppendedId(
                Uri.parse(CALENDAR_EVENTS_URI), dot.calendarEventId ?: 0
            ), null, null
        )
        contentResolver.delete(
            ContentUris.withAppendedId(
                Uri.parse(CALENDAR_REMINDERS_URI), dot.calendarReminderId ?: 0
            ), null, null
        )
    }

    private fun prepareEventValues(dot: Dot): ContentValues {
        val calendarId = fetchCalendarId()
        val reminderTime = dot.reminder ?: 0L
        return ContentValues().apply {
            put(Events.DTSTART, reminderTime)
            put(Events.DTEND, reminderTime + NOTIFICATION_LIFETIME)
            put(Events.TITLE, dot.text)
            put(Events.DESCRIPTION, dot.text)
            put(Events.EVENT_COLOR, dot.color?.toIntColor())
            put(Events.CALENDAR_ID, calendarId)
            put(Events.HAS_ALARM, true)
            put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
        }
    }

    private fun createEvent(dot: Dot): Long {
        val eventContentValues = prepareEventValues(dot)
        val createdEventUri =
            contentResolver.insert(Events.CONTENT_URI, eventContentValues)
        return createdEventUri!!.lastPathSegment!!.toLong()
    }

    private fun prepareReminderValues(eventId: Long): ContentValues {
        return ContentValues().apply {
            put(Reminders.EVENT_ID, eventId)
            put(Reminders.METHOD, Reminders.METHOD_ALERT)
            put(Reminders.MINUTES, 0)
        }
    }

    private fun createReminder(eventId: Long): Long {
        val reminderContentValues = prepareReminderValues(eventId)
        val createdReminderUri = contentResolver.
        insert(Reminders.CONTENT_URI, reminderContentValues)
        return createdReminderUri!!.lastPathSegment!!.toLong()
    }

    private fun fetchCalendarId(): Long? {
        var calendarId: Long? = null

        contentResolver.query(
            Calendars.CONTENT_URI,
            arrayOf(Calendars._ID),
            "${Calendars.ACCOUNT_NAME} = ? AND ${Calendars.CALENDAR_DISPLAY_NAME} = ?",
            arrayOf(calendarEmailAddress, calendarEmailAddress),
            null
        )?.use {
            if (it.count == 0) {
                throw IllegalStateException("User was not found!")
            }
            it.moveToFirst()
            calendarId = it.getLong(0)
        }

        return calendarId
    }
}
