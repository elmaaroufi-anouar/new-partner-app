package com.done.partner.data.services.print.sunmi

import com.done.partner.domain.services.print.PrinterClient


expect class SunmiPrinterClient: PrinterClient {
    override fun printOrder(ticket: ByteArray, printTwo: Boolean)
}
