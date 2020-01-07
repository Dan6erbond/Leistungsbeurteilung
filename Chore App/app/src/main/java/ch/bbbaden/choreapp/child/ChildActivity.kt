package ch.bbbaden.choreapp.child

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.UserManager
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_child.*

class ChildActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)
        setSupportActionBar(childToolbar)

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
        val host: NavHostFragment = childFragment as NavHostFragment? ?: return
        val navController = host.navController

        navController.setGraph(R.navigation.child_graph)
        childNavigation.setupWithNavController(navController)

        childNavigation.setOnNavigationItemSelectedListener { menuItem ->
            childFragment.findNavController().navigate(menuItem.itemId)
            true
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
