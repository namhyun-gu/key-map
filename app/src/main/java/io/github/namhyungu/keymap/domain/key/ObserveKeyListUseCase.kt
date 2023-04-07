package io.github.namhyungu.keymap.domain.key

import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.data.Result
import io.github.namhyungu.keymap.data.source.KeyDataSource
import io.github.namhyungu.keymap.domain.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveKeyListUseCase @Inject constructor(
    private val keyDataSource: KeyDataSource
) : FlowUseCase<Unit, List<Key>>() {

    override fun execute(parameters: Unit): Flow<Result<List<Key>>> {
        return keyDataSource.observeKeys().map { Result.Success(it) }
    }
}