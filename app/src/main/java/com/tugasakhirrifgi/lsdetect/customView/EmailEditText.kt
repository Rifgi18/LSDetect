package com.tugasakhirrifgi.lsdetect.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.tugasakhirrifgi.lsdetect.R

class EmailEditText: AppCompatEditText {

    private lateinit var emailIcon: Drawable
    private lateinit var clearIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init(){
        emailIcon = ContextCompat.getDrawable(context, R.drawable.custom_input_email) as Drawable
        //clearIcon = ContextCompat.getDrawable(context, R.drawable.baseline_clear_24) as Drawable

        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        compoundDrawablePadding = 16
        setHint(R.string.hintEmail)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        }
        setDrawable(emailIcon)


        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /*
                if (!s.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    error = context.getString(R.string.errorEmail)

                 */
            }

            override fun afterTextChanged(s: Editable) {
                //setDrawable(start = emailIcon, end = clearIcon)
            }

        })
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}