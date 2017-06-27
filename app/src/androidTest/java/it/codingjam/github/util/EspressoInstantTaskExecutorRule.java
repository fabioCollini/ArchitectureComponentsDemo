package it.codingjam.github.util;

import android.arch.core.executor.AppToolkitTaskExecutor;
import android.arch.core.executor.TaskExecutor;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class EspressoInstantTaskExecutorRule extends TestWatcher {

    @Override
    protected void starting(Description description) {
        super.starting(description);
        AppToolkitTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            private final Object mLock = new Object();

            @Nullable
            private volatile Handler mMainHandler;

            @Override
            public void executeOnDiskIO(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(Runnable runnable) {
                if (mMainHandler == null) {
                    synchronized (mLock) {
                        if (mMainHandler == null) {
                            mMainHandler = new Handler(Looper.getMainLooper());
                        }
                    }
                }
                //noinspection ConstantConditions
                mMainHandler.post(runnable);
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
        AppToolkitTaskExecutor.getInstance().setDelegate(null);
    }
}