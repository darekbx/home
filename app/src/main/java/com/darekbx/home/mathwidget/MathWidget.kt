package com.darekbx.home.mathwidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

enum class Key(val label: String, val value: Int) {
    NINE("9", 9),
    EIGHT("8", 8),
    SEVEN("7", 7),

    SIX("6", 6),
    FIVE("5", 5),
    FOUR("4", 4),

    THREE("3", 3),
    TWO("2", 2),
    ONE("1", 1),

    ZERO("0", 0),
    EQUAL("=", -1),
    BACK("<", -2),
    MINUS("-", -3),
    RESET("R", -4),
    EMPTY("", -10),
}

sealed class UiState {
    object Idle : UiState()
    class WrongResult(val correct: Int) : UiState()
    object SolutionIsNaN : UiState()
}

class MathWidget : GlanceAppWidget() {

    private val level = 3

    private val generator by lazy { EquationGenerator() }
    private val validator by lazy { EquationValidator() }
    private val equationState = mutableStateOf(generator.generate(level))
    private val solutionState = mutableStateOf("")
    private val uiState = mutableStateOf<UiState>(UiState.Idle)

    companion object {
        private var LAST_EQUATION = ""
    }

    init {
        if (LAST_EQUATION.isEmpty()) {
            LAST_EQUATION = generator.generate(level)
        }
        equationState.value = LAST_EQUATION
    }

    private val onKeyPress: (Key) -> Unit = { key ->
        uiState.value = UiState.Idle
        when(key) {
            Key.BACK -> handleBack()
            Key.EQUAL -> checkSolution()
            Key.RESET -> resetEquation()
            Key.MINUS -> applyMinus()
            Key.EMPTY -> { }
            else -> addKey(key)
        }
    }

    private fun checkSolution() {
        solutionState.value.toIntOrNull()?.let { solutionInt ->
            val resultWrapper = validator.validate(equationState.value, solutionInt)
            if (resultWrapper.result) {
                newEquation()
            } else {
                uiState.value = UiState.WrongResult(resultWrapper.equationResult)
            }
        } ?: run {
            uiState.value = UiState.SolutionIsNaN
        }
    }

    private fun newEquation() {
        resetEquation()
        solutionState.value = ""
        uiState.value = UiState.Idle
    }

    private fun resetEquation() {
        LAST_EQUATION = generator.generate(level)
        equationState.value = LAST_EQUATION
        solutionState.value = ""
    }

    private fun applyMinus() {
        if (solutionState.value.isEmpty()) {
            solutionState.value = Key.MINUS.label
        }
    }

    private fun addKey(key: Key) {
        solutionState.value += "${key.value}"
    }

    private fun handleBack() {
        solutionState.value = solutionState.value.dropLast(1)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val equation by remember { equationState }
        val solution by remember { solutionState }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .cornerRadius(18.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EquationText(equation)
                SolutionText(solution)

                KeysMatrix()
            }

            uiState.value.let { state ->
                when (state) {
                    UiState.Idle -> {}
                    is UiState.SolutionIsNaN -> NaNError()
                    is UiState.WrongResult -> WrongResult(state)
                }
            }
        }
    }

    @Composable
    private fun WrongResult(state: UiState.WrongResult) {
        Box(
            modifier = GlanceModifier
                .size(128.dp, 98.dp)
                .padding(8.dp)
                .background(Color.White)
                .cornerRadius(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Correct result: ${state.correct}")
                Spacer(modifier = GlanceModifier.height(12.dp))
                Button(text = "OK", onClick = { newEquation() })
            }
        }
    }

    @Composable
    private fun NaNError() {
        Box(
            modifier = GlanceModifier
                .size(128.dp, 98.dp)
                .padding(8.dp)
                .background(Color.White)
                .cornerRadius(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Solution is NaN")
                Spacer(modifier = GlanceModifier.height(12.dp))
                Button(text = "OK", onClick = { uiState.value = UiState.Idle })
            }
        }
    }

    @Composable
    private fun SolutionText(solution: String) {
        Text(
            text = solution.ifEmpty { " " },
            modifier = GlanceModifier.padding(bottom = 4.dp),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color(200, 200, 200))
            )
        )
    }

    @Composable
    private fun EquationText(equation: String) {
        Text(
            text = equation,
            modifier = GlanceModifier.padding(bottom = 0.dp),
            style = TextStyle(fontSize = 14.sp, color = ColorProvider(Color(200, 200, 200)))
        )
    }

    @Composable
    private fun KeysMatrix() {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyButton(Key.ONE, onKeyPress)
            KeySpace()
            KeyButton(Key.TWO, onKeyPress)
            KeySpace()
            KeyButton(Key.THREE, onKeyPress)
            KeySpace()
            KeyButton(Key.RESET, onKeyPress)
        }
        RowSpace()
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyButton(Key.FOUR, onKeyPress)
            KeySpace()
            KeyButton(Key.FIVE, onKeyPress)
            KeySpace()
            KeyButton(Key.SIX, onKeyPress)
            KeySpace()
            KeyButton(Key.BACK, onKeyPress)
        }
        RowSpace()
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyButton(Key.SEVEN, onKeyPress)
            KeySpace()
            KeyButton(Key.EIGHT, onKeyPress)
            KeySpace()
            KeyButton(Key.NINE, onKeyPress)
            KeySpace()
            KeyButton(Key.MINUS, onKeyPress)
        }
        RowSpace()
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyButton(Key.EMPTY, onKeyPress)
            KeySpace()
            KeyButton(Key.ZERO, onKeyPress)
            KeySpace()
            KeyButton(Key.EMPTY, onKeyPress)
            KeySpace()
            KeyButton(Key.EQUAL, onKeyPress)
        }
    }

    @Composable
    private fun RowSpace() {
        Spacer(modifier = GlanceModifier.height(4.dp))
    }

    @Composable
    private fun KeySpace() {
        Spacer(modifier = GlanceModifier.width(4.dp))
    }

    @Composable
    private fun KeyButton(key: Key, onKeyPress: (key: Key) -> Unit) {
        val sizeModifer = GlanceModifier.size(45.dp, 39.dp)
        if (key == Key.EMPTY) {
            Spacer(modifier = sizeModifer)
        } else {
            Button(
                modifier = sizeModifer,
                text = key.label,
                style = TextStyle(fontSize = 11.sp),
                onClick = { onKeyPress(key) })
        }
    }
}
