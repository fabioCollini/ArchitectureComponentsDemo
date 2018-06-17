package it.codingjam.github.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import it.codingjam.github.binding.visibleOrGone
import it.codingjam.github.viewlib.R
import it.codingjam.github.vo.Lce

class LceContainer<T> : FrameLayout {

    private var loading: View
    private var error: View
    private var errorMessage: TextView
    private var retry: View

    var lce: Lce<T>? = null
        set(value) {
            when (value) {
                is Lce.Loading -> children.forEach { it.visibleOrGone = it == loading }
                is Lce.Success -> {
                    children.forEach { it.visibleOrGone = it != loading && it != error }
                    updateListener?.invoke(value.data)
                }
                is Lce.Error -> {
                    children.forEach { it.visibleOrGone = it == error }
                    errorMessage.text = value.message
                }
            }
        }

    private var updateListener: ((T) -> Unit)? = null

    fun setRetryAction(retryAction: Runnable?) {
        retry.setOnClickListener { retryAction?.run() }
    }

    fun setUpdateListener(listener: ((T) -> Unit)) {
        updateListener = listener
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, retryAction: () -> Unit) : super(context) {
        retry.setOnClickListener { retryAction() }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    init {
        val inflater = LayoutInflater.from(context)
        loading = inflater.inflate(R.layout.loading, this, false).also { addView(it) }
        error = inflater.inflate(R.layout.error, this, false).also { addView(it) }
        errorMessage = error.findViewById(R.id.error_message)
        retry = error.findViewById(R.id.retry)
    }
}