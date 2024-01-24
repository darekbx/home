package com.example.testapplication

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
import kotlin.math.max
import kotlin.random.Random

/*
implementation("androidx.glance:glance-appwidget:1.0.0")
implementation("androidx.glance:glance-material3:1.0.0")
*/

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

    private val level = 4

    private val generator by lazy { EquationGenerator() }
    private val validator by lazy { EquationValidator() }
    private val equationState = mutableStateOf(generator.generate(level))
    private val solutionState = mutableStateOf("")
    private val uiState = mutableStateOf<UiState>(UiState.Idle)

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
        // TODO increase statistics
        resetEquation()
        solutionState.value = ""
        uiState.value = UiState.Idle
    }

    private fun resetEquation() {
        equationState.value = generator.generate(level)
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

                SummaryRow(12, 432)
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
    private fun SummaryRow(bad: Int, good: Int) {
        Row(modifier = GlanceModifier.padding(top = 4.dp)) {
            Text(
                text = "$bad/$good",
                style = TextStyle(fontSize = 10.sp, color = ColorProvider(Color(170, 170, 170)))
            )
        }
    }

    @Composable
    private fun SolutionText(solution: String) {
        Text(
            text = solution,
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
            KeyButton(Key.NINE, onKeyPress)
            KeySpace()
            KeyButton(Key.EIGHT, onKeyPress)
            KeySpace()
            KeyButton(Key.SEVEN, onKeyPress)
            KeySpace()
            KeyButton(Key.RESET, onKeyPress)
        }
        RowSpace()
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyButton(Key.SIX, onKeyPress)
            KeySpace()
            KeyButton(Key.FIVE, onKeyPress)
            KeySpace()
            KeyButton(Key.FOUR, onKeyPress)
            KeySpace()
            KeyButton(Key.BACK, onKeyPress)
        }
        RowSpace()
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyButton(Key.THREE, onKeyPress)
            KeySpace()
            KeyButton(Key.TWO, onKeyPress)
            KeySpace()
            KeyButton(Key.ONE, onKeyPress)
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
        val sizeModifer = GlanceModifier.size(37.dp, 34.dp)
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

class EquationGenerator {

    private val initialValue = 10
    private val modificators = listOf('+', '-', '*')

    fun generate(level: Int): String {
        var maximum = initialValue + level
        val minimum = 5 + level
        val additionBoost = Random.nextDouble() <= 0.33

        if (additionBoost) {
            maximum *= (level + 2)
        }

        val minimumFirst = minimum - 1
        val minimumSecond = minimum + 1
        val minimumThird = minimum

        var firstModificator = randomModificator()
        var secondModificator = randomModificator()

        if (additionBoost) {
            firstModificator = randomAddSubModificator()
            secondModificator = randomAddSubModificator()
        }

        val firstNumber = randomInt(minimumFirst, maximum)
        val secondNumber = randomInt(minimumSecond, maximum)
        val thirdNumber = randomInt(minimumThird, maximum)

        return "$firstNumber $firstModificator $secondNumber $secondModificator $thirdNumber"
    }

    private fun randomModificator() = modificators[Random.nextInt(modificators.size)]

    private fun randomAddSubModificator() = modificators[Random.nextInt(modificators.size - 1)]

    private fun randomInt(minimum: Int, maximum: Int) = Random.nextInt(max(minimum, 1), minimum + maximum)
}

class EquationValidator {

    class ResultWrapper(val result: Boolean, val equationResult: Int)

    fun validate(equation: String, result: Int): ResultWrapper {
        val chunks = equation.split(" ")

        val firstNumber = chunks[0].toInt()
        val secondNumber = chunks[2].toInt()
        val thirdNumber = chunks[4].toInt()

        val firstManipulator = chunks[1]
        val secondManipulator = chunks[3]

        if (isMultiplicator(firstManipulator) && isMultiplicator(secondManipulator)) {
            val equationResult = firstNumber * secondNumber * thirdNumber
            return ResultWrapper(equationResult == result, equationResult)
        }

        if (isMultiplicator(firstManipulator)) {
            val firstValue = firstNumber * secondNumber

            if (isAddition(secondManipulator)) {
                val equationResult = firstValue + thirdNumber
                return ResultWrapper(equationResult == result, equationResult)
            } else if (isSubtraction(secondManipulator)) {
                val equationResult = firstValue - thirdNumber
                return ResultWrapper(equationResult == result, equationResult)
            }
        }

        if (isMultiplicator(secondManipulator)) {
            val secondValue = secondNumber * thirdNumber

            if (isAddition(firstManipulator)) {
                val equationResult = firstNumber + secondValue
                return ResultWrapper(equationResult == result, equationResult)
            } else if (isSubtraction(firstManipulator)) {
                val equationResult = firstNumber - secondValue
                return ResultWrapper(equationResult == result, equationResult)
            }
        }

        val firstValue = when (isAddition(firstManipulator)) {
            true -> firstNumber + secondNumber
            else -> firstNumber - secondNumber
        }

        val equationResult = when (isAddition(secondManipulator)) {
            true -> firstValue + thirdNumber
            else -> firstValue - thirdNumber
        }

        return ResultWrapper(equationResult == result, equationResult)
    }

    private fun isMultiplicator(manipulator: String) = manipulator == "*"
    private fun isAddition(manipulator: String) = manipulator == "+"
    private fun isSubtraction(manipulator: String) = manipulator == "-"
}