package com.zeus.textvalidatortestapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zeus.textvalidator.TextValidator2
import com.zeus.textvalidator.TextValidator2.Companion.addConfirmPasswordValidator2
import com.zeus.textvalidator.TextValidator2.Companion.addValidator2
import com.zeus.textvalidator.TextValidator2.Companion.setValidatedClickListener2
import com.zeus.textvalidatortestapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val textValidator = TextValidator2() //TextValidator(false) makes all registered fields no editable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpValidator()
        setUpListeners()
    }

    private fun setUpValidator() = with(binding) {
        tietCurp.addValidator2(TextValidator2.CURP, textValidator)
        tietRfc.addValidator2(TextValidator2.RFC, textValidator)
        tietName.addValidator2(TextValidator2.NAME, textValidator) //accept only one word
        tietMiddleName.addValidator2(TextValidator2.MIDDLE_NAME, textValidator) //accept 0 or more words
        tietLastName.addValidator2(TextValidator2.LAST_NAME, textValidator) //accept one or more words
        tietText.addValidator2(TextValidator2.TEXT, textValidator) //accept any text and number without especial characters
        tietFreeText.addValidator2(TextValidator2.FREE_TEXT, textValidator) //accept any text
        tietNoRequired.addValidator2(TextValidator2.TEXT, textValidator, false) //it is validate when is not empty (no empty error)
        tietEmail.addValidator2(TextValidator2.EMAIL, textValidator) //accept email pattern aaaa@bbbb.ccc
        tietDate.addValidator2(TextValidator2.DATE, textValidator) //accept pattern dd-mm-yyyy
        tietNumeric.addValidator2(TextValidator2.NUMERIC, textValidator) //accept any number 111.111
        tietNumeric2.addValidator2(TextValidator2.NUMERIC, textValidator, size = 6) //accept number with min length 6
        tietPassword.addValidator2(TextValidator2.PASSWORD, textValidator) //accept any character length up to 6
        tietConfirmPassword.addConfirmPasswordValidator2(tietPassword, textValidator) //compare password fields

        textValidator.addValidator("CustomRegex", Regex("^Hello world$"), R.string.no_hello_world)
        tietCustomRegex.addValidator2("CustomRegex", textValidator) //accept custom regex

        tietErro.addValidator2("unknown", textValidator, false) //unknown regex
        sOptions.addValidator2(textValidator) //accept any non first option
    }

    private fun setUpListeners() = with(binding) {
        btnValidate.setValidatedClickListener2(textValidator) {
            Toast.makeText(this@MainActivity, "Validated and ok", Toast.LENGTH_LONG).show()
        }
    }
}