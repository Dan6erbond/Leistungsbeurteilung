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
import kotlinx.android.synthetic.main.card_view_parent_child.view.qrCode
import kotlinx.android.synthetic.main.dialog_fragment_qr.view.*

class QRDialogFragment(private val bitmap: Bitmap, private val title: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_fragment_qr, null)

            view.title.text = title
            view.qrCode.setImageBitmap(bitmap)

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