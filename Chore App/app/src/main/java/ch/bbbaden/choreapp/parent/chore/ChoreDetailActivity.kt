package ch.bbbaden.choreapp.parent.chore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Assignment
import ch.bbbaden.choreapp.models.Chore
import ch.bbbaden.choreapp.models.Parent
import kotlinx.android.synthetic.main.activity_chore_detail.*


class ChoreDetailActivity : AppCompatActivity(),
    DeleteChoreDialogFragment.DeleteChoreDialogListener,
    AddAssignmentDialogFragment.AddAssignmentDialogListener,
    DeleteAssignmentDialogFragment.DeleteAssignmentDialogListener {

    private var chore: Chore? = null
    private var parent: Parent? = null

    private var assignmentAdded = false
    private var assignmentDeleted = false

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreAssignmentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chore_detail)
        setSupportActionBar(toolbar)

        linearLayoutManager = LinearLayoutManager(this)
        choreAssignments.layoutManager = linearLayoutManager

        chore = intent.extras?.get("chore") as Chore?
        parent = intent.extras?.get("parent") as Parent?

        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(R.id.savingCardView)

        TransitionManager.beginDelayedTransition(savingCardView.parent as ViewGroup, transition)
        savingCardView.visibility = View.GONE

        setupUI()
    }

    private fun setupUI() {
        chore?.let {
            choreName.text = it.name
            choreDescription.text = it.description

            adapter =
                ChoreAssignmentRecyclerAdapter(
                    it.assignments,
                    this
                )
            adapter.notifyDataSetChanged()

            choreAssignments.adapter = adapter

            fabAddAssignment.setOnClickListener {
                val dialog = AddAssignmentDialogFragment(this, parent!!)
                dialog.show(supportFragmentManager, "AddAssignmentDialogFragment")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chore_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            save()
            true
        }

        R.id.action_delete -> {
            val dialog = DeleteChoreDialogFragment(this, chore!!)
            dialog.show(supportFragmentManager, "DeleteChoreDialogFragment")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        chore?.let {
            slideSavingUp()

            parent?.saveChore(it) { success ->
                if (success) {
                    val intent = Intent()
                    intent.putExtra("parent", parent)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun deleteChore(dialog: DialogFragment, chore: Chore) {
        slideSavingUp()

        parent?.deleteChore(chore) {
            if (it) {
                val intent = Intent()
                intent.putExtra("parent", parent)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun slideSavingUp() {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(R.id.savingCardView)

        TransitionManager.beginDelayedTransition(savingCardView.parent as ViewGroup, transition)
        savingCardView.visibility = View.VISIBLE
    }

    override fun addAssignment(
        dialog: DialogFragment,
        assignment: Assignment
    ) {
        chore!!.assignments.add(assignment)
        parent!!.saveChore(chore!!) {
            if (it) {
                assignmentAdded = true
                setupUI()
            } else {
                TODO("Show error to user")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (assignmentAdded || assignmentDeleted) {
            val intent = Intent()
            intent.putExtra("parent", parent)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun deleteAssignment(
        dialog: DialogFragment,
        assignment: Assignment
    ) {
        chore!!.assignments.remove(assignment).let { success ->
            if (success) {
                parent!!.saveChore(chore!!) {
                    if (it) {
                        assignmentDeleted = true
                        setupUI()
                    } else {
                        TODO("Show error to user")
                    }
                }
            } else {
                TODO("Show error to user")
            }
        }
    }

}
