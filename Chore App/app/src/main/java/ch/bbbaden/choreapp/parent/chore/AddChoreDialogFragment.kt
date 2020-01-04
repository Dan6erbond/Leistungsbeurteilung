package ch.bbbaden.choreapp.parent.chore

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Chore
import kotlinx.android.synthetic.main.dialog_fragment_add_chore.view.*

class AddChoreDialogFragment(private val listener: AddChoreDialogListener) : DialogFragment() {

    interface AddChoreDialogListener {
        fun addChore(dialog: DialogFragment, chore: Chore)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_fragment_add_chore, null)
            builder.setView(view)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.addChore(
                        this,
                        Chore(
                            name = view.choreDialogName.text.toString(),
                            description = view.choreDialogDescription.text.toString()
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