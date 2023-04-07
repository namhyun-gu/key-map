package io.github.namhyungu.keymap.ui.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.namhyungu.keymap.data.source.UserDataSource
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
) : ViewModel() {

    private val _loginEvent: MutableSharedFlow<LoginEvent> = MutableSharedFlow()
    val loginEvent: MutableSharedFlow<LoginEvent>
        get() = _loginEvent

    init {

    }
//    private val _requestSignInEvent = MutableLiveData<Event<Unit>>()
//    val requestSignInEvent: LiveData<Event<Unit>> = _requestSignInEvent
//
//    private val _moveHomeEvent = MutableLiveData<Event<Unit>>()
//    val moveHomeEvent: LiveData<Event<Unit>> = _moveHomeEvent
//
//    fun start() {
//        val user = userDataSource.getCurrentUser()
//        if (user != null) {
//            _moveHomeEvent.value = Event(Unit)
//        } else {
//            _requestSignInEvent.value = Event(Unit)
//        }
//    }
}