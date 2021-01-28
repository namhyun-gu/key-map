package io.github.namhyungu.keymap.util

import okhttp3.Response

class HttpException(response: Response) :
    RuntimeException("HTTP ${response.code}: ${response.message}")