package io.github.namhyungu.keymap.util

import org.json.JSONArray
import org.json.JSONObject

fun String.toJsonObject(): JSONObject = JSONObject(this)

fun JSONObject.array(key: String): JSONArray = getJSONArray(key)

fun JSONObject.string(key: String): String = getString(key)

fun JSONObject.`object`(key: String, block: JSONObject.() -> Unit): JSONObject =
    getJSONObject(key).apply(block)