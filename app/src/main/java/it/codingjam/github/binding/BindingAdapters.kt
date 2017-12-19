/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.codingjam.github.binding

import android.content.Context
import android.databinding.BindingAdapter
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import it.codingjam.github.R
import it.codingjam.github.vo.Resource

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).into(imageView)
}

@BindingAdapter("onSearch")
fun onSearch(input: EditText, listener: OnTextListener?) {
    input.setOnEditorActionListener({ v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (listener != null) {
                val query = input.text.toString()
                dismissKeyboard(v)
                listener.onChanged(query)
            }
            true
        } else {
            false
        }
    })
}

@BindingAdapter("onKeyDown")
fun onKeyDown(input: EditText, listener: OnTextListener?) {
    input.setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            if (listener != null) {
                val query = input.text.toString()
                dismissKeyboard(input)
                listener.onChanged(query)
            }
            true
        } else {
            false
        }
    }
}

interface OnTextListener {
    fun onChanged(s: String)
}

private fun dismissKeyboard(view: TextView) {
    val windowToken = view.windowToken
    val context = view.context
    if (context != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

@BindingAdapter("errorMessage")
fun bindErrorMessage(t: TextView, resource: Resource<*>?) {
    t.text = when {
        resource !is Resource.Error -> ""
        resource.message.isBlank() -> t.resources.getString(R.string.unknown_error)
        else -> resource.message
    }
}

@BindingAdapter("visibleWhileLoading")
fun bindVisibleWhileLoading(t: View, resource: Resource<*>?) {
    t.visibility = if (resource is Resource.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOnError")
fun bindVisibleOnError(t: View, resource: Resource<*>?) {
    t.visibility = if (resource is Resource.Error) View.VISIBLE else View.GONE
}
