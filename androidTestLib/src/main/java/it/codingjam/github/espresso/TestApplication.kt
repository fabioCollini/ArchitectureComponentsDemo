package it.codingjam.github.espresso

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import androidx.test.InstrumentationRegistry
import dagger.android.*
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class TestApplication : Application(),
        HasActivityInjector,
        HasFragmentInjector,
        HasSupportFragmentInjector,
        HasServiceInjector,
        HasBroadcastReceiverInjector,
        HasContentProviderInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
    @Inject lateinit var serviceInjector: DispatchingAndroidInjector<Service>
    @Inject lateinit var contentProviderInjector: DispatchingAndroidInjector<ContentProvider>

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun fragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = supportFragmentInjector

    override fun serviceInjector(): AndroidInjector<Service> = serviceInjector

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastReceiverInjector

    override fun contentProviderInjector(): AndroidInjector<ContentProvider> = contentProviderInjector

    companion object {
        fun get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
    }
}