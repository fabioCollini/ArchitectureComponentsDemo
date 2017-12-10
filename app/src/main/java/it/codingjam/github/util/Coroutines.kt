package it.codingjam.github.util

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

interface Coroutines {
    operator fun invoke(f: suspend () -> Unit)

    fun cancel()
}

class AndroidCoroutines : Coroutines {
    private val job = Job()

    override operator fun invoke(f: suspend () -> Unit) {
        async(job + UI) { f() }
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