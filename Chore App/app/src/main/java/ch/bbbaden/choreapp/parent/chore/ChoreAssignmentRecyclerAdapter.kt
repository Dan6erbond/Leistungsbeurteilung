package ch.bbbaden.choreapp.parent.chore

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.inflate
import ch.bbbaden.choreapp.models.Assignment
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Chore
import ch.bbbaden.choreapp.parent.child.ChildArrayAdapter
import kotlinx.android.synthetic.main.card_chore_assignment.view.*

class ChoreAssignmentRecyclerAdapter(private val assignments: List<Assignment>) :
    RecyclerView.Adapter<ChoreAssignmentRecyclerAdapter.ChoreHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChoreHolder {
        val inflatedView = parent.inflate(R.layout.card_chore_assignment, false)
        return ChoreHolder(
            inflatedView
        )
    }

    override fun getItemCount() = assignments.size

    override fun onBindViewHolder(holder: ChoreHolder, position: Int) {
        val assignment = assignments[position]
        holder.bindItem(assignment)
    }

    class ChoreHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var assignment: Assignment? = null
        private var detailsOpen = false

        private val detailsTargetHeight: Int
            get() {
                val matchParentSpec = View.MeasureSpec.makeMeasureSpec(
                    (view.assignmentDetails.parent as View).width,
                    View.MeasureSpec.EXACTLY
                )
                val wrapContentSpec =
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                view.assignmentDetails.measure(matchParentSpec, wrapContentSpec)
                return view.assignmentDetails.measuredHeight
            }

        private val detailsAnimationTime: Long
            get() {
                return (detailsTargetHeight / view.assignmentDetails.context.resources.displayMetrics.density).toLong() * 3
            }

        init {
            view.assignmentDetails.layoutParams.height = 0
        }

        @SuppressLint("SetTextI18n")
        fun bindItem(assignment: Assignment) {
            this.assignment = assignment
            view.startDate.setText(assignment.getDisplayTime(assignment.startDate))

            view.choreAssignmentCollapseButton.setOnClickListener {
                toggleDetails()
            }

            val chore = (view.context as Activity).intent.extras?.get("chore") as Chore?
            val childArrayAdapter =
                ChildArrayAdapter(
                    view.context,
                    chore!!.parent!!.childrenL
                )
            view.childSpinner.adapter = childArrayAdapter


            var childIndex = 0
            for (i in chore!!.parent!!.childrenL.indices) {
                if (chore!!.parent!!.childrenL[i].userId == assignment.assignedTo) {
                    childIndex = i
                    break
                }
            }

            view.childName.text = chore!!.parent!!.childrenL[childIndex].first
            view.childSpinner.setSelection(childIndex)

            view.childSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val child = parent!!.getItemAtPosition(position) as Child
                    assignment.assignedTo = child.userId
                }

            }

            view.repeatValue.setText(assignment.repeat?.get("value")!!.toString())

            val unitAdapter = ArrayAdapter(
                view.context,
                R.layout.spinner_item_selected,
                Assignment.repeatValues
            )
            unitAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            view.repeatUnit.adapter = unitAdapter
            view.repeatUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val unit = parent!!.getItemAtPosition(position)
                    assignment.repeat["unit"] = unit
                }

            }
        }

        private fun toggleDetails() {
            if (detailsOpen) {
                closeDetails()
            } else {
                openDetails()
            }

            detailsOpen = !detailsOpen
        }

        private fun openDetails() {
            val targetHeight = detailsTargetHeight
            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                view.assignmentDetails.layoutParams.height =
                    if (value == detailsTargetHeight) ActionBar.LayoutParams.WRAP_CONTENT else value
                view.assignmentDetails.requestLayout()
            }

            animator.duration = detailsAnimationTime
            animator.start()
        }

        private fun closeDetails() {
            val animator = ValueAnimator.ofInt(view.assignmentDetails.measuredHeight, 0)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                view.assignmentDetails.layoutParams.height = value
                view.assignmentDetails.requestLayout()
            }

            animator.duration = detailsAnimationTime
            animator.start()
        }
    }
}