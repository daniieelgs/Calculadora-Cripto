package com.pia.calculadora

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.marginBottom
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.setMargins

class CompatMoneyButton(context: Context, textShow: String = "", factor: Double = 0.0): MoneyButton(ContextThemeWrapper(context, R.style.moneyButtons)) {

    init{
        text = textShow

        setParamsLayout()

        cornerRadius = dpToPx(10)

        super.factor = factor

    }

    private fun isLandscape() = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun setParamsLayout(){
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, if(isLandscape()) dpToPx(100) else ViewGroup.LayoutParams.MATCH_PARENT)

        params.setMargins(dpToPx(8), if(isLandscape()) 0 else marginTop, dpToPx(8), if(isLandscape()) 0 else marginBottom)

        layoutParams = params
    }

    override fun setInstanceState(instance: MoneyButton) {
        super.setInstanceState(instance)
        setParamsLayout()
    }

    private fun dpToPx(dp: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        Resources.getSystem().displayMetrics).toInt()

}