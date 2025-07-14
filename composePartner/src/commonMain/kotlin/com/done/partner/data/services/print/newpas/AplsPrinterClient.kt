package com.done.partner.data.services.print.newpas

import com.done.partner.domain.services.print.PrinterClient

expect class AplsPrinterClient: PrinterClient {
    override fun printOrder(ticket: ByteArray, printTwo: Boolean)
}
