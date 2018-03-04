package it.codingjam.github.ui.search

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SearchModule {

    @ContributesAndroidInjector
    abstract fun bindSearchFragment(): SearchFragment
}