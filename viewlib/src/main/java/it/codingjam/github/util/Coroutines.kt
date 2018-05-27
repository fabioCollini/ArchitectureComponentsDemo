package it.codingjam.github.util

import android.os.AsyncTask
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

interface Coroutines {
    operator fun invoke(f: suspend CoroutineScope.() -> Unit)

    fun cancel()

    suspend fun onUi(f: suspend () -> Unit)
}

class AndroidCoroutines : Coroutines {
    private val job = Job()

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(job + CommonPool, block = f)
    }

    override fun cancel() {
        job.cancel()
    }

    override suspend fun onUi(f: suspend () -> Unit) {
        withContext(UI, block = f)
    }
}

class TestCoroutines : Coroutines {
    override fun invoke(f: suspend CoroutineScope.() -> Unit) {
        runBlocking { f() }
    }

    override fun cancel() {
    }

    override suspend fun onUi(f: suspend () -> Unit) {
        runBlocking { f() }
    }
}

class AndroidTestCoroutines : Coroutines {
    private val job = Job()

    private val bgContext = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(job + bgContext, block = f)
    }

    override fun cancel() {
        job.cancel()
    }

    override suspend fun onUi(f: suspend () -> Unit) {
        withContext(UI, block = f)
    }
}