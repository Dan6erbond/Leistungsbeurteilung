package ch.bbbaden.choreapp

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ch.bbbaden.choreapp.child.ChildActivity
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.parent.ParentActivity
import ch.bbbaden.choreapp.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_launcher.*


class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val animator = ValueAnimator.ofInt(0, 56)
        animator.addUpdateListener {
            val params = imageView.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = it.animatedValue as Int
            imageView.layoutParams = params
            imageView.requestLayout()
        }
        animator.duration = 600
        animator.start()

        UserManager.getUser {
            when (it) {
                is Parent -> {
                    val intent = Intent(this, ParentActivity::class.java)
                    startActivity(intent)
                }
                is Child -> {
                    val intent = Intent(this, ChildActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
