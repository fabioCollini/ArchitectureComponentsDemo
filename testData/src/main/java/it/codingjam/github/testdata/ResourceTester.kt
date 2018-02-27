package it.codingjam.github.testdata

import assertk.assert
import assertk.assertions.isInstanceOf
import it.codingjam.github.vo.Resource

class ResourceTester(private val list: List<Resource<*>>) {
    fun empty(): ResourceTester = check(Resource.Empty::class.java)
    fun success(): ResourceTester = check(Resource.Success::class.java)
    fun error(): ResourceTester = check(Resource.Error::class.java)
    fun loading(): ResourceTester = check(Resource.Loading::class.java)

    private fun check(clazz: Class<*>): ResourceTester {
        assert(list[0]).isInstanceOf(clazz)
        return ResourceTester(list.subList(1, list.size))
    }
}

infix fun List<Resource<*>>.shouldContain(f: ResourceTester.() -> Unit) = ResourceTester(this).f()