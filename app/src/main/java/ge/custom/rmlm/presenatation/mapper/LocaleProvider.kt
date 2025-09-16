package ge.custom.rmlm.presenatation.mapper

import java.util.Locale
import java.util.TimeZone

class LocaleProviderImpl : LocaleProvider {
    override fun getCurrentLocale(): Locale = Locale.getDefault()
    override fun getCurrentTimeZone(): TimeZone = TimeZone.getDefault()
}

interface LocaleProvider {
    fun getCurrentLocale(): Locale
    fun getCurrentTimeZone(): TimeZone
}