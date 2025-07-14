package com.done.partner.domain.services.print

interface PrinterClient {
    fun printOrder(ticket: ByteArray, printTwo: Boolean)
}
