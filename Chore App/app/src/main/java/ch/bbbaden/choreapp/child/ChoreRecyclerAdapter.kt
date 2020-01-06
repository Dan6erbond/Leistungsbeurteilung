package ch.bbbaden.choreapp.child

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.Chore
import kotlinx.android.synthetic.main.card_view_chore.view.*

class ChoreRecyclerAdapter(private val chores: List<Chore>) :
    RecyclerView.Adapter<ChoreRecyclerAdapter.ChoreHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoreHolder {
        val inflatedView = parent.inflate(R.layout.card_view_chore, false)
        return ChoreHolder(
            inflatedView
        )
    }

    override fun getItemCount() = chores.size

    override fun onBindViewHolder(holder: ChoreHolder, position: Int) {
        val itemChore = chores[position]
        holder.bindChore(itemChore)
    }

    class ChoreHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var chore: Chore? = null

        init {
            view.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(chore: Chore) {
            this.chore = chore
            // view.choreImage.setImageResource(R.drawable.ic_menu_camera)
            view.choreName.text = chore.name
            view.choreTime.text = chore.getDisplayTime()
            view.choreDescription.text = chore.description
        }

        override fun onClick(v: View) {
            Toast.makeText(itemView.context, "${chore?.id} clicked.", Toast.LENGTH_SHORT).show()
        }
    }
}