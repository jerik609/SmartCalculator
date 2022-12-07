package calculator.core

import java.math.BigInteger

class Operand(value: BigInteger): StackItem {
    var value: BigInteger = value
        private set

    override fun toString(): String {
        return value.toString()
    }

}