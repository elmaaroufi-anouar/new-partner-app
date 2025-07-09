package com.done.partner.data.services.print

import android.os.Build
import com.done.core.domain.services.secure_storage.SecureStorageService
import com.done.core.domain.models.language.LanguageCodes
import com.done.core.domain.services.device_info.DeviceInfoService
import com.done.partner.data.services.print.landi.LandiPrinterClient
import com.done.partner.domain.services.print.PrinterService
import com.done.partner.data.services.print.newpas.AplsPrinterClient
import com.done.partner.data.services.print.sunmi.SunmiPrinterClient
import com.done.partner.domain.util.Printer
import com.done.partner.domain.util.PrinterType

class PrinterServiceImpl(
    private val sunmiPrinterClient: SunmiPrinterClient,
    private val alpsPrinterService: AplsPrinterClient,
    private val landiPrinterClient: LandiPrinterClient,
    private val secureStorageService: SecureStorageService,
    private val deviceInfoService: DeviceInfoService
) : PrinterService {

    private val printLangKey = "print_lang_key"

    init {
        val brand = deviceInfoService.getBrandName().lowercase()
        val model = deviceInfoService.getModelName().lowercase()
        if (brand.contains("sunmi") || model.contains("v2")) {
            Printer.CURRENT_PRINTER_TYPE = PrinterType.SUNMI
        } else if (brand.contains("landi") || model.contains("m20")) {
            Printer.CURRENT_PRINTER_TYPE = PrinterType.LANDI
        } else if (brand.contains("alps") || model.contains("Q6Pro")) {
            Printer.CURRENT_PRINTER_TYPE = PrinterType.ALPS
        }
    }

    override suspend fun printOrder(
        ticket: ByteArray, printTwo: Boolean
    ) {
        when (Printer.CURRENT_PRINTER_TYPE) {
            PrinterType.SUNMI -> sunmiPrinterClient.printOrder(ticket, printTwo)
            PrinterType.ALPS -> alpsPrinterService.printOrder(ticket, printTwo)
            PrinterType.LANDI -> landiPrinterClient.printOrder(ticket, printTwo)
            null -> Unit
        }
    }

    override suspend fun setPrinterType(printerType: PrinterType) {
        Printer.CURRENT_PRINTER_TYPE = printerType
    }

    override suspend fun getPrintLang(): String {
        return secureStorageService.getString(printLangKey) ?: LanguageCodes.FR
    }

    override suspend fun setPrintLang(langCode: String) {
        secureStorageService.putString(printLangKey, langCode)
    }
}