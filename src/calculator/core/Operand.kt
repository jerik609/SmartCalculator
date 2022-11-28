package calculator.core

class Operand(value: Double): StackItem {
    var value: Double = value
        private set

    override fun toString(): String {
        return value.toString()
    }

}