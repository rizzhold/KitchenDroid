package com.kitchendroid.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleManager {

    private const val PREF_NAME = "kitchendroid_locale"
    private const val KEY_LANG = "selected_lang"

    /** "en" or "id" */
    fun getLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG, "en") ?: "en"
    }

    fun saveLanguage(context: Context, languageCode: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, languageCode).apply()
    }

    fun applyLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config: Configuration = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            val list = LocaleList(locale)
            LocaleList.setDefault(list)
            config.setLocales(list)
            res.updateConfiguration(config, res.displayMetrics)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
    }
}
