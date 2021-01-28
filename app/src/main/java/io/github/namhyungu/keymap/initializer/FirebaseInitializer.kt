package io.github.namhyungu.keymap.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.FirebaseApp
import timber.log.Timber

class FirebaseInitializer : Initializer<FirebaseApp> {
    override fun create(context: Context): FirebaseApp {
        Timber.d("FirebaseInitializer is created.")
        return FirebaseApp.initializeApp(context)!!
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(TimberInitializer::class.java)
    }
}