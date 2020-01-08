package ch.bbbaden.choreapp.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import kotlinx.android.synthetic.main.dialog_fragment_input.view.*
import kotlinx.android.synthetic.main.dialog_fragment_name.view.title

class InputDialogFragment(
    private val listener: EditNameDialogListener,
    private val title: String,
    private val hint: String,
    private val positiveButtonText: String? = null
) : DialogFragment() {

    interface EditNameDialogListener {
        fun setInput(dialog: DialogFragment, input: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_fragment_input, null)
            view.title.text = title
            view.input.hint = hint
            builder.setView(view)
                .setPositiveButton(
                    positiveButtonText ?: resources.getString(R.string.done)
                ) { _, _ ->
                    listener.setInput(this, view.input.text.toString().trim())
                }
                .setNegativeButton(
                    R.string.fui_cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}