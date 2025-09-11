package com.darekbx.spreadsheet.ui.grid.functions

enum class FunctionType {
    SUM, AVG, MIN, MAX, EMPTY
}

data class Function(val type: FunctionType, val range: List<Int> = emptyList()) {

    companion object {
        fun String?.toFunction(): Result<Function> {
            if (this == null || this.isEmpty()) {
                return Result.success(Function(FunctionType.EMPTY))
            }

            val type = when {
                this.startsWith("=SUM") -> FunctionType.SUM
                this.startsWith("=AVG") -> FunctionType.AVG
                this.startsWith("=MIN") -> FunctionType.MIN
                this.startsWith("=MAX") -> FunctionType.MAX
                else -> return Result.failure(IllegalArgumentException("Unknown function type"))
            }

            val rangeStart = this.indexOf('(')
            val rangeEnd = this.indexOf(')')

            if (rangeIsNotValid(rangeStart, rangeEnd)) {
                return Result.failure(IllegalArgumentException("Invalid function format"))
            }

            val rangeString = this.substring(rangeStart + 1, rangeEnd)
            val hasRange = rangeString.contains(RANGE_SEPARATOR)
            val hasMultipleFields = rangeString.contains(MULTI_FIELD_SEPARATOR)

            val range = try {
                if (hasRange && !hasMultipleFields) createSingleRange(rangeString)
                else if (!hasRange && hasMultipleFields) createMultipleFieldsRange(rangeString)
                else createCombinedRange(rangeString)
            } catch (_: Exception) {
                return Result.failure(IllegalArgumentException("Invalid range values"))
            }

            return Result.success(Function(type, range))
        }

        private fun rangeIsNotValid(rangeStart: Int, rangeEnd: Int): Boolean =
            rangeStart == -1 || rangeEnd == -1 || rangeEnd <= rangeStart || rangeStart + 1 == rangeEnd

        private fun createCombinedRange(rangeString: String): MutableList<Int> {
            val range = mutableListOf<Int>()
            val parts = rangeString.split(MULTI_FIELD_SEPARATOR)
            for (part in parts) {
                if (part.contains(RANGE_SEPARATOR)) {
                    range.addAll(createSingleRange(part))
                } else {
                    range.add(part.toInt())
                }
            }
            return range
        }

        private fun createSingleRange(rangeString: String): List<Int> {
            val parts = rangeString.split(RANGE_SEPARATOR)
            if (parts.size != 2) {
                throw IllegalArgumentException("Invalid range format")
            }
            val start = parts[0].toIntOrNull()
            val end = parts[1].toIntOrNull()
            if (start == null || end == null || start > end) {
                throw IllegalArgumentException("Invalid range values")
            }
            return (start..end).toList()
        }

        private fun createMultipleFieldsRange(rangeString: String): List<Int> {
            return rangeString.split(MULTI_FIELD_SEPARATOR).map { it.toInt() }
        }

        private const val RANGE_SEPARATOR = ':'
        private const val MULTI_FIELD_SEPARATOR = ','
    }
}
