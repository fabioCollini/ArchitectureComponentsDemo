package it.codingjam.github.ui.search

import android.content.SharedPreferences
import it.codingjam.github.util.int
import it.codingjam.github.util.string

class TokenHolder(prefs: SharedPreferences) {
    var token by prefs.string()
        private set

    var count by prefs.int()
        private set

    fun saveToken(newToken: String) {
        token = newToken
        count++
    }
}