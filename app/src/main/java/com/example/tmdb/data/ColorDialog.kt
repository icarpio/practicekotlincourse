package com.example.tmdb.data

import android.content.Context
import androidx.appcompat.app.AlertDialog

// Clase para mostrar un diálogo de selección de color
class ColorDialog(private val context: Context, private val colors: Array<Int>, private val onColorSelected: (Int) -> Unit) {
    fun show() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Select a color")
        dialog.setItems(colors.map { context.getString(it) }.toTypedArray()) { _, which ->
            onColorSelected(colors[which])
        }
        dialog.show()
    }
}