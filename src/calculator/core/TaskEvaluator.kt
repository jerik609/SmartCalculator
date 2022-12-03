package calculator.core

import calculator.main
import java.util.*

class TaskEvaluator {

    companion object {
        const val EXPECT_OPERATOR = "operator"
        const val EXPECT_OPERAND = "operand"

    }

    private val mainStack = Stack<StackItem>()

    /**
     * Evaluates the stack until only one element remains, which will be left on the stack
     */
    private fun eatStack() {
        while (mainStack.size != 1) {
            val rightOperand = mainStack.pop() as Operand
            val operator = mainStack.pop() as Operator
            val leftOperand = mainStack.pop() as Operand
            mainStack.push(operator.performOperation(leftOperand, rightOperand))
        }
        //println("after stack is eaten: ${mainStack.peek().toString()}")
    }

    fun processInput(words: List<String>): Double {

        println("input: $words")

        // empty, a dud
        if (words.isEmpty()) {
            println("nothing, empty term")
            return 0.0
        }

        // the iterator
        val word = words.iterator()

        // one word, must be an operand
        if (words.size == 1) {
            // let it crash if no double, whatever, own fault
            println("just one fella here, must be an operand")
            return word.next().toDouble()
        }

        require(words.size >= 3) // anything else would be nonsense and deserves a crash

        mainStack.push(Operand(word.next().toDouble()))
        mainStack.push(Operator.getOperatorFromString(word.next()))

        /**
         * "the look ahead" logic
         *
         * if "look ahead" operator's priority is higher, then put on stack (cant evaluate for now)
         * if "look ahead" operator's priority is same, then evaluate in forward direction (safe to do)
         * if "look ahead" operator's priority is lower, then do not evaluate further, but consume stack until empty
         *
         * The last one deserves an explanation - the problem would be if we had same level operations on stack
         *   then we would not read left-to-right, but right-to-left ... but we cannot have same level operations
         *   because rule "if same priority, evaluate" would not have been applied.
         */
        //println("ready for main loop --- beginning")

        while (true) {

            //!!! if queue has depth 1 we're done!
            if (mainStack.size == 1) {
                println("main stack size == 1")
                break
            }

            // stack: ... X1 +_1 X2 +_2 X3 +_3 <<< we're here

            val operator1 = mainStack.pop() as Operator // +_3
            val operand1 = mainStack.pop() as Operand // X3

            // words: we're here >>> X4 + x_4 + X5 + x_5 ...
            val operand2 = Operand(word.next().toDouble()) // X4

            println("$operand1 $operator1 $operand2")

            // this will be the end!
            if (!word.hasNext()) {
                //println("the end, just finish stack and done")
                mainStack.push(operator1.performOperation(operand1, operand2))
                eatStack()
                break
                // there's a next word
            } else {
                val lookAhead = Operator.getOperatorFromString(word.next())
                println("the look ahead: $lookAhead")

                // if "look ahead" operator's priority is lower, calculate operation and consume stack until empty
                if (operator1.type.priority > lookAhead.type.priority) {
                    println("push calculation and eat stack")
                    mainStack.push(operator1.performOperation(operand1, operand2))
                    eatStack()

                    // if "look ahead" operator's priority is same, calculate operation and put on stack
                } else if (operator1.type.priority == lookAhead.type.priority) {
                    println("same priority, just push result")
                    mainStack.push(operator1.performOperation(operand1, operand2))

                    // if "look ahead" operator's priority is higher, only put on stack (cant evaluate for now)
                } else {
                    println("higher priority, no result, just push")
                    mainStack.push(operand1)
                    mainStack.push(operator1)
                    mainStack.push(operand2)
                }

                mainStack.push(lookAhead)

            }
        }
        //println("done main loop: ${mainStack.peek().toString()}")

        return (mainStack.pop() as Operand).value
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