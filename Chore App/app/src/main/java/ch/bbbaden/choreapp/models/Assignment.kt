package ch.bbbaden.choreapp.models

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Assignment(
    var assignedTo: String? = null,
    val repeat: HashMap<String, Any>? = hashMapOf()
) : Serializable {

    lateinit var startDate: Date

    companion object {
        val repeatValues = listOf("day", "week", "month", "year")
    }

    constructor(
        assignedTo: String? = null,
        repeat: HashMap<String, Any>? = hashMapOf(),
        startDate: Timestamp? = null
    ) : this(assignedTo, repeat) {
        this.startDate = startDate!!.toDate()
    }

    constructor(
        assignedTo: String? = null,
        repeat: HashMap<String, Any>? = hashMapOf(),
        startDate: Date? = null
    ) : this(assignedTo, repeat) {
        this.startDate = startDate!!
    }

    fun getNextDate(): Date? {
        val currentDate = Calendar.getInstance().time
        val nextDate = startDate
        val repeat = repeat

        when {
            nextDate.after(currentDate) -> {
                return nextDate
            }
            repeat != null -> {
                val value = (repeat["value"] as Long).toInt()
                when (repeat["unit"]) {
                    "day" -> return addToDate(nextDate, currentDate, Calendar.DATE, value)
                    "week" -> return addToDate(nextDate, currentDate, Calendar.DATE, value * 7)
                    "month" -> return addToDate(nextDate, currentDate, Calendar.MONTH, value)
                    "year" -> return addToDate(nextDate, currentDate, Calendar.YEAR, value)
                }
                return null
            }
            else -> {
                return null
            }
        }
    }

    fun getDisplayTime(date: Date): String {
        val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
        val timeFormat = SimpleDateFormat.getTimeInstance()
        return if (DateUtils.isToday(date.time))
            timeFormat.format(date) else dateTimeFormat.format(date)
    }

    private fun addToDate(date: Date, afterDate: Date, unit: Int, value: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        while (calendar.time.before(afterDate)) {
            calendar.add(unit, value)
        }
        return calendar.time
    }

    fun getData(): HashMap<String, *> {
        return hashMapOf(
            "assignedTo" to assignedTo,
            "repeat" to hashMapOf(
                "unit" to repeat?.get("unit"),
                "value" to repeat?.get("value")
            ),
            "startDate" to Timestamp(startDate)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Assignment

        if (assignedTo != other.assignedTo) return false
        if (repeat != other.repeat) return false
        if (startDate != other.startDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assignedTo?.hashCode() ?: 0
        result = 31 * result + (repeat?.hashCode() ?: 0)
        result = 31 * result + startDate.hashCode()
        return result
    }

}