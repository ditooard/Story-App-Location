package com.bangkit2024.aplikasistoryapplocation.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit2024.aplikasistoryapplocation.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr), View.OnTouchListener {

    private var isVisible = false
    private var visibleDrawable: Drawable? = null
    private var hideDrawable: Drawable? = null

    init {
        initDrawables()
        setOnTouchListener(this)
        initTextWatcher()
    }

    private fun initDrawables() {
        visibleDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_hide)
        hideDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_visible)
        updatePasswordToggleDrawable()
    }

    private fun initTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8) {
                    setError(resources.getString(R.string.password_char), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun updatePasswordToggleDrawable() {
        val drawable = if (isVisible) hideDrawable else visibleDrawable
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val passwordButtonEnd = (width - paddingRight - compoundDrawables[2].bounds.width())
            if (event.x > passwordButtonEnd && event.action == MotionEvent.ACTION_UP) {
                togglePasswordVisibility()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        isVisible = !isVisible
        updatePasswordToggleDrawable()

        inputType = if (isVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

}
