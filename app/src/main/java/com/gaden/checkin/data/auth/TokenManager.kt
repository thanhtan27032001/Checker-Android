package com.gaden.checkin.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        val EMPLOYEE_ID = stringPreferencesKey("EMPLOYEE_ID")
    }

    val employeeIdFlow: Flow<String?> = context.dataStore.data.map { it[Keys.EMPLOYEE_ID] }
    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }

    suspend fun saveSession(accessToken: String, employeeId: String) {
        context.dataStore.edit {
            it[Keys.ACCESS_TOKEN] = accessToken
            it[Keys.EMPLOYEE_ID] = employeeId
        }
    }
}