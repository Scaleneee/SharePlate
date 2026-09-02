package com.example.shareplate.data.local

import android.content.Context

/**
 * Remembers who is logged in (and as which role) between launches.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("shareplate_session", Context.MODE_PRIVATE)

    fun saveSession(role: String) {
        prefs.edit()
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun isLoggedIn(): Boolean = !getRole().isNullOrBlank()

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_ROLE = "role"
    }
}
