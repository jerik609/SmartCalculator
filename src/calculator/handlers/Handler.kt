package calculator.handlers

interface Handler {

    fun isForMe(input: String): Boolean

    fun handle(input: String)

}