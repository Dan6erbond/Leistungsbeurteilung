package ch.bbbaden.choreapp.parent.chore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Chore
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.models.ParentDAO
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_parent_chores.*

class ParentChoresFragment : Fragment(), AddChoreDialogFragment.AddChoreDialogListener {

    companion object {
        const val RC_CHORE_DETAILS = 0
    }

    private var parent: Parent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parent = arguments?.get("user") as Parent?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parent_chores, container, false)
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreRecyclerAdapter

    var errorCardViewOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        linearLayoutManager = LinearLayoutManager(context)
        recyclerViewChores.layoutManager = linearLayoutManager

        if (parent != null) {
            setupUI()
        } else {
            ParentDAO().getParent(auth.currentUser!!.uid) {
                it?.let {
                    parent = it
                    setupUI()
                }
            }
        }

        closeErrorCardView()
        closeErrorCardViewBtn.setOnClickListener {
            closeErrorCardView()
        }
    }

    private fun setupUI() {
        adapter = ChoreRecyclerAdapter(
            parent!!.chores,
            this
        )
        parent?.fetchChores {
            adapter.notifyDataSetChanged()
        }
        recyclerViewChores.adapter = adapter

        fabAddChore.setOnClickListener {
            val dialog = AddChoreDialogFragment(this)
            dialog.show(fragmentManager!!, "AddChoreDialogFragment")
        }
    }

    fun openDetails(chore: Chore) {
        val intent = Intent(context, ChoreDetailActivity::class.java)
        intent.putExtra("chore", chore)
        intent.putExtra("parent", parent)
        startActivityForResult(
            intent,
            RC_CHORE_DETAILS
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CHORE_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                parent = data?.extras?.get("parent") as Parent?
                setupUI()
            }
        }
    }

    override fun addChore(dialog: DialogFragment, chore: Chore) {
        parent?.addChore(chore) {
            if (it != null) {
                adapter.notifyDataSetChanged()
            } else {
                openErrorCardView()
                Handler().postDelayed({
                    closeErrorCardView()
                }, 1000)
            }
        }
    }

    private fun closeErrorCardView() {
        if (errorCardViewOpen) {
            slideErrorCardView(View.GONE)
            errorCardViewOpen = false
        }
    }

    private fun openErrorCardView() {
        if (!errorCardViewOpen) {
            slideErrorCardView(View.VISIBLE)
            errorCardViewOpen = true
        }
    }

    private fun slideErrorCardView(visibility: Int) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(R.id.savingCardView)

        TransitionManager.beginDelayedTransition(
            errorCardView.parent as ViewGroup,
            transition
        )
        errorCardView.visibility = visibility
    }
}
