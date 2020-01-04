package ch.bbbaden.choreapp.parent

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.Child
import kotlinx.android.synthetic.main.card_view_child.view.*


class ChildRecyclerAdapter(private val children: List<Child>) :
    RecyclerView.Adapter<ChildRecyclerAdapter.ChildHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildHolder {
        val inflatedView = parent.inflate(R.layout.card_view_child, false)
        return ChildHolder(
            inflatedView
        )
    }

    override fun getItemCount() = children.size

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val itemChore = children[position]
        holder.bindChore(itemChore)
    }

    class ChildHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var child: Child? = null

        init {
            v.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(child: Child) {
            this.child = child
            // view.childImage.setImageResource(R.drawable.ic_menu_camera)
            view.childName.text = child.first
        }

        override fun onClick(v: View) {
            Toast.makeText(itemView.context, "${child?.userId} clicked.", Toast.LENGTH_SHORT).show()
        }
    }
}