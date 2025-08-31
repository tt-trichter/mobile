package org.trichter.app.features.ble.presentation.views

import android.graphics.BitmapFactory
import android.media.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeImage(bytes: ByteArray): ImageBitmap? =
    runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap() }.getOrNull()
