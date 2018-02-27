package it.codingjam.github

import dagger.Module
import dagger.Provides
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.ui.repo.RepoViewModel
import it.codingjam.github.util.Coroutines

@Module open class TestModule {

    @Provides open fun provideRepoViewModel(navigationController: NavigationController, interactor: GithubInteractor, coroutines: Coroutines) =
            RepoViewModel(navigationController, interactor, coroutines)
}