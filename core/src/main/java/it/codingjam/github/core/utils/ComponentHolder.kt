package it.codingjam.github.core.utils

interface ComponentHolder {
    fun <C : Any> getOrCreate(componentClass: Class<C>, componentFactory: () -> C): C

    fun init(interceptor: (Class<*>, () -> Any) -> Any) {
    }
}

inline fun <reified C : Any> ComponentHolder.getOrCreate(noinline componentFactory: () -> C): C =
        getOrCreate(C::class.java, componentFactory)

inline fun <reified C : Any> ComponentHolder.get(): C =
        getOrCreate(C::class.java) {
            throw Exception("Component ${C::class.java.simpleName} not available in ${this::class.java.simpleName}")
        }

open class ComponentsMap : ComponentHolder {
    val moduleComponents = HashMap<Class<*>, Any>()

    override fun <C : Any> getOrCreate(componentClass: Class<C>, componentFactory: () -> C): C =
            moduleComponents.getOrPut(componentClass, componentFactory) as C

}

inline fun <reified C : Any> ComponentHolder.provide(noinline componentFactory: () -> C) {
    getOrCreate(C::class.java, componentFactory)
}

class TestComponentsMap : ComponentsMap() {
    private lateinit var interceptor: (Class<*>, () -> Any) -> Any

    override fun <C : Any> getOrCreate(componentClass: Class<C>, componentFactory: () -> C): C {
        return super.getOrCreate(componentClass) { interceptor(componentClass, componentFactory) as C }
    }

    override fun init(interceptor: (Class<*>, () -> Any) -> Any) {
        this.interceptor = interceptor
        moduleComponents.clear()
    }
}