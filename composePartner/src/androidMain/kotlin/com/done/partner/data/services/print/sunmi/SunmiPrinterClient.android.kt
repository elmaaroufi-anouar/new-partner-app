package com.done.partner.data.services.print.sunmi

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import com.sunmi.printerx.PrinterSdk
import com.sunmi.printerx.SdkException
import com.sunmi.printerx.enums.Align
import com.sunmi.printerx.enums.DividingLine
import com.sunmi.printerx.enums.ImageAlgorithm
import com.sunmi.printerx.style.BitmapStyle

actual class SunmiPrinterClient(
    private val context: Context
) {
    private var selectPrinter: PrinterSdk.Printer? = null

    fun printOrder(ticket: ByteArray, printTwo: Boolean) {
        try {
            PrinterSdk.getInstance().getPrinter(context, object : PrinterSdk.PrinterListen {
                override fun onDefPrinter(printer: PrinterSdk.Printer?) {
                    selectPrinter = printer
                    print(ticket, printTwo)
                }

                override fun onPrinters(printer: List<PrinterSdk.Printer?>?) {
                }
            })
        } catch (e: SdkException) {
            e.printStackTrace()
        }
    }

    private fun print(ticket: ByteArray, printTwo: Boolean) {
        val ticketFromBytes = BitmapFactory.decodeByteArray(ticket, 0, ticket.size)

        val printerWidth = 384
        val scalingRatio = printerWidth.toFloat() / ticketFromBytes.width.toFloat()
        val scaledHeight = (ticketFromBytes.height * scalingRatio).toInt()
        val scaledTicket = ticketFromBytes.scale(printerWidth, scaledHeight)
        ticketFromBytes.recycle()

        selectPrinter?.lineApi()?.run {
            printBitmap(
                scaledTicket,
                BitmapStyle.getStyle()
                    .setAlign(Align.CENTER)
                    .setAlgorithm(ImageAlgorithm.DITHERING)
                    .setValue(127)
                    .setWidth(printerWidth)
            )

            if (printTwo) {
                printBitmap(
                    scaledTicket,
                    BitmapStyle.getStyle()
                        .setAlign(Align.CENTER)
                        .setAlgorithm(ImageAlgorithm.DITHERING)
                        .setValue(127)
                        .setWidth(printerWidth)
                )
            }

            printDividingLine(DividingLine.EMPTY, 40)
            scaledTicket.recycle()
        }
    }
}