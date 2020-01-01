package ch.bbbaden.choreapp.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.models.ParentDAO
import ch.bbbaden.choreapp.parent.ParentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        signUpBtn.setOnClickListener {
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
                passwordTxt2.text.isNullOrEmpty() -> {
                    Toast.makeText(
                        activity, "Please repeat your password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                firstTxt.text.isNullOrEmpty() -> {
                    Toast.makeText(
                        activity, "Please enter your first name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                passwordTxt.text.toString() != passwordTxt2.text.toString() -> {
                    Toast.makeText(
                        activity, "Make sure both passwords match.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    signUp(
                        emailTxt.text.toString(),
                        passwordTxt.text.toString(),
                        firstTxt.text.toString(),
                        lastTxt.text.toString()
                    )
                }
            }
        }
    }

    fun signUp(email: String, password: String, first: String, last: String?) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful && auth.currentUser != null) {
                    val user = auth.currentUser!!
                    val parent = Parent(user.uid, email, first, last)

                    ParentDAO().addParent(parent)

                    Toast.makeText(
                        activity, "Successfully signed up with ${user.email}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    activity?.let {
                        val intent = Intent(it.applicationContext, ParentActivity::class.java)
                        intent.putExtra("user", parent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    }
                } else if (task.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(
                        activity, "An account with this email has already been created.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        activity, "Sign-up failed.",
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
