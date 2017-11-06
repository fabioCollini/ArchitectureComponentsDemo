package it.codingjam.github

import dagger.Module
import dagger.Provides
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.ui.repo.RepoViewModel
import javax.inject.Singleton

@Module open class TestModule {

    @Provides open fun provideRepoViewModel(navigationController: NavigationController, repository: RepoRepository) =
            RepoViewModel(navigationController, repository)

    @Provides @Singleton open fun navigationController() = NavigationController()
}