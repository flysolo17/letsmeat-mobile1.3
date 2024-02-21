package com.cjay.letsmeat.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.cjay.letsmeat.R

import com.google.android.material.dialog.MaterialAlertDialogBuilder


class LoadingDialog(private val context: Context) {

    var alertDialog: AlertDialog ? = null
    fun showDialog(title : String){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        val view : View = LayoutInflater.from(context).inflate(R.layout.dialog_loading,null)
        view.findViewById<TextView>(R.id.textTitle).text = title
        materialAlertDialogBuilder.setView(view)
        materialAlertDialogBuilder.setCancelable(false)
        alertDialog = materialAlertDialogBuilder.create()
        alertDialog?.show()
    }
    fun closeDialog(){
        alertDialog?.dismiss()
    }
}