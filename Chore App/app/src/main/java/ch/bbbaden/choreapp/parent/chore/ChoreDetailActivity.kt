package ch.bbbaden.choreapp.parent.chore

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Chore
import kotlinx.android.synthetic.main.activity_chore_detail.*


class ChoreDetailActivity : AppCompatActivity() {

    private var chore: Chore? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreAssignmentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chore_detail)
        setSupportActionBar(toolbar)

        linearLayoutManager = LinearLayoutManager(this)
        choreAssignments.layoutManager = linearLayoutManager

        chore = intent.extras?.get("chore") as Chore?
        chore?.let {
            choreName.text = it.name
            choreDescription.text = it.description

            adapter =
                ChoreAssignmentRecyclerAdapter(
                    it.assignments
                )
            adapter.notifyDataSetChanged()

            choreAssignments.adapter = adapter
        }

        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(R.id.savingCardView)

        TransitionManager.beginDelayedTransition(savingCardView.parent as ViewGroup, transition)
        savingCardView.visibility = View.GONE
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

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        chore?.let {
            val transition: Transition = Slide(Gravity.BOTTOM)
            transition.duration = 600
            transition.addTarget(R.id.savingCardView)

            TransitionManager.beginDelayedTransition(savingCardView.parent as ViewGroup, transition)
            savingCardView.visibility = View.VISIBLE

            it.parent?.saveChore(it) {success ->
                if (success) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }


}
