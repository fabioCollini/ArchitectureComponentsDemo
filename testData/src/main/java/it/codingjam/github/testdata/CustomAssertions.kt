package it.codingjam.github.testdata

import assertk.Assert
import assertk.assertions.isEqualTo
import it.codingjam.github.util.ActionsFlow
import it.codingjam.github.util.StateAction
import it.codingjam.github.vo.Lce
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing


fun Assert<List<Any>>.containsLce(expected: String) = given { actual ->
    val actualString = actual.map {
        when (it) {
            is Lce.Success<*> -> "S"
            is Lce.Loading -> "L"
            is Lce.Error -> "E"
            else -> "N"
        }
    }.joinToString("")
    assertThat(actualString).isEqualTo(expected)
}

fun <T, P> Assert<List<T>>.map(extract: (T) -> P): Assert<List<P>> =
        transform { it.map(extract) }

fun <T> on(methodCall: suspend () -> T): OngoingStubbing<T> {
    return Mockito.`when`(runBlocking { methodCall() })!!
}

fun <T : Any> ActionsFlow<T>.states(initialState: T): List<Any> = runBlocking {
    fold(initialState to emptyList<Any>()) { (prevState, states), action ->
        if (action is StateAction) {
            val curState = action(prevState)
            curState to states + curState
        } else
            prevState to states + action
    }.second
}