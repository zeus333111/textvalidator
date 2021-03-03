package com.zeus.textvalidator

import android.content.Context
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextValidator2 (val editable: Boolean = true) {

    private val tietCount = mutableMapOf<TextInputEditText, Boolean>()
    private var tietConfirmPassword: TextInputEditText? = null
    private val spinnerCount = ArrayList<Spinner>()

    private val validators: MutableMap<String, Pair<Regex, Int>> = mutableMapOf(
        CURP to Pair(Regex("^[A-Z]{4}[0-9]{6}[A-Z]{6}[A-F0-9]{2}\$"), R.string.validator_curp),
        RFC to Pair(Regex("^[A-Z]{4}[0-9]{6}([A-Z0-9]{3})?\$"), R.string.validator_rfc),
        NAME to Pair(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+\$"), R.string.validator_name),
        TEXT to Pair(Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$"), R.string.validator_text),
        EMAIL to Pair(Regex("^[\\w.]+@(\\w+\\.\\w+){1,2}\$"), R.string.validator_email),
        MIDDLE_NAME to Pair(Regex("^([a-zA-ZáéíóúÁÉÍÓÚñÑ]+\\s?)+\$"), R.string.validator_characters),
        DATE to Pair(Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}\$"), R.string.validator_date),
        LAST_NAME to Pair(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+\$"), R.string.validator_characters),
        PASSWORD to Pair(Regex("^[\\S]{6,}\$"), R.string.validator_passwrod),
        FREE_TEXT to Pair(Regex("(.*), (.*)"), R.string.validator_empty),
        ERROR to Pair(Regex("^\$"), R.string.validator_no_regex)
    )

    private fun getValidator(name: String): Pair<Regex, Int> {
        return validators[name]?: validators[ERROR]!!
    }

    fun addValidator( name: String, regex: Regex, errorStringResId: Int) {
        validators[name] = Pair(regex, errorStringResId)
    }

    private fun addTiet(tiet: TextInputEditText, required: Boolean, password: Boolean = false) {
        if (tiet.isEnabled) {
            tiet.isEnabled = editable
        }
        if (password) {
            tietConfirmPassword = tiet
        } else {
            tietCount[tiet] = required
        }
    }

    private fun addSpinner(spinner: Spinner) {
        spinner.isEnabled = editable
        spinnerCount.add(spinner)
    }

    private fun validateFields(context: Context, onSuccess: () -> Unit) {
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
                it.setError2()
                errors++
            }
        }
        tietConfirmPassword?.let {
            if (tietConfirmPassword?.error != null || tietConfirmPassword?.text.isNullOrBlank()) {
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
        const val CURP = "CURP"
        const val RFC = "RFC"
        const val NAME = "NAME"
        const val TEXT = "TEXT"
        const val EMAIL = "EMAIL"
        const val MIDDLE_NAME = "MIDDLE_NAME"
        const val DATE = "DATE"
        const val LAST_NAME = "LAST_NAME"
        const val NUMERIC = "NUMERIC"
        const val PASSWORD = "PASSWORD"
        const val FREE_TEXT = "FREE_TEXT"
        const val ERROR = "ERROR"

        fun makeNumericRegex(size: Int? = null): Pair<Regex, Int> {
            val regex = Regex("""^[0-9]${if (size == null) "+" else "{$size}"}.?[0-9]*${'$'}""")
            val error = if (size == null) {
                R.string.validator_numeric
            } else {
                R.plurals.validator_numeric_plural
            }
            return Pair(regex, error)
        }

        fun TextInputEditText.addValidator2(type: String, validator: TextValidator2, required: Boolean = true, size: Int? = null) {
            validator.addTiet(this, required)

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

                val (regex, resId) = if (type == NUMERIC) {
                    makeNumericRegex(size)
                } else {
                    validator.getValidator(type)
                }

                when(type) {
                    NUMERIC -> {
                        if (!regex.matches(text.toString())) {
                            til.error = if (size == null) {
                                context.getString(resId)
                            } else {
                                context.resources.getQuantityString(resId, size, size)
                            }
                        } else {
                            til.error = null
                            til.isErrorEnabled = false
                        }
                    }
                    else -> {
                        if (!regex.matches(text.toString())) {
                            til.error = context.getString(resId)
                        } else {
                            til.error = null
                            til.isErrorEnabled = false
                        }
                    }
                }
            }
        }

        fun TextInputEditText.addConfirmPasswordValidator2(tietPassword: TextInputEditText, validator: TextValidator2) {
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

        fun Spinner.addValidator2(validator: TextValidator2, required: Boolean = true) {
            if (required) {
                validator.addSpinner(this)
            }
        }

        fun Spinner.setError2() {
            (selectedView as TextView).error = context.getString(R.string.validator_spinner)
        }

        fun AppCompatButton.setValidatedClickListener2(textValidator: TextValidator2, onValid: () -> Unit) {
            setOnClickListener {
                textValidator.validateFields(context) {
                    onValid.invoke()
                }
            }
        }
    }
}
