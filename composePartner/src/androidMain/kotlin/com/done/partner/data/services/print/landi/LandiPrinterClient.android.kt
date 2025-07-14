package com.done.partner.data.services.print.landi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.graphics.scale
import com.done.partner.domain.services.print.PrinterClient
import com.sdksuite.omnidriver.OmniConnection
import com.sdksuite.omnidriver.OmniDriver
import com.sdksuite.omnidriver.aidl.printer.Align
import com.sdksuite.omnidriver.api.OnPrintListener
import com.sdksuite.omnidriver.api.Printer
import java.io.ByteArrayOutputStream

actual class LandiPrinterClient(
    private val context: Context
): PrinterClient {
    private var printer: Printer? = null

    actual override fun printOrder(ticket: ByteArray, printTwo: Boolean) {
        processTicketForPrinting(ticket)?.let { processedTicket ->
            try {
                OmniDriver.me(context).init(object : OmniConnection {
                    override fun onConnected() {
                        printer = OmniDriver.me(context).getPrinter(Bundle())
                        printer?.openDevice()
                        print(processedTicket, printTwo)
                    }

                    override fun onDisconnected(error: Int) {}
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun print(ticket: ByteArray, printTwo: Boolean) {
        try {
            printer?.addImage(ticket, Align.CENTER, 0)
            if (printTwo) {
                printer?.addImage(ticket, Align.CENTER, 0)
            }

            printer?.feedLine(1)
            printer?.startPrint(object : OnPrintListener {
                override fun onFail(error: Int) {
                    println("OnPrintListener: onFail: $error")
                }

                override fun onSuccess() {
                    println("OnPrintListener: onSuccess")
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        printer?.closeDevice()
    }

    private fun processTicketForPrinting(ticket: ByteArray): ByteArray? {
        return try {
            val bitmap = BitmapFactory.decodeByteArray(ticket, 0, ticket.size) ?: return null

            val processedBitmap = if (bitmap.width > 384) {
                scaleBitmap(bitmap) ?: bitmap
            } else {
                bitmap
            }

            val result = bmpToByteArray(processedBitmap)

            if (processedBitmap == bitmap && !bitmap.isRecycled) {
                bitmap.recycle()
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            ticket
        }
    }

    private fun scaleBitmap(bm: Bitmap, offset: Int = 0, maxWidth: Int = 384): Bitmap? {
        val width = bm.width
        val height = bm.height
        val newWidth = maxWidth - offset
        if (newWidth <= 0) return null

        val scaleFactor = newWidth.toFloat() / width
        val newHeight = (height * scaleFactor).toInt()

        return bm.scale(newWidth, newHeight)
    }

    private fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean = true): ByteArray {
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output)
        if (needRecycle) {
            bmp.recycle()
        }
        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}