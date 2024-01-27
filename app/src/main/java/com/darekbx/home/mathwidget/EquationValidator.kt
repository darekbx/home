package com.darekbx.home.mathwidget

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