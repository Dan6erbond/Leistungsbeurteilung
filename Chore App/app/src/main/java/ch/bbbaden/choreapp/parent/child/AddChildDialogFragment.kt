package ch.bbbaden.choreapp.parent.child

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Child
import kotlinx.android.synthetic.main.dialog_fragment_add_child.view.*

class AddChildDialogFragment(private val listener: AddChildDialogListener) : DialogFragment() {

    interface AddChildDialogListener {
        fun addChild(dialog: DialogFragment, child: Child)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_fragment_add_chore, null)
            builder.setView(view)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.addChild(
                        this,
                        Child(
                            first = view.childDialogFirst.text.toString(),
                            last = view.childDialogLast.text.toString()
                        )
                    )
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