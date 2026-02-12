// taken from https://github.com/adrcotfas/goodtime by Adrian Cotfas
// LICENSE GPLv3
package com.waspbyte.piapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.PlatformLocale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun LocalDate.firstDayOfWeekInMonth(startDayOfWeek: DayOfWeek): LocalDate {
    val firstDayOfMonth = LocalDate(year, month, 1)
    var date = firstDayOfMonth
    while (date.dayOfWeek != startDayOfWeek) {
        date = date.plus(1, DateTimeUnit.DAY)
    }
    return date
}

fun LocalDate.firstDayOfWeekInThisWeek(startDayOfWeek: DayOfWeek): LocalDate {
    var date = this
    while (date.dayOfWeek != startDayOfWeek) {
        date = date.minus(1, DateTimeUnit.DAY)
    }
    return date
}

fun LocalDate.endOfWeekInThisWeek(startDayOfWeek: DayOfWeek): LocalDate {
    var date = this
    if (date.dayOfWeek == startDayOfWeek) {
        return date.plus(DatePeriod(days = 6))
    }
    while (date.dayOfWeek != startDayOfWeek) {
        date = date.plus(DatePeriod(days = 1))
    }
    date.minus(DatePeriod(days = 1))
    return date
}

@OptIn(ExperimentalTime::class)
fun currentDateTime(): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now().toEpochMilliseconds()
    val currentInstant = Instant.fromEpochMilliseconds(now)
    return currentInstant.toLocalDateTime(timeZone)
}

inline fun <reified T : Enum<T>> T.entriesStartingWithThis(): List<T> {
    val entries = enumValues<T>()
    val index = entries.indexOf(this)
    return entries.drop(index) + entries.take(index)
}

// taken from https://github.com/adrcotfas/kotlinx-datetime-names by Adrian Cotfas
// License Apache-2.0
enum class TextStyle {
    FULL,
    FULL_STANDALONE,
    SHORT,
    SHORT_STANDALONE,
    NARROW,
    NARROW_STANDALONE,
}

internal val textStyleMapping =
    mapOf(
        TextStyle.FULL to java.time.format.TextStyle.FULL,
        TextStyle.FULL_STANDALONE to java.time.format.TextStyle.FULL_STANDALONE,
        TextStyle.SHORT to java.time.format.TextStyle.SHORT,
        TextStyle.SHORT_STANDALONE to java.time.format.TextStyle.SHORT_STANDALONE,
        TextStyle.NARROW to java.time.format.TextStyle.NARROW,
        TextStyle.NARROW_STANDALONE to java.time.format.TextStyle.NARROW_STANDALONE,
    )

fun DayOfWeek.getDisplayName(
    textStyle: TextStyle,
    locale: PlatformLocale = PlatformLocale.getDefault(),
): String {
    val javaTextStyle =
        textStyleMapping[textStyle]
            ?: error("Unknown TextStyle: $textStyle")
    return java.time.DayOfWeek
        .of(this.isoDayNumber)
        .getDisplayName(javaTextStyle, locale)
}

fun Month.getDisplayName(
    textStyle: TextStyle,
    locale: PlatformLocale = PlatformLocale.getDefault(),
): String {
    val javaTextStyle =
        textStyleMapping[textStyle]
            ?: error("Unknown TextStyle: $textStyle")
    return java.time.Month
        .of(this.ordinal + 1)
        .getDisplayName(javaTextStyle, locale)
}

fun getLocalizedMonthNamesForStats(): List<String> {
    val localizedMonthNamesShort =
        Month.entries.map { it.getDisplayName(textStyle = TextStyle.SHORT_STANDALONE) }
    return if (localizedMonthNamesShort.any { it.length > 3 }) {
        val localizedMonthNamesNarrow =
            Month.entries.map { it.getDisplayName(textStyle = TextStyle.NARROW_STANDALONE) }
        return localizedMonthNamesNarrow
    } else {
        localizedMonthNamesShort
    }
}

fun getLocalizedDayNamesForStats(): List<String> {
    val localizedDayNamesShort =
        DayOfWeek.entries.map { it.getDisplayName(textStyle = TextStyle.SHORT_STANDALONE) }
    return if (localizedDayNamesShort.any { it.length > 3 }) {
        val localizedDayNamesNarrow =
            DayOfWeek.entries.map { it.getDisplayName(textStyle = TextStyle.NARROW_STANDALONE) }
        return localizedDayNamesNarrow
    } else {
        localizedDayNamesShort
    }
}

typealias HeatmapData = Map<LocalDate, Float>

