package calculator.core

import kotlin.math.pow

class Operator(private val type: OperatorType): StackItem {

    companion object {
        fun getOperatorFromString(input: String): Operator {
            // input must be non-empty
            require(input.isNotEmpty())
            // input must contain the same character
            require(input.contains("${input[0]}+".toRegex()))







        }









        }
    }

    // operator precedence: Parentheses, Exponents, Multiplication/Division, Addition/Subtraction
    enum class OperatorType(
        val visual: Char,
        val priority: Int,
        val operation: (Operand, Operand) -> Operand
    ) {
        ADDITION('+', 1, { operand1, operand2 -> Operand(operand1.value + operand2.value) }),
        SUBTRACTION('-', 1, { operand1, operand2 -> Operand(operand1.value - operand2.value) }),
        MULTIPLICATION('*', 2, { operand1, operand2 -> Operand(operand2.value * operand1.value) }),
        DIVISION('/', 2, { operand1, operand2 -> Operand(operand2.value / operand1.value) }),
        EXPONENT('^', 3, { operand1, operand2 -> Operand(operand1.value.pow(operand2.value)) }) ;
        // PARENTHESES_?('(', 4, { operand1, operand2 -> ??? } ) - tough cookie
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