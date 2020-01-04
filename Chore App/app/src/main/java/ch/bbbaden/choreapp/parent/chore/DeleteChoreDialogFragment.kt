package ch.bbbaden.choreapp.parent.chore

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Chore

class DeleteChoreDialogFragment(private val listener: DeleteChoreDialogListener, val chore: Chore) : DialogFragment() {

    interface DeleteChoreDialogListener {
        fun deleteChore(dialog: DialogFragment, chore: Chore)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.remove_chore_confirmation)
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    listener.deleteChore(this, chore)
                }
                .setNegativeButton(
                    R.string.fui_cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}