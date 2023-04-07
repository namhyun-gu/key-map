package io.github.namhyungu.keymap.domain.key

import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.data.source.KeyDataSource
import io.github.namhyungu.keymap.domain.UseCase
import javax.inject.Inject

class GetKeyListUserCase @Inject constructor(
    private val keyDataSource: KeyDataSource
) : UseCase<Unit, List<Key>>() {

    override suspend fun execute(parameters: Unit): List<Key> {
        return keyDataSource.getKeys()
    }
}