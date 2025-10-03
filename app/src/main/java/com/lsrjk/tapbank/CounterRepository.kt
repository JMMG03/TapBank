package com.lsrjk.tapbank

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tapbank_prefs")

class CounterRepository(private val context: Context) {
    private val KEY_COUNT = longPreferencesKey("count")
    private val KEY_HAPTIC = booleanPreferencesKey("haptics_enabled")

    val flow = context.dataStore.data.map { prefs: Preferences -> prefs[KEY_COUNT] }
    val hapticsFlow = context.dataStore.data.map { prefs -> prefs[KEY_HAPTIC] ?: true }

    suspend fun set(value: Long) {
        context.dataStore.edit { it[KEY_COUNT] = value }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTIC] = enabled }
    }
}
