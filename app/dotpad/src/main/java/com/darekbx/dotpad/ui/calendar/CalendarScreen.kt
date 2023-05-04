package com.darekbx.dotpad.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.dotpad.ui.CommonLoading
import com.darekbx.dotpad.ui.dots.Dot
import com.darekbx.dotpad.ui.dots.DotSize
import com.darekbx.dotpad.ui.dots.toColor
import com.darekbx.dotpad.ui.theme.*
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.day.Day
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.selection.EmptySelectionState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

@ExperimentalFoundationApi
@Composable
fun CalendarScreen(dots: State<List<Dot>?>) {
    Box(
        Modifier
            .fillMaxHeight()
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        dots.value?.let { dotsList ->
            MakeCalendar(Modifier.align(Alignment.Center), dotsList)
        } ?: CommonLoading()
    }
}

@Composable
private fun MakeCalendar(modifier: Modifier, dots: List<Dot>) {
    StaticCalendar(
        modifier,
        showAdjacentMonths = true,
        firstDayOfWeek = DayOfWeek.MONDAY,
        dayContent = { dayState ->
            val dayDots = dots.filterByDay(dayState)
            SingleDay(Modifier, dayState, dayDots)
        },
        weekHeader = { daysOfWeek -> WeekHeader(daysOfWeek) }
    )
}

@Composable
private fun WeekHeader(daysOfWeek: List<DayOfWeek>) {
    Row {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT),
                color = Color.DarkGray,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
private fun SingleDay(
    modifier: Modifier,
    dayState: DayState<EmptySelectionState>,
    dayDots: List<Dot>
) {
    val backgroundColor = when (dayState.date.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> satSunBackground
        else -> Color.Black
    }
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            if (dayState.isFromCurrentMonth) {
                DayDots(dayDots)
            }

            DayNumber(Modifier.align(Alignment.Center), dayState)
            if (dayState.date.dayOfWeek == DayOfWeek.MONDAY) {
                val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
                Text(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = "W${dayState.date.get(weekFields.weekOfYear()) - 1}",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun DayDots(
    dayDots: List<Dot>,
    maxDayDots: Int = 3
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        dayDots
            .take(maxDayDots)
            .forEach { dot ->
                Box(
                    Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            dot
                                .requireColor()
                                .toColor()
                        )
                        .size(8.dp)
                )
            }
        if (dayDots.size > maxDayDots) {
            val diff = dayDots.size - maxDayDots
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = "+$diff",
                color = Color.Gray,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
private fun DayNumber(modifier: Modifier, dayState: DayState<EmptySelectionState>) {
    val weight = when (dayState.isCurrentDay) {
        true -> FontWeight.Bold
        else -> FontWeight.Normal
    }
    Text(
        modifier = modifier,
        text = "${dayState.date.dayOfMonth}",
        fontWeight = weight,
        color = when {
            dayState.isCurrentDay -> dotRed.toColor()
            dayState.isFromCurrentMonth -> Color.White
            else -> Color.DarkGray
        }
    )
}

@Preview("Calendar")
@Composable
fun PreviewCalendar() {
    MakeCalendar(
        Modifier
            .width(300.dp)
            .height(200.dp)
            .background(Color.Black),
        emptyList()
    )
}

@Preview("Current day")
@Composable
fun PreviewCurrentDay() {
    val day = object : Day {
        override val date: LocalDate
            get() = LocalDate.now()
        override val isCurrentDay: Boolean
            get() = true
        override val isFromCurrentMonth: Boolean
            get() = true
    }
    SingleDay(
        modifier = Modifier.size(64.dp),
        dayState = DayState(day, EmptySelectionState),
        dayDots = emptyList()
    )
}

@Preview("Simple day one dot")
@Composable
fun PreviewSimpleDay() {
    val now = Calendar.getInstance().timeInMillis
    val day = object : Day {
        override val date: LocalDate
            get() = LocalDate.now()
        override val isCurrentDay: Boolean
            get() = false
        override val isFromCurrentMonth: Boolean
            get() = true
    }
    SingleDay(
        modifier = Modifier.size(64.dp),
        dayState = DayState(day, EmptySelectionState),
        dayDots = listOf(
            Dot(1L, "Test", 0F, 0F, DotSize.SMALL, dotTeal, reminder = now)
        )
    )
}

@Preview("Simple day many dots")
@Composable
fun PreviewSimpleDayManyDots() {
    val now = Calendar.getInstance().timeInMillis
    val day = object : Day {
        override val date: LocalDate
            get() = LocalDate.now()
        override val isCurrentDay: Boolean
            get() = false
        override val isFromCurrentMonth: Boolean
            get() = true
    }
    SingleDay(
        modifier = Modifier.size(64.dp),
        dayState = DayState(day, EmptySelectionState),
        dayDots = listOf(
            Dot(1L, "Test", 0F, 0F, DotSize.SMALL, dotTeal, reminder = now),
            Dot(1L, "Test", 0F, 0F, DotSize.SMALL, dotYellow, reminder = now),
            Dot(1L, "Test", 0F, 0F, DotSize.SMALL, dotYellow, reminder = now),
            Dot(1L, "Test", 0F, 0F, DotSize.SMALL, dotPurple, reminder = now),
            Dot(1L, "Test", 0F, 0F, DotSize.SMALL, dotRed, reminder = now)
        )
    )
}


private fun List<Dot>.filterByDay(
    dayState: DayState<EmptySelectionState>
) = filter {
    val dotReminder = it.reminderCalendar
        ?: return@filter false
    val year = dotReminder.get(Calendar.YEAR)
    val day = dotReminder.get(Calendar.DAY_OF_YEAR)
    year == dayState.date.year && day == dayState.date.dayOfYear
}
