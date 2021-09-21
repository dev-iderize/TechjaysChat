package com.techjays.chatlibrary.Util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import com.techjays.chatlibrary.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val apiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val displayDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    val currentDate: String
        get() {
            val cal = Calendar.getInstance()
            return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(cal.time)
        }

    val currentTime: String
        get() {
            val cal = Calendar.getInstance()
            cal.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
            return SimpleDateFormat("h:mm a", Locale.ENGLISH).format(cal.time)
        }

    val previous15DaysDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -15)
            return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(cal.time)
        }

    val after15DaysDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, +15)
            return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(cal.time)
        }

    val previousMonth: String
        get() {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(cal.time)
        }

    /**
     * @param d Date Object
     * @return String in format dd-MM-yyyy
     */
    fun formatDisplayDate(d: Date): String? {
        var ds: String? = null
        try {
            ds = displayDateFormat.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ds
    }

    /**
     * @param ds Date String in format dd-MM-yyyy
     * @return instance of [Date]
     */
    fun parseDisplayDate(ds: String): Date? {
        var date: Date? = null
        try {
            date = displayDateFormat.parse(ds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return date
    }

    /**
     * @param d Date Object
     * @return string in format yyyy-MM-dd
     */
    fun formatApiDate(d: Date): String? {
        var ds: String? = null
        try {
            ds = apiDateFormat.format(d)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ds
    }

    fun getApiDate(date: String): String {
        val d = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(date)// 06 Dec 2017
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cal.time) // 2017-12-06
    }


    fun getDateNamefromDate(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date) // 2017-12-06
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(cal.time) // 06 Dec 2017
    }

    fun getDateNameFromDateTime(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date) // 2017-12-06
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(cal.time) // 06 Dec 2017
    }

    fun getMonthformat(date: String): String {
        val d = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        ).parse(date) // 2017-12-06 10:30:00
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat(
            "E, MMM dd, h:mm a",
            Locale.ENGLISH
        ).format(cal.time) // Dec,06 10:30 AM
    }

    fun getupdateformat(date: String): String {
        val d = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        ).parse(date) // 2017-12-06 10:30:00
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat(
            "MMM dd, h:mm a",
            Locale.ENGLISH
        ).format(cal.time) // Dec,06 10:30 AM
    }

    fun getleaveformat(date: String): String {
        val d = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        ).parse(date) // 2017-12-06 10:30:00
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("MMM dd", Locale.ENGLISH).format(cal.time) // Dec,06 10:30 AM
    }

    fun getleaveformatwithYear(date: String): String {
        val d = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        ).parse(date) // 2017-12-06 10:30:00
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(cal.time) // Dec,06 10:30 AM
    }

    fun getNotificationformat(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(date) // 2017-12-06 10:30
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("MMM dd", Locale.ENGLISH).format(cal.time) // Dec,06 10:30 AM
    }

    fun getNotificationformatwithYear(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(date) // 2017-12-06 10:30
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(cal.time) // Dec,06 10:30 AM
    }

    fun getDashBoardformat(date: String): String {
        val d = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(date) // 2017-12-06 10:30
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("MMM dd", Locale.ENGLISH).format(cal.time) // Dec,06 10:30 AM
    }

    fun getDashBoardformatwithYear(date: String): String {
        val d = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(date) // 2017-12-06 10:30
        val cal = Calendar.getInstance()

        cal.time = d
        return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(cal.time) // Dec,06 10:30 AM
    }

    fun getTime24to12(time: String): String? {
        try {
            val t = SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(time) //14:00
            return SimpleDateFormat("h:mm a", Locale.ENGLISH).format(t) //10:25 AM
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun getTime12to24(time: String): String? {
        try {
            val t = SimpleDateFormat("h:mm a", Locale.ENGLISH).parse(time)//10:25 AM
            return SimpleDateFormat("HH:mm", Locale.ENGLISH).format(t) //14:00
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun getMilliSeconds(date: String): Long {
        if (date.isEmpty())
            return 0
        val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(date)
        return d!!.time
    }

    /**
     * @param utcDate UTC Time
     * 1. Convert UTC time to exact UTC format
     * 2. Then convert that UTC time to device timezone
     * @return Device time
     */
    fun convertUTCToDeviceTime(utcDate: String): Long {
        try {
            if (utcDate.isEmpty())
                return 0
            val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.timeZone = TimeZone.getDefault()
            return getMilliSeconds(formatter.format(utcFormatter.parse(utcDate)!!))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    private val myFormatDate = SimpleDateFormat("dd MMM yyyy", Locale.US)
    fun findDifferenceofTwoDays(from_date: String, to_date: String): Long? {
        try {
            val fdo = myFormatDate.parse(from_date)
            val tdo = myFormatDate.parse(to_date)
            val d = tdo.time - fdo.time
            if (tdo.after(fdo) || (tdo == fdo)) {
                return (d / (24 * 60 * 60 * 1000)) + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun findDifferenceofTwoHours(from_hour: String, to_hour: String): String? {
        try {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val startDate = simpleDateFormat.parse(DateUtil.getTime12to24(from_hour))
            val endDate = simpleDateFormat.parse(DateUtil.getTime12to24(to_hour))

            var difference = endDate.time - startDate.time
            if (difference < 0) {
                val dateMax = simpleDateFormat.parse("24:00")
                val dateMin = simpleDateFormat.parse("00:00")
                difference = dateMax.time - startDate.time + (endDate.time - dateMin.time)
            }
            val days = (difference / (1000 * 60 * 60 * 24)).toInt()
            val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
            val min =
                (difference - (1000 * 60 * 60 * 24 * days).toLong() - (1000 * 60 * 60 * hours).toLong()).toInt() / (1000 * 60)

            return String.format("%s:%s", hours, min)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * @param aSelectedDate     String
     * @param aCurrentFormat    String
     * @param aConversionFormat String
     * @return String
     */
    fun convertDateFormat(
        aSelectedDate: String,
        aCurrentFormat: String,
        aConversionFormat: String
    ): String {
        var aDate = ""
        try {
            if (aSelectedDate.isEmpty())
                return ""
            val d = SimpleDateFormat(aCurrentFormat, Locale.ENGLISH).parse(aSelectedDate)
            aDate = SimpleDateFormat(aConversionFormat, Locale.ENGLISH).format(d.time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aDate
    }


    /**
     * @param context    Context
     * @param datePicker DatePickerDialog - DatePickerDialog.OnDateSetListener from_datePicker = new DatePickerDialog.OnDateSetListener()
     * @param hidePast   true - Hide Past Date, Show Future Date -- false - Show Past Date, Hide Future Date
     */

    /**
     * @param context         Context
     * @param SelectedDate    String (dd-MM-yyyy)
     * @param from_datePicker DatePickerDialog - DatePickerDialog.OnDateSetListener from_datePicker = new DatePickerDialog.OnDateSetListener()
     * @param hidePast   true - Hide Past Date, Show Future Date -- false - Show Past Date, Hide Future Date
     */

    /**
     * @param context       Context
     * @param format        SimpleDateFormat
     * @param fromDate      String (dd-MM-yyyy)
     * @param SelectedDate  String (dd-MM-yyyy)
     * @param to_datePicker DatePickerDialog - DatePickerDialog.OnDateSetListener from_datePicker = new DatePickerDialog.OnDateSetListener()
     */



    /**
     * @param context  Context
     * @param listener TimePickerDialog.OnTimeSetListener
     */

    fun showTimeDialog(context: Context, listener: TimePickerDialog.OnTimeSetListener) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val dialog =
            TimePickerDialog(context, listener, hourOfDay, minute, false) // True - 24hours format
        dialog.setCancelable(false)
        dialog.setTitle("")
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "SELECT", dialog)
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "CANCEL", dialog)
        dialog.show()
    }


    /**
     * @param context  Context
     * @param format 10:00 AM
     * @param interval 15,30, 45 ....
     */

    fun getTimeSlot(context: Context, format: String, interval: Int): java.util.ArrayList<String> {
        val slots = arrayListOf<String>()
        try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val startDate = cal.get(Calendar.DATE)
            while (cal.get(Calendar.DATE) == startDate) {
                slots.add(df.format(cal.time))
                cal.add(Calendar.MINUTE, interval)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return slots
    }

    /**
     * Get Age from DOB
     */

    fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        Log.i("Age -->> ", age.toString())
        return age
    }
}