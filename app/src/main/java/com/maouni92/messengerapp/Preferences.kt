package com.maouni92.messengerapp

import android.content.Context
import android.content.SharedPreferences
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.ThemeMode

class Preferences(context: Context) {

   private var sharedPref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)


    fun currentThemeMode():Int{
        return sharedPref.getInt(ThemeMode.THEME_STATE, 0)
    }

    fun setThemeMode(theme:Int){
       sharedPref.edit().apply {
            putInt(ThemeMode.THEME_STATE, theme)
            apply()
        }
    }

    fun setToken(token:String?){
        sharedPref.edit().apply {
            putString("token", token)
            apply()
        }
    }

    fun getToken():String?{
        return sharedPref.getString("token","")
    }

    fun enableNotifications(isEnabled:Boolean){
        sharedPref.edit().apply {
            putBoolean(Constants.NOTIFICATIONS_PREFERENCES_KEY, isEnabled)
            apply()
        }
    }

    fun isNotificationsEnabled() : Boolean{
        return sharedPref.getBoolean(Constants.NOTIFICATIONS_PREFERENCES_KEY, true)
    }
}