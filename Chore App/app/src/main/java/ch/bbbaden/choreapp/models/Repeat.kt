package ch.bbbaden.choreapp.models

import java.io.Serializable

data class Repeat(var unit: String? = null, val value: Int? = null) : Serializable {

    companion object {
        val units = listOf("day", "week", "month", "year")
    }

    enum class RepeatUnit(val text: String? = null) {
        DAY("day"),
        WEEK("week"),
        MONTH("month"),
        YEAR("year")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Repeat

        if (unit != other.unit) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = unit?.hashCode() ?: 0
        result = 31 * result + (value ?: 0)
        return result
    }
}