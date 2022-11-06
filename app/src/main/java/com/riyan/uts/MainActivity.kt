package com.riyan.uts

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.jakewharton.rxbinding2.widget.RxTextView
import com.riyan.uts.databinding.ActivityMainBinding
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        processedLogin()

        onAction()
    }

    private fun onAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                Intent(this@MainActivity, HomeActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun processedLogin() {
        binding.apply {
            val emailStream = RxTextView.textChanges(edtEmail)
                .skipInitialValue()
                .map { email ->
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }
            emailStream.subscribe {
                showEmailExistAlert(it)
            }

            val passwordStream = RxTextView.textChanges(edtPassword)
                .skipInitialValue()
                .map { password ->
                    password.length < 8
                }
            passwordStream.subscribe {
                showPasswordExistAlert(it)
            }

            val invalidFieldStream = Observable.combineLatest(
                emailStream,
                passwordStream
            ) {
                emailInvalid, passwordInvalid ->
                !emailInvalid && !passwordInvalid
            }

            invalidFieldStream.subscribe {
                showButtonLogin(it)
            }
        }
    }

    private fun showButtonLogin(state: Boolean) {
        binding.btnLogin.isEnabled = state
    }

    private fun showPasswordExistAlert(state: Boolean) {
        binding.edtPassword.error = if (state) resources.getString(R.string.pass_length_less_then_8) else null
    }

    private fun showEmailExistAlert(state: Boolean) {
        binding.edtEmail.error = if (state) resources.getString(R.string.email_not_valid) else null
    }
}