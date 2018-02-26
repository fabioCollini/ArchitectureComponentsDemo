package it.codingjam.github

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.codingjam.github.testing.SingleFragmentActivity

@Module
abstract class TestAndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSingleFragmentActivity(): SingleFragmentActivity
}