fun LocalDate.at(firstDayOfWeek: DayOfWeek): LocalDate {
    var date = this
    while (date.dayOfWeek != firstDayOfWeek) {
        date = date.plus(1, DateTimeUnit.DAY)
    }
    return date
}

fun convertSpToDp(
    density: Density,
    spValue: Float,
): Float = with(density) { spValue.sp.toDp().value }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeatmapSection(
    firstDayOfWeek: DayOfWeek,
    data: HeatmapData,
    color: Color = colorResource(R.color.theme_primary),
) {
    val density = LocalDensity.current
    val endLocalDate = remember { currentDateTime().date }
    val startLocalDate = remember { endLocalDate.minus(DatePeriod(days = 363)) }

    val startAtStartOfWeek = remember { startLocalDate.firstDayOfWeekInThisWeek(firstDayOfWeek) }
    val endAtEndOfWeek = remember { endLocalDate.endOfWeekInThisWeek(firstDayOfWeek) }
    val numberOfWeeks =
        remember {
            if (startLocalDate.daysUntil(endAtEndOfWeek) % 7 == 0) {
                52
            } else {
                53
            }
        }

    val fontSizeStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Thin)
    val cellSize = remember { (convertSpToDp(density, fontSizeStyle.fontSize.value) * 1.5f).dp }
    val cellSpacing = remember { cellSize / 6f }
    val daysInOrder = remember { firstDayOfWeek.entriesStartingWithThis() }

    val monthNames = remember { getLocalizedMonthNamesForStats() }
    val dayNames = remember { getLocalizedDayNamesForStats() }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = numberOfWeeks - 1)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Heatmap",
                style =
                    MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = color,
                    ),
            )
        }

        CompositionLocalProvider(
            LocalOverscrollFactory provides null,
        ) {
            Row(
                modifier =
                    Modifier
                        .wrapContentSize()
                        .background(colorResource(R.color.theme_surface))
                        .padding(
                            top = 16.dp,
                            bottom = 16.dp,
                            start = cellSize,
                            end = 32.dp,
                        ),
            ) {
                Column(
                    modifier =
                        Modifier
                            .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(cellSize),
                    )
                    val labeledDays = mutableListOf(1, 3, 5)

                    daysInOrder.forEach {
                        if (labeledDays.contains(it.isoDayNumber)) {
                            Text(
                                modifier =
                                    Modifier
                                        .padding(cellSpacing)
                                        .height(cellSize),
                                text = dayNames[it.ordinal],
                                style = MaterialTheme.typography.labelSmall,
                                color = colorResource(R.color.theme_onSurface)
                            )
                        } else {
                            Box(
                                modifier =
                                    Modifier
                                        .padding(cellSpacing)
                                        .size(cellSize),
                            )
                        }
                    }
                }
                Column {
                    LazyRow(
                        modifier = Modifier.wrapContentHeight(),
                        state = listState,
                        flingBehavior = rememberSnapFlingBehavior(listState, SnapPosition.Start),
                    ) {
                        items(numberOfWeeks) { index ->
                            Column(
                                modifier =
                                    Modifier
                                        .wrapContentHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                val currentWeekStart =
                                    remember(index) { startAtStartOfWeek.plus(DatePeriod(days = index * 7)) }

                                val monthName = monthNames[currentWeekStart.month.ordinal]
                                if (currentWeekStart == startAtStartOfWeek ||
                                    currentWeekStart ==
                                    currentWeekStart.firstDayOfWeekInMonth(
                                        firstDayOfWeek,
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier.height(cellSize),
                                        text = monthName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorResource(R.color.theme_onSurface)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(cellSize))
                                }

                                daysInOrder.forEach { dayOfWeek ->
                                    val currentDay =
                                        remember(
                                            index,
                                            dayOfWeek,
                                        ) { currentWeekStart.at(dayOfWeek) }
                                    if (currentDay in startLocalDate..endLocalDate) {
                                        Box(modifier = Modifier.padding(cellSpacing)) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .size(cellSize)
                                                        .clip(MaterialTheme.shapes.extraSmall)
                                                        .background(
                                                            colorResource(R.color.theme_surfaceContainer).copy(
                                                                alpha = 0.5f,
                                                            ),
                                                        ),
                                            )
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .size(cellSize)
                                                        .clip(MaterialTheme.shapes.extraSmall)
                                                        .background(
                                                            color.copy(
                                                                alpha =
                                                                    data[currentDay]?.plus(0.2f)
                                                                        ?: 0f,
                                                            ),
                                                        ),
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .padding(cellSpacing)
                                                    .size(cellSize),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}