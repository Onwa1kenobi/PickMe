package io.onwa.pickme.adapter

interface PickCallback {
    fun onPickMade(pick: Any)
    fun onDeselectPick(pick: Any) {}
}