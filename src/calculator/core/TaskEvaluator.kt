package calculator.core

import java.util.Stack

class TaskEvaluator {

    companion object {
        const val EXPECT_OPERATOR = "operator"
        const val EXPECT_OPERAND = "operand"

    }

    private val mainStack = Stack<StackItem>()

    /**
     * Filter out multiple ---- or +++. A mixed input (e.g. +-++---) will crash the app.
     */
    private fun sanitizeOperator(input: String): StackItem {
        //TODO: verify + and - in input :-)
        with (input.toList().groupBy { it }) {
            require( this.size == 1 )
            if (this.values.first()[0] == '-' && this.values.first().size % 2 != 0) {
                return Operator(Operator.OperatorType.SUBTRACTION)
            } else {
                return Operator(Operator.OperatorType.ADDITION)
            }
        }
    }
    fun fillStackForCalculation(task: List<String>) {
        var expectant = EXPECT_OPERAND
        for (item in task) {
            expectant = when (expectant) {
                EXPECT_OPERAND -> {
                    mainStack.push(Operand(item.toDouble()))
                    //println("added operand: ${mainStack.peek()}")
                    EXPECT_OPERATOR
                }



                EXPECT_OPERATOR -> {
                    mainStack.push(sanitizeOperator(item))
                    //println("added operator: ${mainStack.peek()}")
                    EXPECT_OPERAND
                }

                else -> throw Exception("This is unexpected: $expectant, crashing!")
            }

//            println("Stack so far: $mainStack")
//            println("Expecting $expectant")
        }

        // must not expect operand (= we do not have a trailing operator)
        check(expectant == EXPECT_OPERATOR)
    }

    fun calculateStack(): Double {

        while (mainStack.isNotEmpty()) {

            val operand1 = mainStack.pop()
            check(operand1 is Operand)

            if (mainStack.empty()) {
                return operand1.value
            }

            val operator = mainStack.pop()
            check(operator is Operator)

            val operand2 = mainStack.pop()
            check(operand2 is Operand)

            mainStack.push(operator.performOperation(operand1, operand2))
        }
        throw Exception("It's not possible to end up here :-)")
    }
}