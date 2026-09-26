package com.example.gareebi

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "gareebi_preferences"
)

class GareebiPreferences(private val context: Context) {

    companion object {
        private val MONTHLY_LIMIT =
            doublePreferencesKey("monthly_limit")
    }

    val monthlyLimit: Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[MONTHLY_LIMIT] ?: 15000.0
        }

    suspend fun setMonthlyLimit(limit: Double) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_LIMIT] = limit
        }
    }
}