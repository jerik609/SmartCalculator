package calculator.handlers

import calculator.core.VariablePool
import calculator.debugMePrintln
import calculator.input.Input
import java.util.concurrent.atomic.AtomicReference

class VariableAssignmentHandler(private val variablePool: AtomicReference<VariablePool>): Handler {

    override fun isForMe(input: String): Boolean {
        return input.contains("=".toRegex())
    }

    override fun handle(input: String) {
        debugMePrintln(">>> variable handler <<<")

        // valid variable assignment
        if (Input.isValidVariableAssignment(input)) {
            with(input.split("=").map { it.trim() }) {

                // attempt to assign variable to variable
                if (this[1].matches("[a-zA-Z]+".toRegex())) {
                    if (variablePool.get().getVariable(this[1]) != null) {
                        debugMePrintln("assigning variable to a variable, value ${variablePool.get().getVariable(this[1])}")
                        variablePool.get().registerVariable(this[0] to variablePool.get().getVariable(this[1])!!)
                    } else {
                        println("Unknown variable")
                    }

                    // regular "number to variable" assignment
                } else {
                    variablePool.get().registerVariable(this[0] to this[1].toDouble())
                }
            }
        } else if (Input.isVariableAssignment(input)) {
            println("Invalid assignment")
        } else {
            println("Invalid identifier")
        }
    }

}