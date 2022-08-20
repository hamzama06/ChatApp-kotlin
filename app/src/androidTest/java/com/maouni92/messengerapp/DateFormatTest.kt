package com.maouni92.messengerapp


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.maouni92.messengerapp.helper.getDateFormat
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DateFormatTest {

    @Test
    fun dateFormat_hoursFormat(){

      val date = "Wed,Aug 10,2022,16:40"


      val expectedDateFormat = "Wed"
      val currentDateFormat = date.getDateFormat(1660048780775)

       assertThat(currentDateFormat).isEqualTo(expectedDateFormat)
    }
}