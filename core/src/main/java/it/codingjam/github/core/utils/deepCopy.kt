package it.codingjam.github.core.utils

inline fun <C1, C2> C1.deepCopy(
        field1: C1.() -> C2, f1: C1.(C2) -> C1,
        f: C2.(C1) -> C2): C1 {
    val value2 = field1()
    val newValue2 = value2.f(this)
    return f1(newValue2)
}

inline fun <C1, C2, C3> C1.deepCopy(
        field1: C1.() -> C2, f1: C1.(C2) -> C1,
        field2: C2.() -> C3, f2: C2.(C3) -> C2,
        f: C3.(C2) -> C3): C1 {
    val value2 = field1()
    val value3 = value2.field2()
    val newValue3 = value3.f(value2)
    val newValue2 = value2.f2(newValue3)
    return f1(newValue2)
}

inline fun <C1, C2, C3, C4> C1.deepCopy(
        field1: C1.() -> C2, f1: C1.(C2) -> C1,
        field2: C2.() -> C3, f2: C2.(C3) -> C2,
        field3: C3.() -> C4, f3: C3.(C4) -> C3,
        f: C4.(C3) -> C4): C1 {
    val value2 = field1()
    val value3 = value2.field2()
    val value4 = value3.field3()
    val newValue4 = value4.f(value3)
    val newValue3 = value3.f3(newValue4)
    val newValue2 = value2.f2(newValue3)
    return f1(newValue2)
}
