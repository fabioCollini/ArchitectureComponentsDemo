package it.codingjam.github.ui.user

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import it.codingjam.github.ui.common.getParam

@Module
abstract class UserModule {

    @ContributesAndroidInjector(modules = [UserLocalModule::class])
    abstract fun bindUserFragment(): UserFragment
}

@Module
class UserLocalModule {

    @Provides fun param(fragment: UserFragment) = UserFragment.getParam(fragment)
}