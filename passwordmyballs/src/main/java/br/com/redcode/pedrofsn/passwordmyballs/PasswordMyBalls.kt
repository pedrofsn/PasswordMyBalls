package br.com.redcode.pedrofsn.passwordmyballs

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener


/*
    CREATED BY @PEDROFSN
*/

class PasswordMyBalls : LinearLayout {

    private lateinit var editText: EditText
    private lateinit var linearLayout: LinearLayout

    private var listenerFillPassword: ((String) -> Unit)? = null
    private val balls = arrayListOf<ImageView>()
    private val defaultCount = 6
    private var count: Int = defaultCount

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

    private fun initView(context: Context, attrs: AttributeSet?) {
        // Inflate XML
        val view = inflate(context, R.layout.ui_my_balls, this)

        // Initialize views
        editText = view.findViewById(R.id.editText)
        linearLayout = view.findViewById(R.id.linearLayout)

        // Get attributes from XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordMyBalls)
        count = typedArray.getInt(R.styleable.PasswordMyBalls_balls, defaultCount)

        editText.addTextChangedListener { onTextChanged() }

        setMaxInput(count)
        setupBalls()
        requestFocusInFirstEditText()
        guideCursor()
        handleEnterKeyboard()

        typedArray.recycle()
    }

    private fun onTextChanged() {
        val input = editText.getString()
        val length = input.length

        // Eg.: 1 character = 0 index (balls)
        val countNormalized = length - 1

        balls.forEachIndexed { index, imageView ->
            val newStatus = when {
                index <= countNormalized -> filled
                else -> empty
            }

            imageView.background = newStatus
        }

        if (length == count) {
            handlePassword()
        }
    }

    private fun handleEnterKeyboard() = editText.handleEnterKeyboard { handlePassword() }

    private fun setupBalls() {
        for (index in 0 until count) {
            val imageView = ImageView(context)
            imageView.minimumWidth = 50
            imageView.minimumHeight = 50
            imageView.background = empty

            imageView.layoutParams = getMargin()

            linearLayout.addView(imageView)
            balls.add(imageView)
        }

        linearLayout.invalidate()
    }

    private fun getMargin(right: Int = 25): ViewGroup.LayoutParams {
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, right, 0)
        return layoutParams
    }

    private fun setMaxInput(count: Int) {
        editText.filters += InputFilter.LengthFilter(count)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun guideCursor() {
        editText.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                editText.setSelection(editText.text.length)
                requestFocusInFirstEditText()
            }
            return@setOnTouchListener true
        }

        // tricky
        linearLayout.setOnClickListener { requestFocusInFirstEditText() }
    }

    fun clear() {
        editText.setText("")
        requestFocusInFirstEditText()
    }

    private fun handlePassword() {
        val input = editText.getString()
        if (count == input.length) {
            listenerFillPassword?.invoke(input)
        }
    }

    fun hideKeyboard() = editText.hideKeyboard()

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