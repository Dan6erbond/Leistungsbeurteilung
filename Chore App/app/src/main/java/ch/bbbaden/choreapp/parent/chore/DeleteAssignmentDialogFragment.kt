package ch.bbbaden.choreapp.parent.chore

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Assignment

class DeleteAssignmentDialogFragment(
    private val listener: DeleteAssignmentDialogListener,
    private val assignment: Assignment
) : DialogFragment() {

    interface DeleteAssignmentDialogListener {
        fun deleteAssignment(
            dialog: DialogFragment,
            assignment: Assignment
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.remove_assignment_confirmation)
                .setPositiveButton(
                    R.string.yes
                ) { _, _ ->
                    listener.deleteAssignment(this, assignment)
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