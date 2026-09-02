package com.example.shareplate.data.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    private const val SUPABASE_URL = "https://gpwtlihsibqrunnqjxzh.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_RsyEyJmY55X_gRUdcjDg3g_5D0mgnZj"

    val client = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
        install(Postgrest)
        install(Auth) {
            scheme = "shareplate"
            host = "reset"
        }
    }
}
