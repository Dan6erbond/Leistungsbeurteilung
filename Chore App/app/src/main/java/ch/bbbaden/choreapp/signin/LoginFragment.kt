package ch.bbbaden.choreapp.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.parent.ParentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        progressBar.isInvisible = true

        signInBtn.setOnClickListener {
            when {
                emailTxt.text.isNullOrEmpty() -> {
                    Toast.makeText(
                        activity, "Please enter your email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                passwordTxt.text.isNullOrEmpty() -> {
                    Toast.makeText(
                        activity, "Please enter your password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    signIn(emailTxt.text.toString(), passwordTxt.text.toString())
                }
            }
        }
    }

    private fun signIn(email: String, password: String) {
        progressBar.isInvisible = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser


                    Toast.makeText(
                        activity, "Successfully signed in as ${user?.email}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    activity!!.let {
                        val intent = Intent(it, ParentActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    }
                } else {
                    Log.e(
                        this::class.simpleName,
                        task.exception?.message ?: task.exception.toString()
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            activity?.applicationContext, "Invalid email or password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            activity?.applicationContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            this::class.simpleName,
                            task.exception?.message ?: task.exception.toString()
                        )
                    }
                }
            }
    }
}
