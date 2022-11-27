package calculator.core

class Operand(private val value: Double): StackItem {

    override fun toString(): String {
        return value.toString()
    }

}