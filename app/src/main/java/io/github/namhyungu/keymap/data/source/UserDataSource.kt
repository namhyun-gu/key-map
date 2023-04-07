package io.github.namhyungu.keymap.data.source

import io.github.namhyungu.keymap.data.User

interface UserDataSource {

    suspend fun signIn(token: String): String?

    fun getCurrentUser(): User?

}