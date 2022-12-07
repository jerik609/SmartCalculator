package calculator.data

import java.math.BigInteger
import java.util.concurrent.atomic.AtomicReference

class VariablePool {

    private val variables = AtomicReference<MutableMap<String, BigInteger>>(mutableMapOf())

    fun registerVariable(variable: Pair<String, BigInteger>) {
        variables.get()[variable.first] = variable.second
    }

    fun getVariable(variableKey: String) = variables.get()[variableKey]

}