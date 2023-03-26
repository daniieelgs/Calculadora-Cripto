package com.pia.calculadora

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import com.google.android.material.textview.MaterialTextView
import java.math.BigDecimal
import kotlin.math.abs

class ScreenResult (context: Context, attrs:android.util.AttributeSet? = null): MaterialTextView(context, attrs), InstaceState<ScreenResult> {

    var textSizeSP: Float = textSize / resources.displayMetrics.scaledDensity
        get() = textSize / resources.displayMetrics.scaledDensity
        set(value){
            setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
            refreshDrawableState()
            field = value
        }

    private val jumpSize = 10

    private var initSizeText: Float? = null

    private var sizesText: MutableMap<String, Float>? = null

    private var innerText: String = text.toString()

    var textNumber: Double = text.toString().replace(context.getString(R.string.miller_separator), "").replace(context.getString(R.string.decimal_separator), ".").toDouble()
        get() = text.toString().replace(context.getString(R.string.miller_separator), "").replace(context.getString(R.string.decimal_separator), ".").toDouble()
        set(value){

            var txt: String = BigDecimal(value).toPlainString()

            if(txt.contains(".") && txt.split(".")[1] == context.getString(R.string.number0)) txt = txt.split(".")[0]

            text = txt.replace(".", context.getString(R.string.decimal_separator))

            field = value
        }

    private fun init(){
        if(initSizeText == null) initSizeText = textSizeSP

        if(sizesText == null) sizesText = mutableMapOf("1" to initSizeText!!)

        isLongClickable = false
    }

    private fun millers(): String{

        var txt = ""

        val decimal = context.getString(R.string.decimal_separator)
        val miller = context.getString(R.string.miller_separator)

        innerText = innerText.replace(miller, "")

        val tempTxt = if(innerText.contains(decimal)) innerText.split(decimal)[0] else innerText

        for((i, v) in tempTxt.withIndex()){

            val n = tempTxt.length - 2 - i

            txt += v

            if((n-2) >= 0 && (n-2) % 3 == 0) txt += miller

        }

        innerText = txt + if(innerText.contains(decimal)) context.getString(R.string.decimal_separator) + innerText.split(decimal)[1] else ""

        return innerText

    }

    private fun addNumber(n: String): String{

        val decimal: String = context.getString(R.string.decimal_separator)
        val miller: String = context.getString(R.string.miller_separator)

        if(innerText == context.getString(R.string.number0) && n != decimal){
            innerText = n
            return innerText
        }

        if(innerText.contains(decimal) && n == decimal) return innerText
        if(innerText.contains(decimal) && innerText.split(decimal)[1].replace(miller, "").length >= 8){
            SnackbarCustom.position = SnackbarCustom.SnackbarLayout.BOTTOM
            SnackbarCustom.showSnackbarInfo(context as Activity, context.getString(R.string.message_error_max_decimal))
            return innerText
        }
//        if(n != decimal && !innerText.contains(decimal) && innerText.replace(miller, "").length >= 8) return innerText

        innerText += n

        return millers()
    }

    fun appendNumber(n: String){
        text = addNumber(n)
    }

    fun deleteNumber(){
        innerText = if(innerText.length <= 1) context.getString(R.string.number0) else innerText.substring(0, innerText.length-1)
        text = millers()
    }

    fun deleteAllNumber(){
        innerText = context.getString(R.string.number0)
        text = innerText

    }

    override fun setText(text: CharSequence?, type: BufferType?) {

        var bigDifference = abs((text?.length ?: this.text.length) - this.text.length) > 2

        init()

        innerText = text.toString()

        if(innerText.contains(context.getString(R.string.decimal_separator))){

            if(innerText.split(context.getString(R.string.decimal_separator))[1].length > 8){
                innerText = innerText.substring(0, innerText.indexOf(context.getString(R.string.decimal_separator)) + 9)
            }

            while(innerText.contains(context.getString(R.string.decimal_separator)) && innerText.split(context.getString(R.string.decimal_separator))[1].endsWith("0")){
                innerText = innerText.substring(0, innerText.length-1)
            }

        }

//        if(innerText.contains(context.getString(R.string.decimal_separator)) && innerText.split(context.getString(R.string.decimal_separator))[1].length > 8){
//            innerText = innerText.substring(0, innerText.indexOf(context.getString(R.string.decimal_separator)) + 9)
//            this.text = innerText
//        }

        super.setText(millers(), type)

        if(sizesText!!.containsKey(text.toString().length.toString())){
            textSizeSP = sizesText!![text.toString().length.toString()]!!

            if(lineCount != 1) updateTextSize()
        }
        else if(bigDifference) updateTextSize()
        else{

            post{
                while(lineCount > 1){
                    textSizeSP -= jumpSize
                }
                sizesText!![text.toString().length.toString()] = textSizeSP
            }

            while(lineCount > 1) textSizeSP -= 10
        }

    }

    private fun updateTextSize(){

        post{
            if(lineCount > 1){
                textSizeSP -= jumpSize
                updateTextSize()
            }
            sizesText!![text.toString().length.toString()] = textSizeSP
        }
    }

    override fun saveInstanceState() {

        state = mutableMapOf("innerText" to innerText, "textSizeSP" to textSizeSP)

    }

    override fun setInstanceState(instance: ScreenResult) {

        innerText = instance.state["innerText"] as String
        //textSizeSP = instance.state["textSizeSP"] as Float

        updateTextSize()

        text = innerText

    }

    override lateinit var state: MutableMap<String, Any>


}