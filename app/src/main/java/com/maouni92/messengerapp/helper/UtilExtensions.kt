package com.maouni92.messengerapp.helper

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.maouni92.messengerapp.Preferences
import com.maouni92.messengerapp.R


fun Activity.initStatusBar(){
            val  pref = Preferences(this)
            if (pref.currentThemeMode() == ThemeMode.LIGHT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                   this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    this.window.statusBarColor = this.resources.getColor(R.color.white_200)
                }
            }
        }

fun String.getDateFormat(timeInMilliseconds:Long) : String{
    var dateFormat = ""
    val currentTime = System.currentTimeMillis()
    val timeDifference = currentTime - timeInMilliseconds
    val dateArray = this.split(",")

    when {
        timeDifference > Constants.MILLISECONDS_IN_YEAR -> dateFormat = "${dateArray[1]} ${dateArray[2]}"
        timeDifference > Constants.MILLISECONDS_IN_WEEK -> dateFormat =  dateArray[1]
        timeDifference > Constants.MILLISECONDS_IN_DAY -> dateFormat = dateArray[0]
        timeDifference > 0 -> dateFormat = dateArray[3]
    }
    return dateFormat
}

fun View.hideKeyboard(context: Context) {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

