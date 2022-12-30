package com.udacity


sealed class ButtonState(val strResId: Int) {
    object Normal : ButtonState(R.string.button_completed)
    object Clicked : ButtonState(R.string.button_clicked)
    object Loading : ButtonState(R.string.button_loading)
    object Completed : ButtonState(R.string.button_completed)
}