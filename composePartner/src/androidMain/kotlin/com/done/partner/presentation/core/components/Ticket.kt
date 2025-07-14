package com.done.partner.presentation.core.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import com.done.core.domain.models.language.LanguageCodes
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.services.print.formatDate
import com.done.partner.domain.services.print.getTimeAfterMinutes
import com.done.partner.domain.util.Printer
import java.io.ByteArrayOutputStream
import java.util.*

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun ScreenShootTicket(
    order: Order,
    printLang: String? = LanguageCodes.FR,
    storeName: String?,
    printTwo: Boolean,
    picture: Picture
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        androidx.compose.ui.graphics.Canvas(
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
                localizedContext = localizedContext,
                printTwo = printTwo
            )
        }
    }
}

@Composable
fun Ticket(
    order: Order,
    localizedContext: Context,
    storeName: String?,
    printTwo: Boolean,
) {

    // for ticket, we won't use font style because it is static and should look the same in all printers

    fun fontSize(extraSize: Int = 5) = (Printer.PRINTER_TEXT_SIZE + extraSize).sp
    val fontWeight = FontWeight.SemiBold
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_black),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!printTwo) {
            Text(
                localizedContext.getString(Res.string.duplicated),
                fontSize = fontSize(),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            localizedContext.getString(Res.string.pickup_code),
            fontSize = fontSize(),
            fontWeight = fontWeight
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalStars()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            VerticalStars()
            Text(
                order.friendlyNumber,
                lineHeight = 1.sp,
                fontSize = fontSize(38),
                fontWeight = FontWeight.SemiBold
            )
            VerticalStars(modifier = Modifier.padding(end = 7.dp))
        }
        HorizontalStars()
        Spacer(modifier = Modifier.height(22.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                order.id,
                fontSize = fontSize(0),
                textAlign = TextAlign.Center,
                fontWeight = fontWeight,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "${localizedContext.getString(Res.string.estimated_pickup_time)} ${order.deliveryEstimationInMin.getTimeAfterMinutes()}",
                textAlign = TextAlign.Center,
                fontSize = fontSize(4),
                fontWeight = fontWeight,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (storeName != null && storeName.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Store,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = storeName,
                        fontSize = fontSize(5),
                        fontStyle = FontStyle.Italic,
                        fontWeight = fontWeight
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val firstName = order.customer?.firstName
            if (firstName != null && firstName.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = firstName,
                        fontSize = fontSize(5),
                        fontStyle = FontStyle.Italic,
                        fontWeight = fontWeight
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -",
                fontSize = fontSize(1),
                maxLines = 1,
                fontWeight = fontWeight
            )

            order.orderItems.forEach { item ->
                Row {
                    Text(
                        "${item.quantity}x ${item.name}",
                        fontSize = fontSize(4),
                        fontWeight = fontWeight,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f)
                    )
                    Text(
                        "${((item.product?.price?.amount ?: 0.0) / 100.0) * item.quantity} ${item.product?.price?.currency ?: "MAD"}",
                        fontSize = fontSize(4),
                        fontWeight = fontWeight
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                item.orderItemOptions.forEach { opt ->
                    val optionQuantity = opt.quantity * item.quantity
                    Row(
                        modifier = Modifier.padding(start = 24.dp)
                    ) {
                        Text(
                            "${optionQuantity}x ",
                            fontSize = fontSize(1),
                            fontWeight = fontWeight,
                            modifier = Modifier
                        )
                        Text(
                            opt.name,
                            fontSize = fontSize(1),
                            fontWeight = fontWeight,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "${((opt.price?.amount ?: 0.0) / 100.0) * item.quantity} ${item.product?.price?.currency ?: "MAD"}",
                            fontSize = fontSize(1),
                            fontWeight = fontWeight
                        )
                    }
                }
                Text(
                    "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -",
                    fontSize = fontSize(1),
                    maxLines = 1,
                    fontWeight = fontWeight
                )
            }

            if (order.note.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Message,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = localizedContext.getString(Res.string.customer_request),
                        fontSize = fontSize(4),
                        fontStyle = FontStyle.Italic,
                        fontWeight = fontWeight
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "\"${order.note}\"",
                    fontSize = fontSize(4),
                    fontWeight = fontWeight
                )
            }

            Spacer(Modifier.height(34.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    localizedContext.getString(Res.string.total),
                    fontSize = fontSize(13),
                    fontWeight = fontWeight
                )
                Text(
                    order.productAmount?.display ?: "",
                    fontSize = fontSize(13),
                    fontWeight = fontWeight
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                System.currentTimeMillis().formatDate(),
                fontSize = fontSize(3),
                fontWeight = fontWeight
            )
            Spacer(modifier = Modifier.height(38.dp))

            Text(
                localizedContext.getString(Res.string.made_with_in_morocco),
                fontSize = fontSize(3),
                fontWeight = fontWeight,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(100.dp))
            Box(
                modifier = Modifier
                    .size(1.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun HorizontalStars() {
    Text(
        "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *",
        fontSize = 21.sp,
        maxLines = 1,
        fontWeight = FontWeight.SemiBold
    )
}


@Composable
fun VerticalStars(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        repeat(3) {
            Text(
                "*",
                fontSize = 21.sp,

                maxLines = 1,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


//@Preview
//@Composable
//private fun ReceiptLayoutPreview() {
//    DoneTheme {
//        Column(
//            modifier = Modifier
//                .background(Color.White)
//                .verticalScroll(rememberScrollState())
//        ) {
//            Ticket(
//                order = previewOrders[0],
//                storeName = "Done Store",
//                localizedContext = LocalContext.current,
//                printTwo = false,
//            )
//        }
//    }
//}