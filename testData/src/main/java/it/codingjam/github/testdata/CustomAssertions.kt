package it.codingjam.github.testdata

import assertk.Assert
import assertk.assertions.isEqualTo
import it.codingjam.github.vo.Lce


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