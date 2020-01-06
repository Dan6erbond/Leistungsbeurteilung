package ch.bbbaden.choreapp.parent.chore

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Assignment
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Repeat
import ch.bbbaden.choreapp.parent.child.ChildArrayAdapter
import kotlinx.android.synthetic.main.dialog_fragment_add_assignment.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddAssignmentDialogFragment(private val listener: AddAssignmentDialogListener) :
    DialogFragment() {

    interface AddAssignmentDialogListener {
        fun addAssignment(
            dialog: DialogFragment,
            assignment: Assignment
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_fragment_add_assignment, null)

            val childArrayAdapter =
                ChildArrayAdapter(
                    view.context,
                    UserManager.parent!!.childrenL
                )
            view.childSpinner.adapter = childArrayAdapter

            val dateTimeFormat = SimpleDateFormat.getDateTimeInstance()
            view.startDate.setText(dateTimeFormat.format(Calendar.getInstance().time))

            view.repeatValue.setText("1")

            val unitAdapter = ArrayAdapter(
                view.context,
                R.layout.spinner_item_selected,
                Repeat.units
            )
            unitAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            view.repeatUnit.adapter = unitAdapter

            builder.setView(view)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    val startDate = dateTimeFormat.parse(view.startDate.text.toString())

                    listener.addAssignment(
                        this,
                        Assignment(
                            (view.childSpinner.selectedItem as Child).userId,
                            Repeat(
                                view.repeatUnit.selectedItem as String,
                                view.repeatValue.text.toString().toInt()
                            ),
                            startDate
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