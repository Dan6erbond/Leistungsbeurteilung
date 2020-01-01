package ch.bbbaden.choreapp.parent

import android.annotation.SuppressLint
import android.app.ActionBar
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
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
            view.layoutParams.height = 0
        }

        @SuppressLint("SetTextI18n")
        fun bindChore(child: Child) {
            this.child = child
            // view.childImage.setImageResource(R.drawable.ic_menu_camera)
            view.childName.text = child.first

            // val targetHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96f, resources.displayMetrics).toInt()
            // val matchParentSpec = View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY)
            val wrapContentSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(0, wrapContentSpec) // widthMeasureSpec should be matchParentSpec if measuredWidth is to be used
            val targetHeight = view.measuredHeight

            val a: Animation = object : Animation() { // ValueAnimator as an alternative when we know the target height
                override fun applyTransformation(
                    interpolatedTime: Float,
                    t: Transformation?
                ) {
                    view.layoutParams.height =
                        if (interpolatedTime == 1f) ActionBar.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // Expansion speed of 1dp/ms
            a.duration = (targetHeight / view.context.resources.displayMetrics.density).toLong() * 5
            view.startAnimation(a)
        }

        override fun onClick(v: View) {
            Toast.makeText(itemView.context, "${child?.userId} clicked.", Toast.LENGTH_SHORT).show()
        }
    }
}