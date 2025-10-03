package com.lsrjk.tapbank

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tapbank_prefs")

class CounterRepository(private val context: Context) {
    private val KEY_COUNT = longPreferencesKey("count")

    val flow = context.dataStore.data.map { prefs: Preferences ->
        prefs[KEY_COUNT]
    }

    suspend fun set(value: Long) {
        context.dataStore.edit { it[KEY_COUNT] = value }
    }
}
