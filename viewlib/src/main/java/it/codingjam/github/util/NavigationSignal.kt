package it.codingjam.github.util

data class NavigationSignal<P>(val destination: Any, val params: P) : Signal()