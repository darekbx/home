package com.darekbx.home.mathwidget

import kotlin.math.max
import kotlin.random.Random

class EquationGenerator {

    private val initialValue = 10
    private val modificators = listOf('+', '-', '*')

    fun generate(level: Int): String {
        var maximum = initialValue + level
        val minimum = 5 + level
        val additionBoost = Random.nextDouble() <= 0.1

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
