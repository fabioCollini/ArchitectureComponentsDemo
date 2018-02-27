package it.codingjam.github.espresso

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TestAndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSingleFragmentActivity(): SingleFragmentActivity
}