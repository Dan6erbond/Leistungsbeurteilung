package ch.bbbaden.choreapp.parent.chore

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Chore

class AddChoreDialogFragment : DialogFragment() {

    internal lateinit var listener: AddChoreDialogListener

    interface AddChoreDialogListener {
        fun addChore(dialog: DialogFragment, chore: Chore)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.dialog_fragment_add_chore)
                .setPositiveButton(R.string.add,
                    DialogInterface.OnClickListener { _, _ ->
                        listener.addChore(this, Chore())
                    })
                .setNegativeButton(R.string.fui_cancel,
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}