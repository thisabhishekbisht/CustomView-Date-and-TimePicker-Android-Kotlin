package com.example.customdateandtimeviewwithscroll

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.TimePicker.OnTimeChangedListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var timePicker: TimePicker
    lateinit var numberPicker: NumberPicker
    private var mHour = 0
    private var mMinute: Int = 0
    var selectedDate: Date? = null
    var currentDates: Date? = null
    var count: Int = 10
    var isFirst: Boolean = false
    var futureDates = arrayOfNulls<String>(10)
    var futureDates1 = arrayOfNulls<String>(11)
    var BookingDate: String? = null
    var BookingTime: String? = null
    lateinit var btnConfirm: Button
    lateinit var tv_dateChoosen: TextView
    lateinit var tv_timeChoosen: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init();
    }

    fun init() {
        // initialize views and click listners
        numberPicker = findViewById(R.id.numberPicker);
        timePicker = findViewById(R.id.timePicker);
        btnConfirm = findViewById(R.id.btn_confirm);
        tv_dateChoosen = findViewById(R.id.tv_date)
        tv_timeChoosen = findViewById(R.id.tv_time)

        btnConfirm.setOnClickListener(this)
        // get Calender instance for getting current date and time
        getCurrentTimeAndDate()
        // change listner for number picker and timepicker

        // getFuture date from current date like 1 - 10 days from now you can use your own custom value for arraySize by changing 10 to 20 or something
        getFutureDates()
// set Numberpicker Data with this function
        setNumberPickerData()

        // numberpicker change listner
        numberPicker.setOnValueChangedListener { picker: NumberPicker?, oldVal: Int, newVal: Int ->
            val calendarw = Calendar.getInstance()
            val myYears = calendarw[Calendar.YEAR].toString()
            val selectedBookingDate =
                futureDates[newVal].toString() + " " + myYears
            @SuppressLint("SimpleDateFormat") val sdfyy = SimpleDateFormat("EEE d-MMM yyyy")
            try {
                selectedDate = sdfyy.parse(selectedBookingDate)
                if (selectedDate == currentDates) {
                    timePicker.setCurrentHour(mHour)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        // timePicker
        // change listner
        timePicker.setOnTimeChangedListener(OnTimeChangedListener { view, hourOfDay, minute ->
            if (selectedDate!!.after(currentDates)) {

            } else {
                val cal = Calendar.getInstance()
                cal.add(Calendar.MINUTE, 30)
                mHour = cal[Calendar.HOUR_OF_DAY]
                mMinute = cal[Calendar.MINUTE]
                val currentLocalTime = cal.time
                if (hourOfDay < mHour) { //
                    timePicker.setCurrentHour(mHour)
                    timePicker.setCurrentMinute(mMinute)
                } else if (minute < mMinute) {
                    timePicker.setCurrentMinute(mMinute)
                }
            }
        })


    }

    private fun setNumberPickerData() {
        numberPicker.minValue = 0
        numberPicker.displayedValues = futureDates
        numberPicker.maxValue = futureDates.size - 1
    }

    private fun getCurrentTimeAndDate() {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val month_date =
            SimpleDateFormat("MMM ")
        @SuppressLint("SimpleDateFormat") val dayofWeek =
            SimpleDateFormat("EEE")

        val d = Date()
        val day = dayofWeek.format(d)
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val year = calendar[Calendar.YEAR]
        val month = month_date.format(calendar.time)

        val myBookingdate = "$day $dayOfMonth-$month $year"
        @SuppressLint("SimpleDateFormat") val sdf: DateFormat =
            SimpleDateFormat("EEE d-MMM yyyy")
        try {
            currentDates = sdf.parse(myBookingdate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 30)

    }

    private fun getFutureDates() {

        var sdf = SimpleDateFormat("EEE d-MMM")
        for (i in 0 until count) {
            val calendar: Calendar = GregorianCalendar()
            calendar.add(Calendar.DATE, i)
            val day: String = sdf.format(calendar.time)
            val mCurrentHour = calendar[Calendar.HOUR_OF_DAY]
            val mCurntMinute = calendar[Calendar.MINUTE]
            if (mCurrentHour == 23 && mCurntMinute >= 30) {
                count = 11
                isFirst = true
                futureDates1[i] = day
                //   futureBokingDates[i] = days;
            } else {
                futureDates[i] = day
                futureDates1[i] = day
            }
        }
        //
        if (isFirst) {
            val list: List<String> = ArrayList(Arrays.asList<String>(*futureDates1))

            list.drop(0)
            futureDates = arrayOf(list.toTypedArray()[0])
        }
        //
        //
        BookingDate = futureDates[numberPicker.value]
        val calendarw = Calendar.getInstance()
        val myYears = calendarw[Calendar.YEAR].toString()
        val selectedBookingDate: String = BookingDate + " " + myYears
        @SuppressLint("SimpleDateFormat") val sdfyy =
            SimpleDateFormat("EEE d-MMM yyyy")

        try {
            selectedDate = sdfyy.parse(selectedBookingDate)
            Log.e("selectedDate", selectedDate.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    override fun onClick(p0: View?) {
        when (p0) {
            btnConfirm ->        // set other button clickable false
// change
// Get date and Time from  Time and Date Picker
                getDateAndTime()
        }
    }

    private fun getDateAndTime() {
        BookingDate = futureDates[numberPicker.value]
        BookingTime = updateTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute())

        // setValues to textField
        tv_dateChoosen.setText("Date : " + BookingDate)
        tv_timeChoosen.setText("Time : " + BookingTime)

    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private fun updateTime(hours: Int, mins: Int): String? {
        var hours = hours
        var timeSet = ""
        if (hours > 12) {
            hours -= 12
            timeSet = "PM"
        } else if (hours == 0) {
            hours += 12
            timeSet = "AM"
        } else if (hours == 12) timeSet = "PM" else timeSet = "AM"
        var minutes = ""
        minutes = if (mins < 10) "0$mins" else mins.toString()
        // Append in a StringBuilder
        return StringBuilder().append(hours).append(':')
            .append(minutes).append(" ").append(timeSet).toString()
    }
}


