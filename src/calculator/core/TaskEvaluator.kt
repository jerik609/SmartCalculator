package calculator.core

import java.util.Scanner
import java.util.Stack

class TaskEvaluator(private val scanner: Scanner) {

    companion object {
        const val EXPECT_OPERATOR = "operator"
        const val EXPECT_OPERAND = "operand"

    }

    val mainStack = Stack<StackItem>()

    private fun sanitizeOperator(input: String): StackItem {
        // Filter out multiple ---- or +++. The +-++--- will crash the app
        with (input.toList().groupBy { it }) {
            require( this.size == 1 )
            if (this.values.first()[0] == '-' && this.values.first().size % 2 != 0) {
                return Operator(Operator.OperatorType.MINUS)
            } else {
                return Operator(Operator.OperatorType.PLUS)
            }
        }
    }

    // 9 +++ 10 -- 8 --- 1 ++ 3

    fun fillStackForCalculation() {

        var expectant = EXPECT_OPERAND
        while (scanner.hasNext()) {

            if (expectant == EXPECT_OPERAND) {
                mainStack.push(Operand(scanner.nextDouble()))
                println("added operand: ${mainStack.peek()}")
                expectant = EXPECT_OPERATOR
            } else if (expectant == EXPECT_OPERATOR) {
                mainStack.push(sanitizeOperator(scanner.next()))
                println("added operator: ${mainStack.peek()}")
                expectant = EXPECT_OPERAND
            } else {
                throw Exception("WOW SOMETHING IS MESSED UP! $expectant")
            }

            println("Stack so far: $mainStack")
        }
        // must not expect operand (= we do not have a trailing operator)
        check(expectant == EXPECT_OPERAND)

    }

}