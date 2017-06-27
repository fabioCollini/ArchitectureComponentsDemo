package it.codingjam.github.util

import android.arch.core.executor.AppToolkitTaskExecutor
import android.arch.core.executor.TaskExecutor
import android.os.Handler
import android.os.Looper

import org.junit.rules.TestWatcher
import org.junit.runner.Description

class EspressoInstantTaskExecutorRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        AppToolkitTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            private val lock = Any()

            @Volatile private var mainHandler: Handler? = null

            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

            override fun postToMainThread(runnable: Runnable) {
                if (mainHandler == null) {
                    synchronized(lock) {
                        if (mainHandler == null) {
                            mainHandler = Handler(Looper.getMainLooper())
                        }
                    }
                }

                mainHandler!!.post(runnable)
            }

            override fun isMainThread() = true
        })
    }

    override fun finished(description: Description?) {
        super.finished(description)
        AppToolkitTaskExecutor.getInstance().setDelegate(null)
    }
}