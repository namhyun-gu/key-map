package io.github.namhyungu.keymap.domain.signin

import io.github.namhyungu.keymap.data.User
import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.domain.UseCase
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userDataSource: UserDataSource
) : UseCase<Unit, User>() {

    override suspend fun execute(parameters: Unit): User {
        return requireNotNull(userDataSource.getCurrentUser())
    }
}