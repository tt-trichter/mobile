package org.trichter.app.util

actual class Log actual constructor() {
    actual companion object {
        actual fun i(tag: String, message: String) {
            throw NotImplementedError("Logging not implemented on iOS")
        }

        actual fun w(tag: String, message: String) {
            throw NotImplementedError("Logging not implemented on iOS")
        }

        actual fun e(tag: String, message: String) {
            throw NotImplementedError("Logging not implemented on iOS")
        }

        actual fun e(tag: String?, message: String?, tr: Throwable?) {
            throw NotImplementedError("Logging not implemented on iOS")
        }

        actual fun d(tag: String, message: String) {
            throw NotImplementedError("Logging not implemented on iOS")
        }

        actual fun d(tag: String?, message: String?, tr: Throwable?) {
            throw NotImplementedError("Logging not implemented on iOS")
        }
    }
}