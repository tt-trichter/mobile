package org.trichter.app.util

expect class Log() {
    companion object {
        fun d(tag: String, message: String)
        fun d(tag: String?, message: String?, tr: Throwable?)
        fun i(tag: String, message: String)
        fun w(tag: String, message: String)
        fun e(tag: String, message: String)
        fun e(tag: String?, message: String?, tr: Throwable?)
    }
}