package ch.bbbaden.choreapp.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import kotlinx.android.synthetic.main.card_view_child.view.*

class QRDialogFragment(private val bitmap: Bitmap) : DialogFragment() {

    private val smallerDimension: Int
        get() {
            val manager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            Pair(width, height)
            val smallerDimension = if (width < height) width else height
            return smallerDimension * 3 / 4
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_fragment_child_qr, null)
            view.childQR.setImageBitmap(bitmap)

            builder.setView(view)
                .setPositiveButton(
                    R.string.done
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}