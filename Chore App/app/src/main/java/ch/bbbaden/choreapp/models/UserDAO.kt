package ch.bbbaden.choreapp.models

class UserDAO {

    fun getUser(uid: String, callback: ((user: Any?) -> Unit)?) {
        ParentDAO().getParent(uid) { user ->
            if (user != null) {
                callback?.invoke(user)
            } else {
                ChildDAO().getChild(uid) { child ->
                    if (child != null) {
                        callback?.invoke(child)
                    } else {
                        callback?.invoke(null)
                    }
                }
            }
        }
    }

}