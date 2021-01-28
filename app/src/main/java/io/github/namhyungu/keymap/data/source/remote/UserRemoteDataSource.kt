package io.github.namhyungu.keymap.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.namhyungu.keymap.data.User
import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.util.await

class UserRemoteDataSource(
    private val auth: FirebaseAuth,
) : UserDataSource {
    override suspend fun signIn(token: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val result = auth.signInWithCredential(credential).await()
        return result != null
    }

    override fun getCurrentUser(): User? {
        return if (auth.currentUser != null) {
            User(auth.uid!!)
        } else {
            null
        }
    }
}