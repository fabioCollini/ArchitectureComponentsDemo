package it.codingjam.github.ui.repo

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class RepoModule {

    @ContributesAndroidInjector(modules = [RepoLocalModule::class])
    abstract fun bindRepoFragment(): RepoFragment
}

@Module
class RepoLocalModule {

    @Provides fun repoId(fragment: RepoFragment) = RepoFragment.param(fragment)
}