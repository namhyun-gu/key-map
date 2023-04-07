package io.github.namhyungu.keymap.ui.splash

sealed interface LoginEvent {

    object NotLogged : LoginEvent

    object Logged : LoginEvent

}