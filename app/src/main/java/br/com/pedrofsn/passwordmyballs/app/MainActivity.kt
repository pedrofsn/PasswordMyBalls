package br.com.pedrofsn.passwordmyballs.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwordMyBalls.onPasswordInputted { password ->
            Toast.makeText(this, password, Toast.LENGTH_SHORT).show()
            passwordMyBalls.hideKeyboard()
        }
    }
}
