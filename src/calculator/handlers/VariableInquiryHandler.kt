package calculator.handlers

import calculator.core.VariablePool
import calculator.input.Input

class VariableInquiryHandler(private val variablePool: VariablePool): Handler {

    override fun isForMe(input: String): Boolean {
        val inputList = input.split(" ").map { it.trim() }
        return inputList.size == 1 && inputList[0].contains("[a-zA-Z]".toRegex())
    }

    override fun handle(input: String) {
        if (Input.isVariableInquiry(input)) {
            println(variablePool.getVariable(input.trim())?.toInt() ?: "Unknown variable")
        } else {
            println("Invalid identifier")
        }
    }

}