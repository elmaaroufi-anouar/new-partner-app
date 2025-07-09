package com.done.partner.presentation.store.product_detail

sealed class OptionGroupState {
    data class Radio(val selectedOption: String?) : OptionGroupState()
    data class Checkbox(val checkedItems: MutableMap<String, Boolean>) : OptionGroupState()
    data class Button(val quantities: MutableMap<String, Int>) : OptionGroupState()

    fun copyAsRadio(newOption: String?) = Radio(newOption)
    fun copyAsCheckbox(newCheckedItems: MutableMap<String, Boolean>) = Checkbox(newCheckedItems)
    fun copyAsButton(newQuantities: MutableMap<String, Int>) = Button(newQuantities)
}


enum class SelectionType {
    Radio, Checkbox, Button
}