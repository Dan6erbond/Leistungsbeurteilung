package ch.bbbaden.choreapp.child

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.ChildDAO
import ch.bbbaden.choreapp.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_child.*

class ChildActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChoreRecyclerAdapter

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)
        setSupportActionBar(childToolbar)

        auth = FirebaseAuth.getInstance()

        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        ChildDAO().getChild("EEhtqfYn0q2ERtKMCxr0") {
            it?.let {
                adapter = ChoreRecyclerAdapter(it.chores)
                it.fetchChores {
                    adapter.notifyDataSetChanged()
                }
                recyclerView.adapter = adapter
                swipeRefreshLayout.setOnRefreshListener {
                    it.fetchChores {
                        adapter.notifyDataSetChanged()
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
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
        auth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
