package org.trichter.app.util

import android.util.Log as AndroidLog

actual class Log actual constructor() {
    actual companion object {
        actual fun i(tag: String, message: String) {
            AndroidLog.i(tag, message)
        }

        actual fun w(tag: String, message: String) {
            AndroidLog.w(tag, message)
        }

        actual fun e(tag: String, message: String) {
            AndroidLog.e(tag, message)
        }

        actual fun e(tag: String?, message: String?, tr: Throwable?) {
            AndroidLog.e(tag, message, tr)
        }

        actual fun d(tag: String, message: String) {
            AndroidLog.d(tag, message)
        }

        actual fun d(tag: String?, message: String?, tr: Throwable?) {
            AndroidLog.d(tag, message, tr)
        }
    }
}