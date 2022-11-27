package calculator.core

class Operator(private val type: OperatorType): StackItem {

    override fun toString(): String {
        return type.visual.toString()
    }

    enum class OperatorType(val visual: Char) {
        PLUS('+'), MINUS('-'), ;
    }

}