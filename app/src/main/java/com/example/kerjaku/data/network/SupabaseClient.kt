package com.example.kerjaku.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseApi {
    val client = createSupabaseClient(
        supabaseUrl = "https://wjxfloqdptdcljgijkvb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndqeGZsb3FkcHRkY2xqZ2lqa3ZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk4MDY1NzYsImV4cCI6MjA5NTM4MjU3Nn0.sAFnoBtJeA5wNqIqwd-r9CZ4XXIj9wKciuNyW4XYfFU"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}