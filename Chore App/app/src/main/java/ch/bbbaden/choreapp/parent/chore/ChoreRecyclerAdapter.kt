package ch.bbbaden.choreapp.parent.chore

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.Chore
import kotlinx.android.synthetic.main.card_view_chore.view.*

class ChoreRecyclerAdapter(
    private val chores: List<Chore>,
    private val fragment: ParentChoresFragment
) :
    RecyclerView.Adapter<ChoreRecyclerAdapter.ChoreHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoreHolder {
        val inflatedView = parent.inflate(R.layout.card_view_chore_parent, false)
        return ChoreHolder(
            inflatedView,
            fragment
        )
    }

    override fun getItemCount() = chores.size

    override fun onBindViewHolder(holder: ChoreHolder, position: Int) {
        val itemChore = chores[position]
        holder.bindChore(itemChore)
    }

    class ChoreHolder(private val v: View, val fragment: ParentChoresFragment) :
        RecyclerView.ViewHolder(v), View.OnClickListener {

        private var chore: Chore? = null

        init {
            v.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(chore: Chore) {
            this.chore = chore
            // view.choreImage.setImageResource(R.drawable.ic_menu_camera)
            v.choreName.text = chore.name
            v.choreDescription.text = chore.description
        }

        override fun onClick(v: View) {
            fragment.openDetails(chore!!)
        }
    }
}