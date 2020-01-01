package ch.bbbaden.choreapp.signin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ch.bbbaden.choreapp.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlin.system.exitProcess


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(signInToolbar)

        val host: NavHostFragment = signInFragment as NavHostFragment? ?: return
        val navController = host.navController

        signInNavigation.setupWithNavController(navController)

        signInNavigation.setOnNavigationItemSelectedListener { menuItem ->
            signInFragment.findNavController().navigate(menuItem.itemId)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_in_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_exit -> {
            finishAffinity()
            exitProcess(0)
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
