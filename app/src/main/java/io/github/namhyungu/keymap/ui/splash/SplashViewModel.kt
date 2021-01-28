package io.github.namhyungu.keymap.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.util.Event

class SplashViewModel @ViewModelInject constructor(
    private val userDataSource: UserDataSource,
) : ViewModel() {
    private val _requestSignInEvent = MutableLiveData<Event<Unit>>()
    val requestSignInEvent: LiveData<Event<Unit>> = _requestSignInEvent

    private val _moveHomeEvent = MutableLiveData<Event<Unit>>()
    val moveHomeEvent: LiveData<Event<Unit>> = _moveHomeEvent

    fun start() {
        val user = userDataSource.getCurrentUser()
        if (user != null) {
            _moveHomeEvent.value = Event(Unit)
        } else {
            _requestSignInEvent.value = Event(Unit)
        }
    }
}