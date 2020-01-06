package ch.bbbaden.choreapp.child

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_child.*

class ChildActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)
        setSupportActionBar(childToolbar)

        linearLayoutManager = LinearLayoutManager(this)
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
        adapter = ChoreRecyclerAdapter(UserManager.child!!.chores)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.child_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_sign_out -> {
            signOut()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        UserManager.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
