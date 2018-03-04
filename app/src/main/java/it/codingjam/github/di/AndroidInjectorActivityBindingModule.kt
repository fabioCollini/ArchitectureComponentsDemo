package it.codingjam.github.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.codingjam.github.MainActivity

@Module
abstract class AndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}