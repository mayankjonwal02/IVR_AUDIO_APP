package com.example.ivr_audio_app_20

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform