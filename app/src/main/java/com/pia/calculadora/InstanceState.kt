package com.pia.calculadora

import java.io.Serializable

interface InstaceState<T>: Serializable {

    var state: MutableMap<String, Any>

    fun saveInstanceState()
    fun setInstanceState(instance: T)

}