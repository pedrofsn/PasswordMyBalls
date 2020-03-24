package br.com.pedrofsn.passwordmyballs.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwordMyBalls1.onPasswordInputted { password ->
            Toast.makeText(this, password, Toast.LENGTH_SHORT).show()
            passwordMyBalls1.hideKeyboard()
        }

        passwordMyBalls2.onPasswordInputted { password ->
            Toast.makeText(this, password, Toast.LENGTH_SHORT).show()
            passwordMyBalls2.hideKeyboard()
        }

        passwordMyBalls3.onPasswordInputted { password ->
            Toast.makeText(this, password, Toast.LENGTH_SHORT).show()
            passwordMyBalls3.hideKeyboard()
        }
    }
}
