package br.com.redcode.pedrofsn.passwordmyballs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.widget.addTextChangedListener


/*
    CREATED BY @PEDROFSN
*/

class PasswordMyBalls : FrameLayout {

    private lateinit var editText: EditText
    private lateinit var linearLayout: LinearLayout
    private lateinit var typedArray: TypedArray

    private var listenerFillPassword: ((String) -> Unit)? = null
    private val balls = arrayListOf<ImageView>()

    private val filled by lazy {
        return@lazy try {
            typedArray.getDrawableOrThrow(R.styleable.PasswordMyBalls_drawableFilled)
        } catch (e: Exception) {
            ContextCompat.getDrawable(context, R.drawable.ball_with_data)
        }
    }

    private val empty by lazy {
        return@lazy try {
            typedArray.getDrawableOrThrow(R.styleable.PasswordMyBalls_drawableEmpty)
        } catch (e: Exception) {
            ContextCompat.getDrawable(context, R.drawable.ball_without_data)
        }
    }

    private val count by lazy { typedArray.getInt(R.styleable.PasswordMyBalls_balls, 6) }
    private val marginRight by lazy {
        typedArray.getInt(
            R.styleable.PasswordMyBalls_marginRight,
            25
        )
    }
    private val size by lazy { typedArray.getInt(R.styleable.PasswordMyBalls_size, 50) }

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
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordMyBalls)

        watchText()
        setupInput()
        setMaxInput(count)
        setupBalls()
        guideCursor()
        handleEnterKeyboard()
        requestFocusInEditText()

        typedArray.recycle()
    }

    private fun setupInput() {
        val index = typedArray.getInt(
            R.styleable.PasswordMyBalls_type,
            BallType.ONLY_NUMBERS.type
        )

        when (BallType.values()[index]) {
            BallType.ONLY_NUMBERS -> {
                val numberDigits = context.getString(R.string.number_digits)
                editText.inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD
                editText.keyListener = DigitsKeyListener.getInstance(numberDigits)
            }
            BallType.ANY_TEXT -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    private fun refreshBalls() {
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
            handlePassword(input, count)
        }
    }

    private fun setupBalls() {
        filled // this call it's just to instance by lazyload

        repeat(count) {
            ImageView(context).apply {
                with(size) {
                    minimumWidth = this
                    minimumHeight = this
                }

                background = empty
                layoutParams = getMargin()
            }.run {
                linearLayout.addView(this)
                balls.add(this)
            }
        }.also {
            linearLayout.invalidate()
        }
    }

    private fun getMargin(right: Int = marginRight): ViewGroup.LayoutParams {
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, right, 0)
        return layoutParams
    }

    private fun setMaxInput(count: Int) {
        editText.filters += InputFilter.LengthFilter(count)
    }

    fun onPasswordInputted(callback: (String) -> Unit) {
        this.listenerFillPassword = callback
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun guideCursor() {
        editText.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                editText.setSelection(editText.text.length)
                requestFocusInEditText()
            }
            return@setOnTouchListener true
        }

        // tricky
        linearLayout.setOnClickListener { requestFocusInEditText() }
    }

    fun clear() {
        editText.setText("")
        requestFocusInEditText()
    }

    private fun handlePassword(input: String = getPassword(), length: Int = input.length) {
        if (count == length) {
            listenerFillPassword?.invoke(input)
        }
    }

    fun requestFocusInEditText() {
        editText.requestFocus()
        getInputManager()?.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    private fun EditText.handleEnterKeyboard(function: (EditText) -> Unit) =
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                function(this)
            }
            return@setOnEditorActionListener true
        }


    private fun getInputManager(): InputMethodManager? {
        return (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
    }

    fun getPassword() = editText.getString()
    fun hideKeyboard() = editText.hideKeyboard()
    private fun watchText() = editText.addTextChangedListener { refreshBalls() }
    private fun handleEnterKeyboard() = editText.handleEnterKeyboard { handlePassword() }
    private fun EditText.getString() = text.toString().trim()
    private fun EditText.hideKeyboard() = getInputManager()?.hideSoftInputFromWindow(windowToken, 0)

}