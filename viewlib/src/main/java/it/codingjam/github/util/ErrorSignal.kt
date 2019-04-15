package it.codingjam.github.util

data class ErrorSignal(val error: Throwable?, val message: String) : Signal() {
    constructor(t: Throwable) : this(t, t.message ?: "Error ${t.javaClass.name}")
}