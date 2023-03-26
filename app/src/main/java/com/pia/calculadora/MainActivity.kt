package com.pia.calculadora

//© Daniel Garcia Serrano

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var txScreen: ScreenResult

    private lateinit var txtNumber: String

    private lateinit var moneyButtons: ArrayList<MoneyButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txScreen = findViewById(R.id.txScreen)

        txtNumber = txScreen.text.toString()

        findViewById<Button>(R.id.btnNumberDelete).setOnLongClickListener(::deleteAllNumber)

        loadMoneyButtons(R.id.btnMoneyEuro, R.id.btnMoneyADA, R.id.btnMoneyBTC, R.id.btnMoneyETH, R.id.btnMoneyLTC)

        moneyButtons[0].factor = 1.0
        moneyButtons[0].buttonSelected = true
    }

    private fun convert(moneySelected: MoneyButton, moneyValue: MoneyButton){
        txScreen.textNumber = moneySelected.convert(txScreen.textNumber, moneyValue)
    }

    private fun clickMoneyButton(v: View?){

        with(v as MoneyButton){

            if(!buttonFactored) {
                createDialogFactor(this, deleteButton = false)?.show()
            }else{
                convert(this, getMoneyButtonSelectedOrDefault(moneyButtons[0]))
            }

            getMoneyButtonSelected()?.let {
                it.buttonSelected = false
                it.refreshDrawableState()
            }

            buttonSelected = true
        }

    }

    private fun longClickMoneyButton(v: View?): Boolean{

        val btn: MoneyButton = v as MoneyButton

        createDialogFactor(btn)?.show()

        return false
    }

    private fun getMoneyButtonSelected(): MoneyButton? = moneyButtons.filter { it.buttonSelected }.let { if(it.isEmpty()) null else it[0]}
    private fun getMoneyButtonSelectedOrDefault(defaultMoneyButton: MoneyButton): MoneyButton = getMoneyButtonSelected() ?: defaultMoneyButton

    private fun createDialogFactor(button: MoneyButton, moneyButtonSelected: MoneyButton = getMoneyButtonSelectedOrDefault(moneyButtons[0]), deleteButton: Boolean = true): AlertDialog? {

        if(button == moneyButtons[0]) return null

        val dialog = DialogFactor(this, button, moneyButtons.filter { it != button && it.buttonFactored }.toTypedArray(),
            { if(button.buttonSelected) convert(button, moneyButtonSelected) },
            {
                if(!button.buttonFactored){
                    button.buttonSelected = false
                    moneyButtonSelected.buttonSelected = true
                }
            },
            {

                val name = button.text.toString()

                MaterialAlertDialogBuilder(this)
                    .setTitle(String.format(getString(R.string.title_dialog_coin_delete), name))
                    .setMessage(String.format(getString(R.string.message_dialog_coin_delete), name))
                    .setNegativeButton(getString(R.string.negative_no_button_dialog)){_,_ ->}
                    .setPositiveButton(getString(R.string.positive_yes_button_dialog)){_, _ ->
                        if(button.buttonSelected) clickMoneyButton(moneyButtons[0])

                        findViewById<LinearLayout>(R.id.linearMoneyButtons).removeView(button)

                        moneyButtons.remove(button)

                        SnackbarCustom.position = SnackbarCustom.SnackbarLayout.BOTTOM
                        SnackbarCustom.showSnackbarInfo(this, String.format(getString(R.string.info_coin_deleted), name))
                    }.show()

            }
        )

        if(!deleteButton) dialog.layout.findViewById<Button>(R.id.btnDeleteCoin).visibility = View.GONE

        return dialog.dialog

    }

    private fun loadMoneyButtons(vararg button: MoneyButton){

        val addEvents = {btn: MoneyButton ->
            btn.setOnClickListener(::clickMoneyButton)
            btn.setOnLongClickListener(::longClickMoneyButton)
        }

        if(!::moneyButtons.isInitialized) moneyButtons = ArrayList(button.toList().onEach {
            addEvents(it)
        })
        else button.forEach {
            moneyButtons.add(it)
            addEvents(it)
        }

    }

    private fun loadMoneyButtons(vararg id: Int) = loadMoneyButtons(*id.map<MoneyButton>(::findViewById).toTypedArray())


    private fun vibratePhone(time: Long = 200, effect: Int = VibrationEffect.EFFECT_TICK){
        @Suppress("DEPRECATION") val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(time, effect))
    }

    fun numberClick(v : View?) = txScreen.appendNumber((v as Button).text.toString())

    private fun deleteAllNumber(v: View?): Boolean{

        txScreen.deleteAllNumber()

        vibratePhone()

        return false
    }

    fun deleteClick(v : View?){
        when(v?.id){

            R.id.btnNumberDelete -> txScreen.deleteNumber()
            R.id.btnNumberAc -> deleteAllNumber(v)

        }
    }

    fun addMoney(v: View?){

        var edtName = EditText(this)

        val defaultNameCoin = "[...]"

        val valueCoin = ControlValueCoin(this, defaultNameCoin, moneyButtons.filter { it.buttonFactored }.toTypedArray(), layoutInflater.inflate(R.layout.dialog_new_coin,null), true)

        val edtCoinName = valueCoin.layout.findViewById<EditText>(R.id.edtCoinName)

        valueCoin.layout.findViewById<Button>(R.id.btnDeleteCoin).visibility = View.GONE

        edtCoinName.addTextChangedListener {
            valueCoin.nameCoin = edtCoinName.text.toString().trim().ifEmpty { defaultNameCoin }
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.titleAddMoney))
            .setMessage(getString(R.string.messageAddMoney))
            .setView(valueCoin.layout)
            .setNeutralButton(getString(R.string.neutral_button_dialog)) {_, _ ->}
            .setPositiveButton(getString(R.string.positive_confirm_button_dialog)){_, _ ->}
            .create()

        dialog.setOnShowListener { Idialog ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if(valueCoin.nameCoin == defaultNameCoin || valueCoin.nameCoin.isEmpty()){
                    SnackbarCustom.position = SnackbarCustom.SnackbarLayout.TOP
                    SnackbarCustom.showSnackbarError(this, getString(R.string.message_error_coin_name), view = valueCoin.layout)
                }else if(!valueCoin.verifyInput()){
                    SnackbarCustom.position = SnackbarCustom.SnackbarLayout.TOP
                    SnackbarCustom.showSnackbarError(this, getString(R.string.message_error_value_factor), view = valueCoin.layout)
                }else if(moneyButtons.any { it.text.toString().uppercase() == valueCoin.nameCoin.uppercase() }){
                    SnackbarCustom.position = SnackbarCustom.SnackbarLayout.TOP
                    SnackbarCustom.showSnackbarError(this, String.format(getString(R.string.message_error_name_alredy_exist), valueCoin.nameCoin), view = valueCoin.layout)
                }else{

                    var newMoneyButton: MoneyButton = CompatMoneyButton(this, valueCoin.nameCoin, valueCoin.moneyValue)

                    loadMoneyButtons(newMoneyButton)

                    with(findViewById<LinearLayout>(R.id.linearMoneyButtons)){
                        var btnAddMoney: Button = findViewById(R.id.btnAddMoney)
                        removeView(btnAddMoney)
                        addView(newMoneyButton)
                        addView(btnAddMoney)
                    }

                    dialog.dismiss()

                    SnackbarCustom.showSnackBarCorrect(this, String.format(getString(R.string.info_coin_created, valueCoin.nameCoin)))

                }
            }
        }

        dialog.show()

    }

    override fun onSaveInstanceState(outState: Bundle) {

        txScreen.saveInstanceState()

        moneyButtons.forEach { it.saveInstanceState() }

        outState.putSerializable("screen", txScreen)
        outState.putSerializable("moneyButtons", moneyButtons)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        txScreen.setInstanceState(savedInstanceState.getSerializable("screen") as ScreenResult)

        val listMoneyButtons: LinearLayout = findViewById(R.id.linearMoneyButtons)

        val addMoneyButton: Button = findViewById(R.id.btnAddMoney)

        (savedInstanceState.getSerializable("moneyButtons") as ArrayList<MoneyButton>).forEach { it ->

            var button: MoneyButton = it


            if(moneyButtons.any{bt -> bt.text == button.text}){

                moneyButtons[moneyButtons.let {
                    var index = 0

                    while(moneyButtons[index].text != button.text) index++

                    index
                }].setInstanceState(button)

            }else{

                button.parent?.let {parent ->
                    (parent as LinearLayout).removeView(button)
                }

                button.setInstanceState(button)

                moneyButtons.add(button)
                listMoneyButtons.removeView(addMoneyButton)
                listMoneyButtons.addView(button)
                listMoneyButtons.addView(addMoneyButton)
            }
        }

        super.onRestoreInstanceState(savedInstanceState)
    }
}

