package io.trubi.skyline.dev

import android.util.Log
import io.trubi.skyline.BuildConfig

/**
 * Created by Fitz on 2018/3/2.
 *
 */

object Sogger {

    private const val TAG = "Skyline-log"
    private val OPEN_LOG  = BuildConfig.DEBUG

    fun i(message: String) {
        i(TAG, message)
    }

    fun d(message: String) {
        d(TAG, message)
    }

    fun w(message: String) {
        w(TAG, message)
    }

    fun v(message: String) {
        v(TAG, message)
    }

    fun e(message: String) {
        e(TAG, message)
    }

    fun i(tag: String, message: String) {
        if (OPEN_LOG) {
            Log.i("$TAG: $tag", message)
        }
    }

    fun d(tag: String, message: String) {
        if (OPEN_LOG) {
            Log.d("$TAG: $tag", message)
        }
    }

    fun w(tag: String, message: String) {
        if (OPEN_LOG) {
            Log.w("$TAG: $tag", message)
        }
    }

    fun v(tag: String, message: String) {
        if (OPEN_LOG) {
            Log.v("$TAG: $tag", message)
        }
    }

    fun e(tag: String, message: String) {
        if (OPEN_LOG) {
            Log.e("$TAG: $tag", message)
        }
    }

}
