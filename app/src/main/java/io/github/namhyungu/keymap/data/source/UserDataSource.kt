package io.github.namhyungu.keymap.data.source

import io.github.namhyungu.keymap.data.User

interface UserDataSource {

    suspend fun signIn(token: String): Boolean

    fun getCurrentUser(): User?

}