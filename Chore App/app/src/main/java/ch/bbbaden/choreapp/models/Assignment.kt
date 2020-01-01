package ch.bbbaden.choreapp.models

import com.google.firebase.Timestamp
import java.util.*

data class Assignment(
    val name: String? = null,
    val description: String? = null,
    val assignedTo: String? = null,
    val repeat: HashMap<String, *>? = null,
    val startDate: Timestamp? = null
) {

    fun getNextDate(): Date? {
        val currentDate = Calendar.getInstance().time
        val nextDate = startDate!!.toDate()

        if (nextDate.after(currentDate)) {
            return nextDate
        } else if (repeat != null) {
            val value = (repeat["value"] as Long).toInt()
            when (repeat["unit"]) {
                "day" -> return addToDate(nextDate, currentDate, Calendar.DATE, value)
                "week" -> return addToDate(nextDate, currentDate, Calendar.DATE, value * 7)
                "month" -> return addToDate(nextDate, currentDate, Calendar.MONTH, value)
                "year" -> return addToDate(nextDate, currentDate, Calendar.YEAR, value)
            }
            return null
        } else {
            return null
        }
    }

    private fun addToDate(date: Date, afterDate: Date, unit: Int, value: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        while (calendar.time.before(afterDate)) {
            calendar.add(unit, value)
        }
        return calendar.time
    }

}