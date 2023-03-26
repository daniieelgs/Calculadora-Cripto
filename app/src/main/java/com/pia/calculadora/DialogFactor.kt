package com.pia.calculadora

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@SuppressLint("InflateParams")
class DialogFactor(context: Activity, button: MoneyButton, moneyList: Array<MoneyButton>, onPositive: (DialogInterface) -> Unit, onCancel: DialogInterface.OnCancelListener, onRemove: OnClickListener): MaterialAlertDialogBuilder(context) {

    var dialog: AlertDialog
        private set

    val layout: View

    init{

        val title: String = button.text.toString()

        var valueCoin = ControlValueCoin(context, button, moneyList)

        layout = valueCoin.layout

        setTitle(title)
        setView(layout)
        setNeutralButton(context.getString(R.string.neutral_button_dialog)) {dialog, _ -> dialog.cancel() }
        setPositiveButton(context.getString(R.string.positive_confirm_button_dialog)) {_, _ -> }
        setOnCancelListener (onCancel)
        create()

        with(create()){
            dialog = this

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            setOnShowListener { Idialog ->
                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if(!valueCoin.verifyInput()){
                        SnackbarCustom.position = SnackbarCustom.SnackbarLayout.TOP
                        SnackbarCustom.showSnackbarError(context, context.getString(R.string.message_error_value_factor), view = layout)
                    }else{
                        button.factor = valueCoin.moneyValue * valueCoin.moneyFactorSelected.first.factor
                        button.refreshDrawableState()
                        Idialog.dismiss()
                        onPositive(Idialog)
                    }
                }
            }
        }

        layout.findViewById<Button>(R.id.btnDeleteCoin).setOnClickListener{
            onRemove.onClick(it)
            dialog.dismiss()
        }
    }

}