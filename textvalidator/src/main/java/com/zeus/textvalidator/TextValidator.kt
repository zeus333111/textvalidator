package com.zeus.textvalidator

import android.content.Context
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zeus.textvalidator.TextValidator.Companion.CURP
import com.zeus.textvalidator.TextValidator.Companion.DATE
import com.zeus.textvalidator.TextValidator.Companion.EMAIL
import com.zeus.textvalidator.TextValidator.Companion.LAST_NAME
import com.zeus.textvalidator.TextValidator.Companion.MIDDLE_NAME
import com.zeus.textvalidator.TextValidator.Companion.NAME
import com.zeus.textvalidator.TextValidator.Companion.NUMERIC
import com.zeus.textvalidator.TextValidator.Companion.PASSWORD
import com.zeus.textvalidator.TextValidator.Companion.RFC
import com.zeus.textvalidator.TextValidator.Companion.TEXT

fun TextInputEditText.addValidator(type: Int, validator: TextValidator, required: Boolean = true, size: Int? = null) {
    validator.addTiet(this, required)
    val regexCurp = Regex("^[A-Z]{4}[0-9]{6}[A-Z]{6}[A-F0-9]{2}\$")
    val regexRfc = Regex("^[A-Z]{4}[0-9]{6}([A-Z0-9]{3})?\$")
    val regexName = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+\$")
    val regexMiddlename = Regex("^([a-zA-ZáéíóúÁÉÍÓÚñÑ]+\\s?)+\$")
    val regexLastName = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+\$")
    val regexText = Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$")
    val regexEmail = Regex("^[\\w.]+@(\\w+\\.\\w+){1,2}\$")
    val regexNumber = Regex("""^[0-9]${if (size == null) "+" else "{$size}"}.?[0-9]*${'$'}""")
    val regexDate = Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}\$")
    val regexPassword = Regex("^[\\w\\D]{6,}\$")

    addTextChangedListener { text ->
        val til = parent.parent as TextInputLayout
        if (text.toString().isBlank() && required) {
            til.error = context.getString(R.string.validator_empty_field)
            return@addTextChangedListener
        } else if (text.toString().isBlank() && !required) {
            til.isErrorEnabled = false
            til.error = null
            return@addTextChangedListener
        }
        when (type) {
            CURP -> {
                if (!regexCurp.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_curp)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            RFC -> {
                if (!regexRfc.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_rfc)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            NAME -> {
                if (!regexName.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_name)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            MIDDLE_NAME -> {
                if (!regexMiddlename.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_characters)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            LAST_NAME -> {
                if (!regexLastName.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_characters)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            TEXT -> {
                if (!regexText.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_text)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            EMAIL -> {
                if (!regexEmail.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_email)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            DATE -> {
                if (!regexDate.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_empty_field)
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            NUMERIC -> {
                if (!regexNumber.matches(text.toString())) {
                    til.error = if (size == null) {
                        context.getString(R.string.validator_numeric)
                    } else {
                        context.resources.getQuantityString(R.plurals.validator_numeric_plural, size, size)
                    }
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
            PASSWORD -> {
                if (!regexPassword.matches(text.toString())) {
                    til.error = context.getString(R.string.validator_passwrod)
                    til.errorIconDrawable = null
                } else {
                    til.error = null
                    til.isErrorEnabled = false
                }
            }
        }
    }
}

fun TextInputEditText.addConfirmPasswordValidator(tietPassword: TextInputEditText, validator: TextValidator) {
    validator.addTiet(this, true, true)
    val til = parent.parent as TextInputLayout
    addTextChangedListener { textConfirm ->
        tietPassword.text?.let { textPass ->
            if (textPass.toString() != textConfirm.toString()) {
                til.error = context.getString(R.string.validator_confirm_password)
                til.errorIconDrawable = null
            } else {
                til.error = null
                til.isErrorEnabled = false
            }
        }
    }
}

fun Spinner.addValidator(validator: TextValidator, required: Boolean = true) {
    if (required) {
        validator.addSpinner(this)
    }
}

fun Spinner.setError() {
    (selectedView as TextView).error = context.getString(R.string.validator_spinner)
}

fun AppCompatButton.setValidatedClickListener(textValidator: TextValidator, onValid: () -> Unit) {
    setOnClickListener {
        textValidator.validateFields(context) {
            onValid.invoke()
        }
    }
}

class TextValidator(val editable: Boolean = true) {
    private val tietCount = mutableMapOf<TextInputEditText, Boolean>()
    private var tietConfirmPassword: TextInputEditText? = null
    private val spinnerCount = ArrayList<Spinner>()

    fun addTiet(tiet: TextInputEditText, required: Boolean, password: Boolean = false) {
        if (tiet.isEnabled) {
            tiet.isEnabled = editable
        }
        if (password) {
            tietConfirmPassword = tiet
        } else {
            tietCount.put(tiet, required)
        }
    }

    fun addSpinner(spinner: Spinner) {
        spinner.isEnabled = editable
        spinnerCount.add(spinner)
    }

    fun validateFields(context: Context, onSuccess: () -> Unit) {
        var errors = 0
        tietCount.forEach {
            val (tiet, required) = it
            val til = tiet.parent.parent as TextInputLayout
            if (til.error != null || (tiet.text.isNullOrBlank() && required)) {
                til.error = til.context.getString(R.string.validator_empty_field)
                til.requestFocus()
                errors++
            }
        }
        spinnerCount.forEach {
            if (it.selectedItemPosition == 0) {
                it.setError()
                errors++
            }
        }
        tietConfirmPassword?.let {
            if (tietConfirmPassword?.error != null) {
                tietConfirmPassword?.error = context.getString(R.string.validator_confirm_password)
                tietConfirmPassword?.requestFocus()
                errors++
            }
        }

        if (errors == 0) {
            onSuccess.invoke()
        } else {
            Toast.makeText(
                context,
                context.resources.getQuantityString(R.plurals.validator_errors, errors, errors),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        const val CURP = 1
        const val RFC = 2
        const val NAME = 3
        const val TEXT = 4
        const val EMAIL = 5
        const val MIDDLE_NAME = 6
        const val DATE = 7
        const val LAST_NAME = 8
        const val NUMERIC = 9
        const val PASSWORD = 10
    }
}