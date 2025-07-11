package com.done.partner.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.done.core.domain.models.language.LanguageCodes
import com.done.partner.domain.models.orders.Order
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Picture
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Canvas
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream
import java.util.Locale


@Composable
actual fun ScreenShootTicket(
    order: Order,
    printLang: String?,
    storeName: String?,
    printTwo: Boolean,
    onPictureReady: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    val picture = remember { Picture() }

    LaunchedEffect(picture) {
        onPictureReady(
            createBitmapBytesFromPicture(picture)
        )
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        Canvas(
                            picture.beginRecording(
                                width, height
                            )
                        )
                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawPicture(picture)
                    }
                }
            }
    ) {
        val configuration = remember(printLang) {
            Configuration(context.resources.configuration).apply {
                setLocale(
                    when (printLang) {
                        LanguageCodes.EN -> Locale.ENGLISH
                        LanguageCodes.AR -> Locale("ar")
                        else -> Locale.FRANCE
                    }
                )
            }
        }

        val localizedContext = remember(configuration) {
            context.createConfigurationContext(configuration)
        }

        CompositionLocalProvider(LocalContext provides localizedContext) {
            Ticket(
                order = order,
                storeName = storeName,
                printTwo = printTwo
            )
        }
    }
}

fun createBitmapBytesFromPicture(picture: Picture): ByteArray {
    val bitmap = createBitmap(picture.width, picture.height)
    val canvas = AndroidCanvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawPicture(picture)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

    return outputStream.toByteArray()
}