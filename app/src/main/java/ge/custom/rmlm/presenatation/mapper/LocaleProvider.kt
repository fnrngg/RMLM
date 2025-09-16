package ge.custom.rmlm.presenatation.mapper

import java.util.Locale

class LocaleProviderImpl : LocaleProvider {
    override fun getCurrentLocale(): Locale = Locale.getDefault()
}

interface LocaleProvider {
    fun getCurrentLocale(): Locale
}