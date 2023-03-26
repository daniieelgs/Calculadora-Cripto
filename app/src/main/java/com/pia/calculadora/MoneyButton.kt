package com.pia.calculadora

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import java.lang.NumberFormatException as NumberFormatException

open class MoneyButton (context: Context, attrs:AttributeSet? = null, style: Int = 0): MaterialButton(context, attrs, style), InstaceState<MoneyButton> {


    constructor(context: Context, attrs:AttributeSet? = null): this(context, attrs, 0){}

    var buttonSelected:Boolean = false
    var buttonFactored: Boolean = false
        private set

    var factor: Double = 0.0
        set(value){
            if(value < 0) throw NumberFormatException("Money value can not be less than 0")
            buttonFactored = value > 0
            field = value
        }

    fun convert(value: Double): Double = value / factor

    fun convert(value: Double, money: MoneyButton): Double = convert(value * money.factor)

    companion object{
        private val STATE_BUTTON_SELECTED: IntArray = intArrayOf(R.attr.state_button_selected)
        private val STATE_BUTTON_FACTORED: IntArray = intArrayOf(R.attr.state_button_factored)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)

        if(buttonSelected) mergeDrawableStates(drawableState, STATE_BUTTON_SELECTED)
        if(buttonFactored) mergeDrawableStates(drawableState, STATE_BUTTON_FACTORED)

        return drawableState
    }

    override fun saveInstanceState() {

        state = mutableMapOf("buttonSelected" to buttonSelected, "buttonFactored" to buttonFactored, "factor" to factor, "text" to text.toString())

    }

    override fun setInstanceState(instance: MoneyButton) {

        buttonSelected = instance.state["buttonSelected"] as Boolean
        buttonFactored = instance.state["buttonFactored"] as Boolean
        factor = instance.state["factor"] as Double
        text = instance.state["text"] as String

        refreshDrawableState()

    }

    override lateinit var state: MutableMap<String, Any>

}