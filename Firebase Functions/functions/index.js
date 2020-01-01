const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

const db = admin.firestore();

exports.getToken = functions.https.onCall((data, context) => {
  if (!("uid" in data)) {
    throw new functions.https.HttpsError("invalid-argument", "Request data doesn't contain 'uid'.");
  }

  let uid = data.uid
  return admin.auth().createCustomToken(uid)
    .then(customToken => {
      token = customToken
      return {
        "token": customToken,
        "expires": 1
      }
    })
    .catch(error => {
      console.error('Error creating custom token:', error);
      throw new functions.https.HttpsError("unknown", "Firebase couldn't create custom token.", {
        "error-details": error
      });
    })
});

/* exports.getParentID = functions.https.onCall((data, context) => {
  if (!("uid" in data)) {
    throw new functions.https.HttpsError("invalid-argument", "Request data doesn't contain 'uid'.");
  }

  let uid = data.uid

  db.collection('users')
} */

exports.addChild = functions.firestore.document("/users/{user}/children/{child}").onCreate((snapshot, context) => {
  const child = snapshot.data()
  return db.doc(`/users/${context.params.user}`).update({
    "children": admin.firestore.FieldValue.arrayUnion(context.params.child)
  })
})

exports.removeChild = functions.firestore.document("/users/{user}/children/{child}").onDelete((snapshot, context) => {
  const child = snapshot.data()
  return db.doc(`/users/${context.params.user}`).update({
    "children": admin.firestore.FieldValue.arrayRemove(context.params.child)
  })
})
