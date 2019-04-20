package it.codingjam.github.core

import dagger.Component
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.get
import it.codingjam.github.core.utils.getOrCreate
import javax.inject.Singleton

interface CoreComponent {
    val githubInteractor: GithubInteractor
}

@Singleton
@Component(
        dependencies = [CoreDependencies::class]
)
interface CoreComponentImpl : CoreComponent {
    @Component.Factory
    interface Factory {
        fun create(dependencies: CoreDependencies): CoreComponent
    }
}

val ComponentHolder.coreComponent
    get() = getOrCreate {
        DaggerCoreComponentImpl.factory().create(get())
    }

interface CoreDependencies {
    val githubRepository: GithubRepository
}
