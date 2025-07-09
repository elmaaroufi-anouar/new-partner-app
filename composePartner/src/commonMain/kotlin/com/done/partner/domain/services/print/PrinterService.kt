package com.done.partner.domain.services.print

import com.done.partner.domain.util.PrinterType

interface PrinterService {
    suspend fun printOrder(
        ticket: ByteArray, printTwo: Boolean
    )

    suspend fun setPrinterType(printerType: PrinterType)

    suspend fun getPrintLang(): String

    suspend fun setPrintLang(langCode: String)
}