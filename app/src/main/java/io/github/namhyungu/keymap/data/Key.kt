package io.github.namhyungu.keymap.data

import com.google.firebase.firestore.Exclude

data class Key(
    val id: String,
    val content: String,
    val description: String,
    val place: Place?,
) {
    constructor() : this("", "", "", null)

    @get:Exclude
    val isEmpty: Boolean
        get() = content.isEmpty()
}