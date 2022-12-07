package calculator.core

import calculator.input.InvalidExpressionException
import kotlin.math.pow

class Operator(val type: OperatorType): StackItem {

    companion object {
        public fun getOperatorFromString(input: String): Operator {
            // input must be non-empty
            require(input.isNotEmpty())
            // input must contain the same character
            val rex = when (input[0]) {
                '+' -> "\\++".toRegex()
                '*' -> "\\*+".toRegex()
                '^' -> "\\^+".toRegex()
                '(' -> "\\(+".toRegex()
                ')' -> "\\)+".toRegex()
                else -> "${input[0]}+".toRegex()
            }
            //debugMe("input: $input, regex: $rex")
            require(input.matches(rex))

            return when (input[0]) {
                '+' -> Operator(OperatorType.ADDITION)
                '-' -> Operator(OperatorType.SUBTRACTION)
                '*' -> Operator(OperatorType.MULTIPLICATION)
                '/' -> Operator(OperatorType.DIVISION)
                '^' -> Operator(OperatorType.EXPONENT)
                '(' -> Operator(OperatorType.PARENTHESES_OPENING)
                ')' -> Operator(OperatorType.PARENTHESES_CLOSING)
                else -> throw InvalidExpressionException("Invalid operator: $input")
            }
        }
    }

    // operator precedence: Parentheses, Exponents, Multiplication/Division, Addition/Subtraction
    enum class OperatorType(
        val visual: Char,
        val priority: Int,
        val operation: (Operand, Operand) -> Operand
    ) {
        ZERO_OPERATOR('0', 0, { _, _ -> Operand(0.0) }),
        ADDITION('+', 1, { operand1, operand2 -> Operand(operand1.value + operand2.value) }),
        SUBTRACTION('-', 1, { operand1, operand2 -> Operand(operand1.value - operand2.value) }),
        MULTIPLICATION('*', 2, { operand1, operand2 -> Operand(operand1.value * operand2.value) }),
        DIVISION('/', 2, { operand1, operand2 -> Operand(operand1.value / operand2.value) }),
        EXPONENT('^', 3, { operand1, operand2 -> Operand(operand1.value.pow(operand2.value)) }),
        PARENTHESES_OPENING('(', 4, { _, _ -> Operand(0.0) } ),
        PARENTHESES_CLOSING(')', 4, { _, _ -> Operand(0.0) } ), ;
    }

    override fun toString(): String {
        return type.visual.toString()
    }

    /**
     * Perform the operation defined by the operator
     * @param operand1 first input operand
     * @param operand2 second input operand
     * @return operand which is result of the operation performed on the input operands
     */
    fun performOperation(operand1: Operand, operand2: Operand): Operand {
        return type.operation(operand1, operand2)
    }

}