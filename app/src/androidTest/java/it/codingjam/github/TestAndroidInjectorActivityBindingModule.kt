package it.codingjam.github

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.codingjam.github.espresso.SingleFragmentActivity

@Module
abstract class TestAndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSingleFragmentActivity(): SingleFragmentActivity
}