package calculator.core

class VariablePool(private val variables: MutableMap<String, Double> = mutableMapOf()) {

    fun registerVariable(variable: Pair<String, Double>) {
        variables[variable.first] = variable.second
    }

    fun getVariable(variableKey: String) = variables[variableKey]

}