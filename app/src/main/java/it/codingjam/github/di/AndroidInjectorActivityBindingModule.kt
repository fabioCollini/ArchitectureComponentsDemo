package it.codingjam.github.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.codingjam.github.MainActivity
import it.codingjam.github.ui.repo.RepoFragment
import it.codingjam.github.ui.search.SearchFragment
import it.codingjam.github.ui.user.UserFragment

@Module
abstract class AndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun bindUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun bindSearchFragment(): SearchFragment
}