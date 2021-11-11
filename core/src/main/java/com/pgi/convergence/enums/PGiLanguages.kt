package com.pgi.convergence.enums

import java.util.*

enum class PGiLanguages(val locale: String) {
    ENGLISH("en"),
    FRENCH("fr"),
    GERMAN("de"),
    DUTCH("nl"),
    JAPANESE("ja");


    companion object {
        @JvmStatic
        fun toStringArray(): Array<String> {
            val pgiLocales = ArrayList<String>()
            for (loc in PGiLanguages.values()) {
                pgiLocales.add(loc.locale)
            }
            return pgiLocales.toTypedArray();
        }
    }
}