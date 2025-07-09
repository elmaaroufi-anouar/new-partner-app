package com.done.partner.domain.util

enum class PrinterType {
    SUNMI, ALPS, LANDI
}


object Printer {
    var CURRENT_PRINTER_TYPE: PrinterType? = null
    val PRINTER_TEXT_SIZE by lazy {
        when (CURRENT_PRINTER_TYPE) {
            PrinterType.SUNMI -> 20
            PrinterType.ALPS -> 20
            PrinterType.LANDI -> 17
            null -> 20
        }
    }
}