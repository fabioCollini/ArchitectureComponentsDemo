package it.codingjam.github.util

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class FakeSharedPreferences : SharedPreferences, Editor {
    private var values = mutableMapOf<String, Any>()

    private val tempValues = mutableMapOf<String, Any>()

    override fun edit(): Editor = this

    override fun contains(key: String) = values.containsKey(key)

    override fun getAll(): Map<String, Any> = HashMap(values)

    override fun getBoolean(key: String, defValue: Boolean) = values[key] as Boolean? ?: defValue

    override fun getFloat(key: String, defValue: Float) = values[key] as Float? ?: defValue

    override fun getInt(key: String, defValue: Int) = values[key] as Int? ?: defValue

    override fun getLong(key: String, defValue: Long) = values[key] as Long? ?: defValue

    override fun getString(key: String, defValue: String?) = values[key] as String? ?: defValue

    override fun getStringSet(key: String, defValues: Set<String>?) = values[key] as Set<String>? ?: defValues

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            throw UnsupportedOperationException()

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            throw UnsupportedOperationException()

    override fun putBoolean(key: String, value: Boolean): Editor = apply { tempValues.put(key, value) }

    override fun putFloat(key: String, value: Float): Editor = apply { tempValues.put(key, value) }

    override fun putInt(key: String, value: Int): Editor = apply { tempValues.put(key, value) }

    override fun putLong(key: String, value: Long): Editor = apply { tempValues.put(key, value) }

    override fun putString(key: String, value: String): Editor = apply { tempValues.put(key, value) }

    override fun putStringSet(key: String, values: Set<String>): Editor = apply { tempValues.put(key, values) }

    override fun remove(key: String): Editor = apply { tempValues.remove(key) }

    override fun clear(): Editor = apply { tempValues.clear() }

    override fun commit(): Boolean {
        values = HashMap(tempValues)
        return true
    }

    override fun apply() {
        commit()
    }
}