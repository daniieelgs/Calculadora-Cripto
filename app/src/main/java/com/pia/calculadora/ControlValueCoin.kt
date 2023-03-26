package com.pia.calculadora

import android.annotation.SuppressLint
import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener

@SuppressLint("SetTextI18n")
class ControlValueCoin(context: Activity, coinName: String, moneyList: Array<MoneyButton>, val layout: View = context.layoutInflater.inflate(R.layout.dialgo_factor,null), var permitZero: Boolean = false) {

    private val messageDialogFactor = context.getString(R.string.message_dialog_factor)

    private var message: String = messageDialogFactor

    private val tvEquality = layout.findViewById<TextView>(R.id.tvEquality)

    private var valueText = "0"

    private val setTextEquality = { valueText: String, money: String -> tvEquality.text = String.format(templateTextEquality, "1 $nameCoin", "$valueText $money") }

    private val txMessage: TextView = layout.findViewById(R.id.txMessage)

    var nameCoin: String = coinName
        set(value){
            message = String.format(messageDialogFactor, moneyFactorSelected.second, value.uppercase())
            txMessage.text = message
            field = value.uppercase()
            setTextEquality(valueText, moneyFactorSelected.second)
        }

    private var templateTextEquality: String

    private val moneyListName = moneyList.map { it.text.toString() }

    var moneyFactorSelected = moneyList[0] to moneyListName[0]
        private set

    private val edtFactor: EditText = layout.findViewById(R.id.edtFactor)

    var moneyValue: Double
        get() = edtFactor.text.toString().toDouble()
        set(value){
            edtFactor.setText(value.toString())
        }


    val verifyInput = {edtFactor.text != null && edtFactor.text.trim().isNotEmpty() && edtFactor.text.toString().toDouble().let { if(permitZero) it >= 0.0 else it > 0.0 }}

    constructor(context: Activity, button: MoneyButton, moneyList: Array<MoneyButton>): this(context, button.text.toString(), moneyList){
        edtFactor.setText(if(button.buttonFactored) button.factor.toString() else "0")
    }

    init{

        edtFactor.setText("0")

        //message = String.format(messageDialogFactor, moneyFactorSelected.second, coinName)

        txMessage.text = message

        val spMoney:Spinner = layout.findViewById(R.id.spMoney)

        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, moneyListName)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMoney.adapter = arrayAdapter

        templateTextEquality = tvEquality.text.toString()

        //setTextEquality(valueText, moneyFactorSelected.second)

        nameCoin = coinName

        val valueOrDefault = {if(edtFactor.text.toString() == "") edtFactor.setText("0") else if(edtFactor.text.toString() == "0") edtFactor.setText("")}

        edtFactor.setOnKeyListener { _, i, keyEvent ->

            if(!(i == 67 && keyEvent.action == KeyEvent.ACTION_UP && edtFactor.text.toString() == "0")) valueOrDefault()

            if(i == 67 && keyEvent.action == KeyEvent.ACTION_UP && edtFactor.text.toString() == "0") edtFactor.setSelection(1)

            valueText = edtFactor.text.toString()

            setTextEquality(valueText, moneyFactorSelected.second)

            false
        }

        edtFactor.addTextChangedListener {

            val temp = edtFactor.text.toString()

            if(temp.contains(".") && temp.split(".")[0].trim().isEmpty()) edtFactor.setText("0$temp")

            if(temp.contains(".") && temp.split(".")[1].length > 8){
                SnackbarCustom.position = SnackbarCustom.SnackbarLayout.TOP
                SnackbarCustom.showSnackbarError(context, context.getString(R.string.message_error_max_decimal), view = edtFactor)
                edtFactor.setText(temp.substring(0, temp.length - 1))
                edtFactor.setSelection(temp.length - 1)
            }

        }

        spMoney.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                message = message.replace(moneyFactorSelected.second, moneyListName[p2])

                txMessage.text = message

                moneyFactorSelected = moneyList[p2] to moneyListName[p2]

                setTextEquality(valueText, moneyFactorSelected.second)

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

    }

}