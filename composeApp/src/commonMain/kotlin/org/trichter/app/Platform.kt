package org.trichter.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform