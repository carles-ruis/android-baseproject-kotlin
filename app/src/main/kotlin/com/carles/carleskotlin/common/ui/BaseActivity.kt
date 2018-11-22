package com.carles.carleskotlin.common.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import com.carles.carleskotlin.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.layout_progressbar.*
import javax.inject.Inject

abstract class BaseActivity<P : BasePresenterContract<V>, V : BaseView> : AppCompatActivity(), BaseView {

    protected var alertDialog: AlertDialog? = null
    protected abstract val layoutResourceId: Int
    protected abstract fun initViews()
    protected abstract fun getView(): V
    protected open fun initComponents() {}

    @Inject
    internal lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        overridePendingTransition(R.anim.slide_in_from_right_to_left, R.anim.slide_out_from_right_to_left)
        setContentView(layoutResourceId)
        initViews()
        initComponents()
        presenter.onViewCreated(getView())
    }

    override fun onDestroy() {
        presenter.onViewDestroyed()
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_from_left_to_right, R.anim.slide_out_from_left_to_right)
    }

    override fun showProgress() {
        progressbar_layout?.visibility = VISIBLE
    }

    override fun hideProgress() {
        progressbar_layout?.visibility = GONE
    }

    override fun showError(messageId: Int, onRetry: (() -> Unit)?) {
        hideProgress()
        if (isFinishing) return

        val isShowing = alertDialog?.isShowing ?: false
        if (!isShowing) {
            val alertDialogBuilder = AlertDialog.Builder(this).setMessage(messageId)
            if (onRetry != null) {
                alertDialogBuilder.setCancelable(false)
                    .setPositiveButton(R.string.error_retry) { _, _ -> onRetry() }
            }
            alertDialogBuilder.create().show()
        }
    }
}