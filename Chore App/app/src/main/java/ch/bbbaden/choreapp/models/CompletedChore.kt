package ch.bbbaden.choreapp.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class CompletedChore(val chore: DocumentReference? = null, val time: Timestamp? = null)