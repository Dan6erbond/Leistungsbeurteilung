package ch.bbbaden.choreapp.parent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.parent.child.ChildDetailActivity
import ch.bbbaden.choreapp.parent.child.ChildRecyclerAdapter
import ch.bbbaden.choreapp.smallerDimension
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_parent_profile.*

const val RC_CHILD_DETAILS = 0

class ParentProfileFragment : Fragment(), ChildRecyclerAdapter.ChildHolder.ChildHolderListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parent_profile, container, false)
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChildRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        linearLayoutManager = LinearLayoutManager(context)
        recyclerViewChildren.layoutManager = linearLayoutManager

        UserManager.parent?.let {
            setupUI()
        } ?: run {
            UserManager.getUser {
                if (it is Parent) {
                    setupUI()
                } else {
                    TODO("Show error")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        parentImg.setImageBitmap(UserManager.parent?.getQRCode(smallerDimension))
        nameTxt.text = "${UserManager.parent?.first} ${UserManager.parent?.last ?: ""}"
        emailTxt.text = UserManager.parent?.email
        UserManager.parent?.fetchChildren {
            adapter = ChildRecyclerAdapter(it, this)
            adapter.notifyDataSetChanged()
            recyclerViewChildren.adapter = adapter
        }
    }

    override fun openDetails(child: Child) {
        val intent = Intent(context, ChildDetailActivity::class.java)
        intent.putExtra("childId", child.id)
        startActivityForResult(intent, RC_CHILD_DETAILS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHILD_DETAILS) {
            if (resultCode == Activity.RESULT_OK) setupUI()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
