package calculator.core

class Operator(private val type: OperatorType): StackItem {

    override fun toString(): String {
        return type.visual.toString()
    }

    fun performOperation(operand1: Operand, operand2: Operand): Operand {
        return Operand(type.operation(operand1, operand2))
    }

    enum class OperatorType(val visual: Char, val operation: (Operand, Operand) -> Double ) {
        PLUS('+', { operand1, operand2 -> operand1.value + operand2.value } ),
        MINUS('-', { operand1, operand2 -> operand2.value - operand1.value } ) ;
    }

}