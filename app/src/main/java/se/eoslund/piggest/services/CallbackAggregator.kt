package se.eoslund.piggest.services

import com.skydoves.progressview.ProgressView
import se.eoslund.piggest.R
import se.eoslund.piggest.controller.App

class CallbackAggregator(val count: Int) {
    var finalCallback: (() -> Unit)? = null
    var calls = 0

    fun increment() {
        if (++calls == count)
            finalCallback?.invoke()
    }
}