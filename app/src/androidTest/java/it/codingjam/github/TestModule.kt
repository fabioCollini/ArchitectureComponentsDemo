package it.codingjam.github

import dagger.Module
import dagger.Provides
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.ui.repo.RepoViewModel
import it.codingjam.github.util.Coroutines
import javax.inject.Singleton

@Module open class TestModule {

    @Provides open fun provideRepoViewModel(navigationController: NavigationController, repository: RepoRepository, coroutines: Coroutines) =
            RepoViewModel(navigationController, repository, coroutines)

    @Provides @Singleton open fun navigationController() = NavigationController()
}