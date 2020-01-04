package ch.bbbaden.choreapp.parent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Chore
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.models.ParentDAO
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_parent_chores.*

class ParentChoresFragment : Fragment(), AddChoreDialogFragment.AddChoreDialogListener {

    companion object {
        val RC_CHORE_DETAILS = 0
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
    }

    private fun setupUI() {
        adapter = ChoreRecyclerAdapter(parent!!.chores, this)
        parent?.fetchChores {
            adapter.notifyDataSetChanged()
        }
        recyclerViewChores.adapter = adapter

        fabAddChore.setOnClickListener {
            val dialog = AddChoreDialogFragment()
            dialog.show(fragmentManager!!, "AddChoreDialogFragment")
        }
    }

    fun openDetails(chore: Chore) {
        val intent = Intent(context, ChoreDetailActivity::class.java)
        intent.putExtra("chore", chore)
        startActivityForResult(intent, RC_CHORE_DETAILS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CHORE_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                setupUI()
            }
        }
    }

    override fun addChore(dialog: DialogFragment, chore: Chore) {
        println(chore)
    }
}
