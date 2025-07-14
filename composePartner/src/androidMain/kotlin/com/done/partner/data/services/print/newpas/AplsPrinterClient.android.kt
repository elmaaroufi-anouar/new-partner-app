package com.done.partner.data.services.print.newpas

import android.bluetooth.BluetoothAdapter
import android.graphics.BitmapFactory
import com.done.partner.data.services.print.newpas.util.BitMapUtil
import com.done.partner.data.services.print.newpas.util.BluetoothManager
import com.done.partner.data.services.print.newpas.util.ESCUtil
import com.done.partner.data.services.print.newpas.util.ThreadPoolManager
import com.done.partner.domain.services.print.PrinterClient

actual class AplsPrinterClient: PrinterClient {
    actual override fun printOrder(
        ticket: ByteArray, printTwo: Boolean
    ) {
        BluetoothManager.getInstance().bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (BluetoothManager.getInstance().bluetoothAdapter != null) {
            if (BluetoothManager.getInstance().bluetoothAdapter.isEnabled) {
                print(ticket, printTwo)
            } else {
                if (BluetoothManager.getInstance().bluetoothAdapter != null) {
                    BluetoothManager.getInstance().bluetoothAdapter.enable()
                }
                try {
                    Thread.sleep(1000)
                    print(ticket, printTwo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun print(
        ticket: ByteArray, printTwo: Boolean
    ) {
        ThreadPoolManager.getInstance().executeTask {
            try {
                printImage(ticket)
                if (printTwo) {
                    printImage(ticket)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun printImage(ticket: ByteArray) {
        val originalBitmap = BitmapFactory.decodeByteArray(ticket, 0, ticket.size) ?: return

        val align = ESCUtil.alignMode(0.toByte())
        val leftMargin = ESCUtil.printLeftMargin(0)
        val performPrint = ESCUtil.performPrintAndFeedPaper(10.toByte())

        val bitmap = BitMapUtil.getBitmapPrintData(originalBitmap, 384, 1)
        if (bitmap != null) {
            val cmdBytes = arrayOf<ByteArray>(leftMargin, align, bitmap, performPrint)
            val data = ESCUtil.byteMerger(cmdBytes)
            BluetoothManager.getInstance().writeData(data)
            if (!originalBitmap.isRecycled) {
                originalBitmap.recycle()
            }
        }
    }
}