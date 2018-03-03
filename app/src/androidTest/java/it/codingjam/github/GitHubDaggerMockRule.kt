package it.codingjam.github


import it.codingjam.github.espresso.TestApplication
import it.cosenonjaviste.daggermock.DaggerMock

fun gitHubDaggerMockRule() = DaggerMock.rule<TestComponent>(ViewLibModule()) {
    set { it.inject(TestApplication.get()) }
    customizeBuilder<TestComponent.Builder> { it.application(TestApplication.get()) }
}

