# TextValidator

TextValidator is a library written in kotlin to facilitate the validation of text fields.

## Installation

### Gradle

Add this to the root build.gradle at the end of repositories (**WARNING:** Make sure you add this under **allprojects** not under buildscript):
```groovy
allprojects {
        repositories {
                ...
                maven { url 'https://jitpack.io' }
        }
}
```

Add the dependency to the project build.gradle:
```Groovy
dependencies {
        implementation 'com.github.zeus333111:textvalidator:1.0.7'
}
```

## Usage
it is completely necessary to use TextInputLayout and TextinputEdittext

```xml
<com.google.android.material.textfield.TextInputLayout
            android:id="@+id/til_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/tiet_text"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

        </com.google.android.material.textfield.TextInputLayout>
```

First instantiate an object TextValidator

```kotlin
val textValidator = TextValidator()
```

Then add validator and specify its type.
Also you can validate spinner and passwords.

```kotlin
tietText.addValidator(TextValidator.NAME, textValidator)
spinner.addValidator(textValidator)
tietPassword.addValidator(TextValidator.PASSWORD, textValidator)
tietConfirmPassword.addConfirmPasswordValidator(tietPassword, textValidator)
```

Finally setValidateClickListener to a button and pass the textValidator. When everything is valid then it will execute the block.

```kotlin
 btnValidate.setValidatedClickListener(textValidator) {
            //when all fields are valid then run this
        }
```

## Validations Types

- **CURP**
- **RFC**
- **NAME** accept only one word
- **MIDDLE_NAME** accept 0 or more words
- **LAST_NAME** accept one or more words
- **TEXT** accept any text and number without especial characters
- **FREE_TEXT** accept any text
- **EMAIL** accept email pattern aaaa@bbbb.ccc
- **DATE** accept pattern yyyy-mm-dd
- **NUMERIC** accept any number 111.111
- **NUMERIC** accept number with min length 6
- **PASSWORD** accept any character length up to 6

## Custom validation
It is possible to add custom validation as shown next:

```kotlin
textValidator.addValidator("CustomRegex", Regex("^Hello world$"), R.string.no_hello_world)
```

Where **R.string.no_hello_world** is error string resource id

## Error resources

TextValidator has the next error texts in Spanish. It is posible to override it adding **tools:override="true"** to your resource strings xml file

```xml
<string name="validator_curp">CURP no Válido</string>
    <string name="validator_rfc">RFC no Válido</string>
    <string name="validator_characters">Válido sólo caracteres</string>
    <string name="validator_name">Válido sólo caracteres</string>
    <string name="validator_empty_field">No deje vacíos</string>
    <string name="validator_date">Formato de fecha incorrecto</string>
    <string name="validator_text">Usar sólo letras y números</string>
    <string name="validator_email">Correo electronico no válido</string>
    <string name="validator_spinner">Seleccione una opcion</string>
    <string name="validator_numeric">Válido sólo números</string>
    <string name="validator_passwrod">Debe contener al menos 6 caracteres</string>
    <string name="validator_confirm_password">La contraseña debe ser igual</string>
    <string name="validator_empty"> </string>
    <string name="validator_no_regex">No regex registered</string>
    <plurals name="validator_numeric_plural">
        <item quantity="one">Número de %d digito</item>
        <item quantity="other">Número de %d digitos</item>
    </plurals>
    <plurals name="validator_errors">
        <item quantity="one">Se encontró un error</item>
        <item quantity="other">Se encontraron %d errores</item>
    </plurals>
```
