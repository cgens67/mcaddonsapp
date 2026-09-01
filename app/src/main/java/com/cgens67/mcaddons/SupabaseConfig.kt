package com.cgens67.mcaddons

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AddonItem(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("size_mb") val sizeMb: Double? = null
)

object SupabaseConfig {
    const val PROJECT_URL = "https://lxsgyczwjzvmbmlcueay.supabase.co"
    const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx4c2d5Y3p3anp2bWJtbGN1ZWF5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODgyMDMwMTYsImV4cCI6MjEwMzc3OTAxNn0.a4UpjE7V-uIGt6oFFBPPMJOnKfUSV3lpUrkERV4zteM"

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            header("apikey", ANON_KEY)
            header("Authorization", "Bearer $ANON_KEY")
        }
    }
}
