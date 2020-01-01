package ch.bbbaden.choreapp.parent

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.ParentDAO
import ch.bbbaden.choreapp.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_parent.*


class ParentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent)
        setSupportActionBar(parentToolbar)

        auth = FirebaseAuth.getInstance()

        val host: NavHostFragment = parentFragment as NavHostFragment? ?: return
        val navController = host.navController

        ParentDAO().getParent(auth.currentUser!!.uid) {
            it?.let {
                val bundle = bundleOf("user" to it)
                navController.setGraph(R.navigation.parent_graph, bundle) // setting the graph programmatically to pass args
                parentNavigation.setupWithNavController(navController)

                parentNavigation.setOnNavigationItemSelectedListener { menuItem ->
                    parentFragment.findNavController().navigate(menuItem.itemId, bundle)
                    true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.parent_menu, menu)
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