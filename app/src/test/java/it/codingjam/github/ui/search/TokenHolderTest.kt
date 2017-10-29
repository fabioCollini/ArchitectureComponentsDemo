package it.codingjam.github.ui.search

import assertk.assert
import assertk.assertions.isEqualTo
import it.codingjam.github.util.FakeSharedPreferences
import org.junit.Test

class TokenHolderTest {
@Test fun shouldCount() {
    val prefs = FakeSharedPreferences()
    val tokenHolder = TokenHolder(prefs)

    tokenHolder.saveToken("a")
    tokenHolder.saveToken("b")

    assert(tokenHolder.count).isEqualTo(2)
    assert(prefs.getInt("count", 0)).isEqualTo(2)
}
}