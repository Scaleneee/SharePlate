package com.example.shareplate.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {

    val client = createSupabaseClient(
        supabaseUrl = "https://gpwtlihsibqrunnqjxzh.supabase.co",
        supabaseKey = "sb_publishable_RsyEyJmY55X_gRUdcjDg3g_5D0mgnZj"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

}