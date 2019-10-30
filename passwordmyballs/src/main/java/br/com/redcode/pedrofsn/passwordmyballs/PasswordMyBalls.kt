package br.com.redcode.pedrofsn.passwordmyballs

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

/*
    CREATED BY @PEDROFSN
*/

class PasswordMyBalls : LinearLayout {

    private lateinit var editText0: EditText
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var editText3: EditText
    private lateinit var editText4: EditText
    private lateinit var editText5: EditText

    private var listenerFillPassword: ((String) -> Unit)? = null
    private val EMPTY_STRING = ""
    private var lastPassword = EMPTY_STRING
    private var count = 6

    fun onPasswordInputted(callback : (String) -> Unit) {
        this.listenerFillPassword = callback
    }

    private val filled by lazy { ContextCompat.getDrawable(context, R.drawable.ball_with_data) }
    private val empty by lazy { ContextCompat.getDrawable(context, R.drawable.ball_without_data) }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        // inflate xml
        val view = inflate(context, R.layout.ui_my_balls, this)

        // Views
        editText0 = view.findViewById(R.id.editText0)
        editText1 = view.findViewById(R.id.editText1)
        editText2 = view.findViewById(R.id.editText2)
        editText3 = view.findViewById(R.id.editText3)
        editText4 = view.findViewById(R.id.editText4)
        editText5 = view.findViewById(R.id.editText5)

        flowTypping(editText0, editText1)
        flowTypping(editText1, editText2)
        flowTypping(editText2, editText3)
        flowTypping(editText3, editText4)
        flowTypping(editText4, editText5)
        flowToHandleSuccess(editText5)

        // Get attributes from XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordMyBalls)
        count = typedArray.getInt(R.styleable.PasswordMyBalls_balls, 6)

        typedArray.recycle()
    }

    fun clear() {
        editText0.setText(EMPTY_STRING)
        editText1.setText(EMPTY_STRING)
        editText2.setText(EMPTY_STRING)
        editText3.setText(EMPTY_STRING)
        editText4.setText(EMPTY_STRING)
        editText5.setText(EMPTY_STRING)

        requestFocusInFirstEditText()
    }

    private fun flowToHandleSuccess(last: EditText) {
        val watcherFinishedInputCode = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                last.background = when {
                    s.isNullOrBlank().not() -> {
                        handlePassword()
                        filled
                    }
                    else -> empty
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        last.addTextChangedListener(watcherFinishedInputCode)
        last.handleEnterKeyboard { handlePassword() }
    }

    private fun flowTypping(from: EditText, to: EditText) {
        val watcherForward = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                from.background = when {
                    s.isNullOrBlank().not() -> {
                        to.requestFocus()
                        filled
                    }
                    else -> empty
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        val onBackSpace = { s: Editable? ->
            to.background = when {
                s.isNullOrBlank() -> {
                    from.requestFocus()

                    empty
                }
                else -> filled
            }
        }

        val watcherBackward = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onBackSpace(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        from.addTextChangedListener(watcherForward)
        to.addTextChangedListener(watcherBackward)

    }

    private fun handlePassword() {
        val input0 = editText0.getString()
        val input1 = editText1.getString()
        val input2 = editText2.getString()
        val input3 = editText3.getString()
        val input4 = editText4.getString()
        val input5 = editText5.getString()

        lastPassword = input0 + input1 + input2 + input3 + input4 + input5

        listenerFillPassword?.invoke(lastPassword)
    }

    fun getPassword(): String = lastPassword

    fun hideKeyboard() {
        editText0.hideKeyboard()
        editText1.hideKeyboard()
        editText2.hideKeyboard()
        editText3.hideKeyboard()
        editText4.hideKeyboard()
        editText5.hideKeyboard()
    }

    fun requestFocusInFirstEditText() {
        editText0.requestFocus()

        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.showSoftInput(editText0, InputMethodManager.SHOW_FORCED)
    }

    private fun EditText.handleDeleteKeyboard(function: (EditText) -> Unit) {
        setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                val count = editableText.toString().count()
                if (count == 0) {
                    function.invoke(view as EditText)
                }
            }
            return@setOnKeyListener false
        }
    }

    private fun EditText.getString() = text.toString().trim()

    private fun EditText.handleEnterKeyboard(function: (EditText) -> Unit) =
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                function(this)
            }
            return@setOnEditorActionListener true
        }

    private fun EditText.hideKeyboard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(windowToken, 0)
    }

}