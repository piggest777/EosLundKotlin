package se.eoslund.piggest.services

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
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

    fun getAge(dateOfBirth: String): String {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        val date = LocalDate.parse(dateOfBirth, formatter) ?: return ""
        val currentDate = LocalDate.now()

        return Period.between(date, currentDate).years.toString()
    }




}

enum class DateFormat() {
    DATE,
    TIME,
    DAY_AND_MONTH;
}