package com.example.justblog.utils

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import java.util.Date


object GetTimeAgo : Application() {
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = (24 * HOUR_MILLIS).toDouble()
    private const val WEEK_MILLIS = 7 * DAY_MILLIS
    private const val MONTH_MILLIS = DAY_MILLIS * 30
    private const val YEAR_MILLIS = WEEK_MILLIS * 52


    fun getTimeAgo(time: Long, ctx: Context?): String? {
        var time = time
        val sagat = DateFormat.format("hh:mm", Date(time)).toString()
        val gun = DateFormat.format("dd", Date(time)).toString()
        val ay = DateFormat.format("MM", Date(time)).toString()

        val yyl = DateFormat.format("yyyy", Date(time)).toString()

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        // TODO: localize
        val diff = now - time
        if (diff < MINUTE_MILLIS) {
            return "Today $sagat"
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Today $sagat"
        } else if (diff < 50 * MINUTE_MILLIS) {
            val roundup = (diff / MINUTE_MILLIS).toDouble()
            val b = roundup.toInt()
            return "Today $sagat"
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Today $sagat"
        } else if (diff < 24 * HOUR_MILLIS) {
            val roundup = (diff / HOUR_MILLIS).toDouble()
            val b = roundup.toInt()
            return "Today $sagat"
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday $sagat"
        } else if (diff < 7 * DAY_MILLIS) {
            val roundup = diff / DAY_MILLIS
            val b = roundup.toInt()
            return gun + " " + wagt(ay) + " " + sagat
        } else if (diff < 2 * WEEK_MILLIS) {
            return gun + " " + wagt(ay) + " " + sagat
        } else if (diff < DAY_MILLIS * 30.43675) {
            val roundup = diff / WEEK_MILLIS
            val b = roundup.toInt()
            return gun + " " + wagt(ay) + " " + sagat
        } else if (diff < 2 * MONTH_MILLIS) {
            return gun + " " + wagt(ay) + " " + sagat
        } else if (diff < WEEK_MILLIS * 52.2) {
            val roundup = diff / MONTH_MILLIS
            val b = roundup.toInt()
            return gun + " " + wagt(ay) + " " + sagat
        } else if (diff < 2 * YEAR_MILLIS) {
            return gun + "." + wagt(ay) + "." + yyl + "," + sagat
        } else {
            val roundup = diff / YEAR_MILLIS
            val b = roundup.toInt()
            return gun + "." + wagt(ay) + "." + yyl + "," + sagat
        }
    }

    private fun wagt(ay: String): String? {
        val yanwar = "01"
        val fewral = "02"
        val mart = "03"
        val aprel = "04"
        val may = "05"
        val iyun = "06"
        val iyul = "07"
        val awgust = "08"
        val sentyabr = "09"
        val oktyabr = "10"
        val noyabr = "11"
        val dekabr = "12"
        var buay: String? = null


        if (ay == yanwar) {
            return "Jan".also { buay = it }
        }
        if (ay == fewral) {
            return "Feb".also { buay = it }
        }
        if (ay == mart) {
            return "Mar".also { buay = it }
        }
        if (ay == aprel) {
            return "April".also { buay = it }
        }
        if (ay == may) {
            return "May".also { buay = it }
        }
        if (ay == iyun) {
            return "June".also { buay = it }
        }
        if (ay == iyul) {
            return "July".also { buay = it }
        }
        if (ay == awgust) {
            return "August".also { buay = it }
        }
        if (ay == sentyabr) {
            return "September".also { buay = it }
        }
        if (ay == oktyabr) {
            return "October".also { buay = it }
        }
        if (ay == noyabr) {
            return "November".also { buay = it }
        }
        if (ay == dekabr) {
            return "December".also { buay = it }
        }
        return buay
    }
}

