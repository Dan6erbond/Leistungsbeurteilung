package ch.bbbaden.choreapp.parent

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
import ch.bbbaden.choreapp.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_parent.*


class ParentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent)
        setSupportActionBar(parentToolbar)

        val host: NavHostFragment = parentFragment as NavHostFragment? ?: return
        val navController = host.navController

        UserManager.parent?.let {
            navController.setGraph(R.navigation.parent_graph)
            parentNavigation.setupWithNavController(navController)

            parentNavigation.setOnNavigationItemSelectedListener { menuItem ->
                parentFragment.findNavController().navigate(menuItem.itemId)
                true
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
        UserManager.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}