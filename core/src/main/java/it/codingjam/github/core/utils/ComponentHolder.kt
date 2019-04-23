package it.codingjam.github.core.utils

interface ComponentHolder {
    fun <C : Any> getOrCreate(key: Any, componentClass: Class<C>, componentFactory: () -> C): C

    fun remove(key: Any)

    fun init(interceptor: (Class<*>, () -> Any) -> Any)
}

inline fun <reified C : Any> ComponentHolder.getOrCreate(noinline componentFactory: () -> C): C =
        getOrCreate(C::class.java, C::class.java, componentFactory)

inline fun <reified C : Any> ComponentHolder.get(): C =
        getOrCreate(C::class.java, C::class.java) {
            throw Exception("Component ${C::class.java.simpleName} not available in ${this::class.java.simpleName}")
        }

inline fun <reified C : Any> ComponentHolder.provide(noinline componentFactory: () -> C) {
    getOrCreate(C::class.java, C::class.java, componentFactory)
}

class ComponentsMap : ComponentHolder {
    private var interceptor: (Class<*>, () -> Any) -> Any = { _, factory -> factory() }

    private val moduleComponents = HashMap<Any, Any>()

    override fun <C : Any> getOrCreate(key: Any, componentClass: Class<C>, componentFactory: () -> C): C {
        @Suppress("UNCHECKED_CAST")
        return moduleComponents.getOrPut(key) {
            interceptor(componentClass, componentFactory)
        } as C
    }

    override fun init(interceptor: (Class<*>, () -> Any) -> Any) {
        this.interceptor = interceptor
        moduleComponents.clear()
    }

    override fun remove(key: Any) {
        moduleComponents.remove(key)
    }
}