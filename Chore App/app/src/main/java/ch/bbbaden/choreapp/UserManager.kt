package ch.bbbaden.choreapp

import android.util.Log
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.ChildDAO
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.models.ParentDAO
import com.google.firebase.auth.FirebaseAuth

object UserManager {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var parent: Parent? = null
    var child: Child? = null

    fun getUser(callback: ((Any?) -> Unit)?) {
        auth.currentUser?.let {
            ParentDAO().getParent(it.uid) { parent ->
                if (parent != null) {
                    this.parent = parent
                    callback?.invoke(parent)
                } else {
                    ChildDAO().getChild(it.uid) { child ->
                        if (child != null) {
                            this.child = child
                            callback?.invoke(child)
                        } else {
                            Log.w(this::class.simpleName, "Logged in user not referenced in DB.")
                            auth.signOut()
                            callback?.invoke(null)
                        }
                    }
                }
            }
        } ?: callback?.invoke(null)
    }

    fun signOut() {
        parent = null
        child = null
        auth.signOut()
    }
}