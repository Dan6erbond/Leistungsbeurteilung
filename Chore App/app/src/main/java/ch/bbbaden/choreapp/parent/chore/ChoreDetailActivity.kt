package ch.bbbaden.choreapp.parent.chore

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.dialogs.ConfirmationDialogFragment
import ch.bbbaden.choreapp.dialogs.InputDialogFragment
import ch.bbbaden.choreapp.models.Assignment
import ch.bbbaden.choreapp.models.Chore
import kotlinx.android.synthetic.main.activity_chore_detail.*


class ChoreDetailActivity : AppCompatActivity(),
    AddAssignmentDialogFragment.AddAssignmentDialogListener,
    ChoreAssignmentRecyclerAdapter.AssignmentHolder.AssignmentHolderListener,
    InputDialogFragment.EditNameDialogListener {

    private lateinit var chore: Chore

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreAssignmentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chore_detail)
        setSupportActionBar(toolbar)

        linearLayoutManager = LinearLayoutManager(this)
        choreAssignments.layoutManager = linearLayoutManager

        val choreId = intent.extras?.get("choreId") as String
        UserManager.parent!!.getChore(choreId) {
            chore = it!!
        }

        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(R.id.savingCardView)

        TransitionManager.beginDelayedTransition(savingCardView.parent as ViewGroup, transition)
        savingCardView.visibility = View.GONE

        setupUI()
    }

    private fun setupUI() {
        choreName.text = chore.name
        choreDescription.text = chore.description

        adapter = ChoreAssignmentRecyclerAdapter(chore.assignments, this)

        choreAssignments.adapter = adapter

        fabAddAssignment.setOnClickListener {
            val dialog = AddAssignmentDialogFragment(this)
            dialog.show(supportFragmentManager, "AddAssignmentDialogFragment")
        }

        editName.setOnClickListener {
            val dialog = InputDialogFragment(
                this,
                resources.getString(R.string.change_name),
                resources.getString(R.string.chore_name)
            )
            dialog.show(supportFragmentManager, "EditChoreNameDialogFragment")
        }

        editDescription.setOnClickListener {
            val dialog = InputDialogFragment(
                this,
                resources.getString(R.string.change_description),
                resources.getString(R.string.chore_description)
            )
            dialog.show(supportFragmentManager, "EditChoreDescriptionDialogFragment")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chore_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            save {
                finish()
            }
            true
        }

        R.id.action_delete -> {
            val dialog = ConfirmationDialogFragment(getString(R.string.remove_chore_confirmation))
                .setPositiveButtonListener {
                    deleteChore()
                }
            dialog.show(supportFragmentManager, "ConfirmationDialogFragment")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun save(callback: ((success: Boolean) -> Unit)? = null) {
        slideSaving(View.VISIBLE)

        UserManager.parent?.saveChore(chore) { success ->
            if (success) {
                setResult(Activity.RESULT_OK)
            }
            callback?.invoke(success)
            slideSaving(View.GONE)
        }
    }

    private fun deleteChore() {
        slideSaving(View.VISIBLE)

        UserManager.parent?.deleteChore(chore) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun slideSaving(visibility: Int) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(R.id.savingCardView)

        TransitionManager.beginDelayedTransition(savingCardView.parent as ViewGroup, transition)
        savingCardView.visibility = visibility
    }

    override fun addAssignment(
        dialog: DialogFragment,
        assignment: Assignment
    ) {
        chore.assignments.add(assignment)
        save {
            if (it) adapter.notifyDataSetChanged()
        }
    }

    override fun deleteAssignment(assignment: Assignment) {
        chore.assignments.remove(assignment)
        save {
            if (it) adapter.notifyDataSetChanged()
        }
    }

    override fun setInput(dialog: DialogFragment, input: String) {
        when (dialog.tag) {
            "EditChoreNameDialogFragment" -> {
                chore.name = input
                save {
                    if (it) choreName.text = chore.name
                }
            }
            "EditChoreDescriptionDialogFragment" -> {
                chore.description = input
                save {
                    if (it) choreDescription.text = chore.description
                }
            }
        }
    }

}
