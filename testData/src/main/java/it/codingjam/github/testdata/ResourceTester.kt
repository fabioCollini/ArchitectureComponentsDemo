package it.codingjam.github.testdata

import assertk.assert
import assertk.assertions.isInstanceOf
import it.codingjam.github.vo.Lce

class ResourceTester(private val list: List<Lce<*>>) {
    fun success(): ResourceTester = check(Lce.Success::class.java)
    fun error(): ResourceTester = check(Lce.Error::class.java)
    fun loading(): ResourceTester = check(Lce.Loading::class.java)

    private fun check(clazz: Class<*>): ResourceTester {
        assert(list[0]).isInstanceOf(clazz)
        return ResourceTester(list.subList(1, list.size))
    }
}

infix fun List<Lce<*>>.shouldContain(f: ResourceTester.() -> Unit) = ResourceTester(this).f()