package ch.bbbaden.choreapp.child.chore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Child
import kotlinx.android.synthetic.main.fragment_child_chores.*

class ChildChoresFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_child_chores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        UserManager.child?.let {
            setupUI()
        } ?: run {
            UserManager.getUser {
                if (it is Child) setupUI()
                else TODO("Show error")
            }
        }
    }

    private fun setupUI() {
        adapter =
            ChoreRecyclerAdapter(UserManager.child!!.chores)
        UserManager.child!!.fetchChores {
            adapter.notifyDataSetChanged()
        }
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener {
            UserManager.child!!.fetchChores {
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}