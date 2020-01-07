package ch.bbbaden.choreapp.parent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.parent.child.ChildRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_parent_profile.*

class ParentProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parent_profile, container, false)
    }

    private lateinit var auth: FirebaseAuth

    private val smallerDimension: Int
        get() {
            val manager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            Pair(width, height)
            val smallerDimension = if (width < height) width else height
            return smallerDimension * 3 / 4
        }

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
            adapter = ChildRecyclerAdapter(it)
            adapter.notifyDataSetChanged()
            recyclerViewChildren.adapter = adapter
        }
    }
}
