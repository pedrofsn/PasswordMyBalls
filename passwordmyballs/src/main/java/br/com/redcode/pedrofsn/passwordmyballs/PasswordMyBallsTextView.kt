package br.com.redcode.pedrofsn.passwordmyballs

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import android.R.string.no
import android.R.attr.name
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



/*
    CREATED BY @PEDROFSN
*/

class PasswordMyBallsTextView : LinearLayout {

    private lateinit var editText: EditText

    private var listenerFillPassword: ((String) -> Unit)? = null
    private val EMPTY_STRING = ""
    private var lastPassword = EMPTY_STRING
    private var count = 6

    fun onPasswordInputted(callback: (String) -> Unit) {
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context, attrs: AttributeSet?) {
        // inflate xml
        val view = inflate(context, R.layout.ui_my_balls_textview, this)

        // Views
        editText = view.findViewById(R.id.editText)
        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)

        // Get attributes from XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordMyBalls)
        count = typedArray.getInt(R.styleable.PasswordMyBalls_balls, 6)

        val balls = arrayListOf<ImageView>()

        val watcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = editText.getString()
                val length = input.length

                balls.forEachIndexed { index, imageView ->

                    if (index + 1 <= length) {
                        imageView.tag = true
                        imageView.background = filled
                    } else {
                        imageView.tag = false
                        imageView.background = empty
                    }
                }

                if (length == count) {
                    handlePassword()
                }
            }
        }

        editText.addTextChangedListener(watcher)
        editText.filters += InputFilter.LengthFilter(count)

        for (index in 0 until count) {
            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(0, 0, 25, 0)

            val imageView = ImageView(context)
            imageView.minimumWidth = 50
            imageView.minimumHeight = 50
            imageView.background = empty
            imageView.tag = false

             imageView.layoutParams = layoutParams

            linearLayout.addView(imageView)
            balls.add(imageView)
        }
        linearLayout.invalidate()

        requestFocusInFirstEditText()

        editText.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                editText.setSelection(editText.text.length)
                requestFocusInFirstEditText()
            }
            return@setOnTouchListener true
        }

        linearLayout.setOnClickListener { requestFocusInFirstEditText() }

        typedArray.recycle()
    }

    fun clear() {
//        editText0.setText(EMPTY_STRING)
//        editText1.setText(EMPTY_STRING)
//        editText2.setText(EMPTY_STRING)
//        editText3.setText(EMPTY_STRING)
//        editText4.setText(EMPTY_STRING)
//        editText5.setText(EMPTY_STRING)

        editText.setText(EMPTY_STRING)

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
//        val input0 = editText0.getString()
//        val input1 = editText1.getString()
//        val input2 = editText2.getString()
//        val input3 = editText3.getString()
//        val input4 = editText4.getString()
//        val input5 = editText5.getString()

//        lastPassword = input0 + input1 + input2 + input3 + input4 + input5
        lastPassword = editText.getString()

        listenerFillPassword?.invoke(lastPassword)
    }

    fun getPassword(): String = lastPassword

    fun hideKeyboard() {
//        editText0.hideKeyboard()
//        editText1.hideKeyboard()
//        editText2.hideKeyboard()
//        editText3.hideKeyboard()
//        editText4.hideKeyboard()
//        editText5.hideKeyboard()

        editText.hideKeyboard()
    }

    fun requestFocusInFirstEditText() {
        editText.requestFocus()

        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
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