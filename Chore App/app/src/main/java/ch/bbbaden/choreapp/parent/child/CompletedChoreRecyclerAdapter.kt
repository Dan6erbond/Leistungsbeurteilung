package ch.bbbaden.choreapp.parent.child

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.displayTime
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.ChoreDAO
import ch.bbbaden.choreapp.models.CompletedChore
import kotlinx.android.synthetic.main.card_completed_chore.view.*

class CompletedChoreRecyclerAdapter(private val completedChores: List<CompletedChore>) :
    RecyclerView.Adapter<CompletedChoreRecyclerAdapter.AssignmentHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssignmentHolder {
        val inflatedView = parent.inflate(R.layout.card_completed_chore, false)
        return AssignmentHolder(inflatedView)
    }

    override fun getItemCount() = completedChores.size

    override fun onBindViewHolder(holder: AssignmentHolder, position: Int) {
        val completedChore = completedChores[position]
        holder.bindItem(completedChore)
    }

    class AssignmentHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private var completedChore: CompletedChore? = null

        @SuppressLint("SetTextI18n")
        fun bindItem(completedChore: CompletedChore) {
            this.completedChore = completedChore

            ChoreDAO().getChore(completedChore.chore!!.id) {
                view.choreName.text = it!!.name
            }
            view.timeCompleted.text = completedChore.time?.displayTime
        }
    }
}