package it.codingjam.github.util

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

interface Coroutines {
    operator fun invoke(f: suspend () -> Unit)

    fun cancel()
}

class AndroidCoroutines : Coroutines {
    private val job = Job()

    override operator fun invoke(f: suspend () -> Unit) {
        launch(job + CommonPool) { f() }
    }

    override fun cancel() {
        job.cancel()
    }
}

class TestCoroutines : Coroutines {
    override fun invoke(f: suspend () -> Unit) {
        runBlocking { f() }
    }

    override fun cancel() {
    }
}