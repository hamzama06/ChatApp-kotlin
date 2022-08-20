package com.maouni92.messengerapp.helper

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.maouni92.messengerapp.Preferences

class ThemeMode {

    companion object {

        const val LIGHT = 0
        const val DARK = 1

        const val THEME_STATE = "themeState"

        fun initThemePref(context: Context){
            val  pref = Preferences(context)
            when (pref.currentThemeMode()) {
                LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}