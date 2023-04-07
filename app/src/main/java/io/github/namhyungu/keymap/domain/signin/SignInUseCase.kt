package io.github.namhyungu.keymap.domain.signin

import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.domain.UseCase
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userDataSource: UserDataSource
) : UseCase<String?, String>() {

    override suspend fun execute(parameters: String?): String {
        val token = requireNotNull(parameters)
        val signInUid = userDataSource.signIn(token)
        return requireNotNull(signInUid)
    }
}