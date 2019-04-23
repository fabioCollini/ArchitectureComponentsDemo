package it.cosenonjaviste.daggermock

import org.mockito.Mockito
import org.mockito.stubbing.Answer

fun DaggerMock.Companion.overrideComponent(c: Class<*>, componentFactory: () -> Any, testObj: Any): Any {
    val overriddenObjectsMap = OverriddenObjectsMap()
    overriddenObjectsMap.init(testObj)

    val component = try {
        componentFactory()
    } catch (e: Exception) {
        null
    }

    val defaultAnswer = Answer<Any> { invocation ->
        val method = invocation.method
        val provider = overriddenObjectsMap.getProvider(method)
        if (provider != null) {
            provider.get()
        } else {
            if (component != null) {
                method.isAccessible = true
                method.invoke(component, *invocation.arguments)
            } else {
                Mockito.mock(method.returnType)
            }
        }
    }
    return Mockito.mock(c, defaultAnswer)
}

fun DaggerMock.Companion.interceptor(testObj: Any): (Class<*>, () -> Any) -> Any {
    return { c, componentFactory ->
        DaggerMock.overrideComponent(c, componentFactory, testObj)
    }
}