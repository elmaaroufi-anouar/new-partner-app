package com.done.partner.data.services.print.landi

import com.done.partner.domain.services.print.PrinterClient

expect class LandiPrinterClient: PrinterClient {
    override fun printOrder(ticket: ByteArray, printTwo: Boolean)
}
