@file:OptIn(ExperimentalTextApi::class)

package com.darekbx.weight.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import com.darekbx.weight.data.model.EntryType
import com.darekbx.weight.data.model.WeightEntry

val types = mapOf(
    EntryType.MONIKA to Color(0xFFE91E63),
    EntryType.DAREK to Color(0xFF03A9F4),
    EntryType.MICHAL to Color(0xFF4CAF50)
)

val weights = (10..70 step 5).map { it.toDouble() }

@Composable
fun ChartView(modifier: Modifier, weightEntries: List<WeightEntry>) {
    if (weightEntries.isEmpty()) {
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val chartUtils = ChartUtils(weightEntries)

    Canvas(modifier = modifier) {
        types.forEach { (type, color) ->
            chartUtils.drawEntries(this, color, weightEntries.filter { it.type == type })
        }
        weights.forEach { weight ->
            chartUtils.drawGuideLine(this, textMeasurer, weight)
        }
    }
}
