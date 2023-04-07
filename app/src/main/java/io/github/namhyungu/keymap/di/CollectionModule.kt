package io.github.namhyungu.keymap.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class CollectionModule {

    @KeyCollection
    @Provides
    fun provideKeyCollection(): CollectionReference = Firebase.firestore.collection("key")

}