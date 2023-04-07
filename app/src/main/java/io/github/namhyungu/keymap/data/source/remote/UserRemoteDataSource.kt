package io.github.namhyungu.keymap.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.namhyungu.keymap.data.User
import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.util.await

class UserRemoteDataSource(
    private val auth: FirebaseAuth,
) : UserDataSource {
    override suspend fun signIn(token: String): String? {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val result = auth.signInWithCredential(credential).await()
        return result?.user?.uid
    }

    override fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return User(uid)
    }
}