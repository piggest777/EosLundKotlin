package se.eoslund.piggest.services

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun getFormattedDate(date: Date, dateReturnFormat: DateFormat): String {
        val datePattern = "E, dd/MM"
        val timePattern = "HH:mm"
        val dayMonthFormat = "dd MMM"

        return when(dateReturnFormat){
            DateFormat.DATE ->
                formatDate(date, datePattern)
            DateFormat.TIME->
                formatDate(date, timePattern)
            DateFormat.DAY_AND_MONTH ->
                formatDate(date, dayMonthFormat)

        }
    }

    private fun formatDate(date: Date, pattern: String): String{
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }



}

enum class DateFormat() {
    DATE,
    TIME,
    DAY_AND_MONTH;
}