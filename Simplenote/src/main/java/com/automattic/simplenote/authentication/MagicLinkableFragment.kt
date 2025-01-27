package com.automattic.simplenote.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.automattic.simplenote.R
import com.google.android.material.textfield.TextInputLayout
import com.simperium.android.ProgressDialogFragment

/**
 * Base class used to share logic between sign up and login, specifically related to magic links.
 */
abstract class MagicLinkableFragment : Fragment() {

    private var progressDialogFragment: ProgressDialogFragment? = null

    abstract fun inflateLayout(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    abstract fun actionButtonText(): String

    abstract fun onActionButtonClicked(view: View, emailEditText: EditText)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflateLayout(inflater, container, savedInstanceState)
        view?.let { initSignupButton(it) }
        return view
    }

    private fun initSignupButton(view: View) {
        val emailEditText = (view.findViewById<View>(R.id.input_email) as TextInputLayout).editText
        val signupButton = view.findViewById<Button>(R.id.button)
        signupButton.text = actionButtonText()

        setButtonState(signupButton, emailEditText!!.text)
        listenToEmailChanges(emailEditText, signupButton)
        listenToActionButtonClick(signupButton, emailEditText)
    }

    private fun setButtonState(signupButton: Button, email: CharSequence) {
        signupButton.isEnabled = isValidEmail(email)
    }

    private fun isValidEmail(text: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    private fun listenToEmailChanges(emailEditText: EditText, signupButton: Button) {
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                setButtonState(signupButton, s)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun listenToActionButtonClick(actionButton: Button, emailEditText: EditText) {
        actionButton.setOnClickListener {
            onActionButtonClicked(it, emailEditText)
        }
    }

    protected fun showConfirmationScreen(email: String, isSignUp: Boolean) {
        val confirmationFragment = ConfirmationFragment.newInstance(email, isSignUp)
        requireFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, confirmationFragment, SimplenoteSignupActivity.SIGNUP_FRAGMENT_TAG)
            .commit()
    }

    fun showProgressDialog(label: String) {
        progressDialogFragment =
            ProgressDialogFragment.newInstance(label)
        progressDialogFragment?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Simperium)
        progressDialogFragment?.show(requireFragmentManager(), ProgressDialogFragment.TAG)
    }

    protected fun hideDialogProgress() {
        progressDialogFragment?.let {
            if (!it.isHidden) {
                it.dismiss()
                progressDialogFragment = null
            }
        }
    }

    protected fun showDialogError(message: String) {
        hideDialogProgress()
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.simperium_dialog_title_error)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
