package com.zeus.textvalidatortestapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zeus.textvalidator.TextValidator
import com.zeus.textvalidator.TextValidator.Companion.addConfirmPasswordValidator
import com.zeus.textvalidator.TextValidator.Companion.addValidator
import com.zeus.textvalidator.TextValidator.Companion.setValidatedClickListener
import com.zeus.textvalidatortestapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val textValidator = TextValidator() //TextValidator(false) makes all registered fields no editable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpValidator()
        setUpListeners()
    }

    private fun setUpValidator() = with(binding) {
        tietCurp.addValidator(TextValidator.CURP, textValidator)
        tietRfc.addValidator(TextValidator.RFC, textValidator)
        tietName.addValidator(TextValidator.NAME, textValidator) //accept only one word
        tietMiddleName.addValidator(TextValidator.MIDDLE_NAME, textValidator) //accept 0 or more words
        tietLastName.addValidator(TextValidator.LAST_NAME, textValidator) //accept one or more words
        tietText.addValidator(TextValidator.TEXT, textValidator) //accept any text and number without especial characters
        tietFreeText.addValidator(TextValidator.FREE_TEXT, textValidator) //accept any text
        tietNoRequired.addValidator(TextValidator.TEXT, textValidator, false) //it is validate when is not empty (no empty error)
        tietEmail.addValidator(TextValidator.EMAIL, textValidator) //accept email pattern aaaa@bbbb.ccc
        tietDate.addValidator(TextValidator.DATE, textValidator) //accept pattern yyyy-mm-dd
        tietNumeric.addValidator(TextValidator.NUMERIC, textValidator) //accept any number 111.111
        tietNumeric2.addValidator(TextValidator.NUMERIC, textValidator, size = 6) //accept number with min length 6
        tietPassword.addValidator(TextValidator.PASSWORD, textValidator) //accept any character length up to 6
        tietConfirmPassword.addConfirmPasswordValidator(tietPassword, textValidator) //compare password fields

        textValidator.addValidator("CustomRegex", Regex("^Hello world$"), R.string.no_hello_world)
        tietCustomRegex.addValidator("CustomRegex", textValidator) //accept custom regex

        tietErro.addValidator("unknown", textValidator, false) //unknown regex
        sOptions.addValidator(textValidator) //accept any non first option
    }

    private fun setUpListeners() = with(binding) {
        btnValidate.setValidatedClickListener(textValidator) {
            Toast.makeText(this@MainActivity, "Validated and ok", Toast.LENGTH_LONG).show()
        }
    }
}