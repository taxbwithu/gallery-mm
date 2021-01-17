package no.bstcm.gallery.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_login.*
import no.bstcm.gallery.R
import no.bstcm.gallery.login.UserRepository
import no.bstcm.gallery.rxutils.SchedulerProvider

class LoginDialog() : AppCompatDialogFragment() {
    var editTextUsername: EditText? = null
    var editTextPassword: EditText? = null
    var dialogView : View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogTheme)
        val schedulerProvider = SchedulerProvider()
        val inflater = activity?.layoutInflater
        dialogView = inflater?.inflate(R.layout.dialog_login, null)

        val dialog = builder.setView(dialogView)
            .setTitle("Login")
            .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which ->

            })
            .setPositiveButton("ok", DialogInterface.OnClickListener { dialog, which ->

            }).create()
        dialog.setOnShowListener(OnShowListener {
            val b: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val userRepository = UserRepository(schedulerProvider)
                    val result = userRepository.logIn(
                        editTextUsername?.text.toString(),
                        editTextPassword?.text.toString()
                    ).observeOn(schedulerProvider.computation)
                        .subscribe(object : CompletableObserver {
                            override fun onComplete() {
                                val handler = Handler(Looper.getMainLooper());
                                val runnable = object : Runnable {
                                    override fun run() {
                                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                            "result",
                                            true
                                        )
                                        errorDisplay(true)
                                    }
                                }
                                handler.post(runnable);

                                dialog.dismiss()
                            }

                            override fun onError(e: Throwable) {
                                val handler = Handler(Looper.getMainLooper());
                                val runnable = object : Runnable {
                                    override fun run() {
                                        errorDisplay(false)
                                    }
                                }
                                handler.post(runnable);
                                Log.d("TAG", "Error")
                            }

                            override fun onSubscribe(d: Disposable) {
                            }
                        })

                }
            })
        })
        editTextUsername = dialogView?.findViewById(R.id.input_login)
        editTextPassword = dialogView?.findViewById(R.id.input_password)
        return dialog
    }

    fun errorDisplay(credentialsCorrect : Boolean) {
        val error = dialogView?.findViewById<TextView>(R.id.text_input_error)
        if(credentialsCorrect){
            error?.visibility = View.GONE
        }
        else{
            error?.visibility = View.VISIBLE
        }
    }
}