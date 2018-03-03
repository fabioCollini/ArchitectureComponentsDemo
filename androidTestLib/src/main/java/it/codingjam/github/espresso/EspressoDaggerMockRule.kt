package it.codingjam.github.espresso


import it.cosenonjaviste.daggermock.DaggerMock

inline fun <reified C : TestComponent> espressoDaggerMockRule(vararg modules: Any) = DaggerMock.rule<C>(*modules) {
    set { it.inject(TestApplication.get()) }
    customizeBuilder<TestComponentBuilder> {
        it.application(TestApplication.get())
        it
    }
}

