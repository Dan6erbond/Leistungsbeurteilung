package ch.bbbaden.choreapp

import android.content.Context
import android.graphics.Point
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun getSmallerDimension(manager: WindowManager): Int {
    val display = manager.defaultDisplay
    val point = Point()
    display.getSize(point)
    val width = point.x
    val height = point.y
    Pair(width, height)
    val smallerDimension = if (width < height) width else height
    return smallerDimension * 3 / 4
}

val AppCompatActivity.smallerDimension: Int
    get() {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return getSmallerDimension(manager)
    }

val RecyclerView.ViewHolder.smallerDimension: Int
    get() {
        val manager = itemView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return getSmallerDimension(manager)
    }

val Fragment.smallerDimension: Int
    get() {
        val manager =
            activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return getSmallerDimension(manager)
    }

fun getDateDisplayTime(date: Date): String {
    val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
    val timeFormat = SimpleDateFormat.getTimeInstance()
    return if (DateUtils.isToday(date.time))
        timeFormat.format(date) else dateTimeFormat.format(date)
}

val Timestamp.displayTime: String
    get() {
        val date = toDate()
        return getDateDisplayTime(date)
    }

val Date.displayTime: String
    get() {
        return getDateDisplayTime(this)
    }