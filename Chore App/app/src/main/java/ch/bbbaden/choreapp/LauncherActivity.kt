package ch.bbbaden.choreapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.bbbaden.choreapp.child.ChildActivity
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.models.UserDAO
import ch.bbbaden.choreapp.parent.ParentActivity
import ch.bbbaden.choreapp.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class LauncherActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            UserDAO().getUser(currentUser.uid) {
                when (it) {
                    is Parent -> {
                        val intent = Intent(this, ParentActivity::class.java)
                        intent.putExtra("user", it)
                        startActivity(intent)
                    }
                    is Child -> {
                        val intent = Intent(this, ChildActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            "Logged in user not referenced in DB.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.w(this::class.simpleName, "Logged in user not referenced in DB.")
                        auth.signOut()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
