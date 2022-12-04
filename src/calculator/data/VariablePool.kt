package calculator.core

import java.util.concurrent.atomic.AtomicReference

class VariablePool {

    private val variables = AtomicReference<MutableMap<String, Double>>()

    fun registerVariable(variable: Pair<String, Double>) {
        variables.get()[variable.first] = variable.second
    }

    fun getVariable(variableKey: String) = variables.get()[variableKey]

}