package com.techjays.chatlibrary.util

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.techjays.chatlibrary.model.common.Option
import java.util.*

object AppDialogs {

    private var progressDialog: Dialog? = null
    private var dialog: Dialog? = null
    private var custom_dialog: Dialog? = null
    private var selection_dialog: AlertDialog? = null
    private var bottom_dialog: BottomSheetDialog? = null
    private var mPopupWindow: PopupWindow? = null

    /**
     * Simple interface can be implemented for confirm an action via dialogs
     */
    interface ConfirmListener {
        fun yes()
    }

    interface OptionListener : ConfirmListener {
        fun no()
    }


    /**
     * Confirm actions that are critical before proceeding
     *
     * @param c
     * @param text
     * @param l
     */
    @SuppressLint("InlinedApi")
    fun confirmAction(c: Context, title: String, text: String, l: ConfirmListener?) {
        try {
            val alertDialog: AlertDialog
            val builder =
                AlertDialog.Builder(c, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
            builder.setTitle(title)
            builder.setMessage(text)

            builder.setPositiveButton(c.resources.getString(android.R.string.yes)) { dialog, which ->
                l?.yes()
                dialog.dismiss()
            }
            builder.setNegativeButton(c.resources.getString(android.R.string.no)) { dialog, which -> dialog.dismiss() }
            builder.setOnCancelListener { dialogInterface -> dialogInterface.dismiss() }
            alertDialog = builder.create()
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Show Ok Action Dialog
     * @param context Context
     * @param title Title
     * @param msg Message
     * @param action Positive Button Text
     * @param l ConfirmListener
     * @param isCancelable Close button view or hide
     */

    fun customOkAction(
        context: Context,
        msg: String
    ) {
        customOkAction(context, null, msg, null, null, false)
    }

    fun customOkAction(
        context: Context,
        title: String?,
        msg: String,
        action: String?,
        l: ConfirmListener?,
        isCancelable: Boolean
    ) {

        hidecustomView()

        val builder =
            AlertDialog.Builder(context, com.techjays.chatlibrary.R.style.BottomSheetDialog)
        val view = LayoutInflater.from(context)
            .inflate(com.techjays.chatlibrary.R.layout.lib_dialog_ok_action, null)

        val dialogTitle = view.findViewById(com.techjays.chatlibrary.R.id.dialog_title) as TextView
        val dialogMessage =
            view.findViewById(com.techjays.chatlibrary.R.id.dialog_message) as TextView
        val dialogAction =
            view.findViewById(com.techjays.chatlibrary.R.id.dialog_action_button) as TextView
        val dialogClose =
            view.findViewById(com.techjays.chatlibrary.R.id.dialog_close_button) as ImageView

        builder.setCancelable(false)

        if (isCancelable)
            dialogClose.visibility = View.VISIBLE
        else dialogClose.visibility = View.GONE

        dialogTitle.text = title
        dialogMessage.text = msg

        dialogAction.text = if ((action == null)) context.getString(android.R.string.ok) else action
        dialogAction.setOnClickListener {
            l?.yes()
            custom_dialog!!.dismiss()
        }

        dialogClose.setOnClickListener {
            custom_dialog!!.dismiss()
        }

        builder.setView(view)
        custom_dialog = builder.create()
        custom_dialog!!.show()
    }

    /**
     * Confirm actions that are critical before proceeding
     *
     * @param c
     * @param text
     */
    @SuppressLint("InlinedApi")
    fun okAction(c: Context, text: String) {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(c, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
        builder.setTitle("Alert Message")
        builder.setMessage(text)

        builder.setPositiveButton(c.resources.getString(android.R.string.ok)) { dialog, which -> dialog.dismiss() }

        builder.setOnCancelListener { dialogInterface -> dialogInterface.dismiss() }
        alertDialog = builder.create()
        alertDialog.show()

    }


    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showProgressDialog(context: Context) {
        showProgressDialog(context, true)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showProgressDialog(context: Context, isCancelable: Boolean) {
        hideProgressDialog()
        progressDialog = Dialog(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =
            inflater.inflate(com.techjays.chatlibrary.R.layout.lib_dialog_progress_custom, null)
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog!!.setContentView(view)
        progressDialog!!.setCancelable(isCancelable)
        progressDialog!!.show()
    }

    fun hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("InlinedApi")
    fun showcustomView(context: Context, layout: Int, isCancelable: Boolean): View? {
        try {
            hidecustomView()
            custom_dialog = Dialog(context, R.style.Theme_DeviceDefault_Light_Dialog_Alert)
            custom_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            custom_dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(layout, null)
            custom_dialog!!.setContentView(view)
            custom_dialog!!.setCancelable(isCancelable)
            custom_dialog!!.show()
            return view
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun hidecustomView() {
        try {
            if (custom_dialog != null && custom_dialog!!.isShowing) {
                custom_dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param context
     */
    fun hidecustomProgressDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    /**
     * Hides the soft keyboard
     *
     * @param activity Activity
     */
    fun hideSoftKeyboard(activity: Activity, view: View) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Shows the soft keyboard
     *
     * @param a    Activity
     * @param view current EditText view
     */
    fun showSoftKeyboard(a: Activity, view: View) {
        val inputMethodManager =
            a.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }

    fun showSnackbar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackbar(view: View, msg: String, target: View) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAnchorView(target).show()
    }

    /**
     *Short Toast
     */
    fun showToastshort(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     *Long Toast
     */
    fun showToastlong(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    fun initOptionDialog(
        context: Context,
        list: ArrayList<Option>,
        callback: DialogOptionAdapter.Callback
    ) {
        hidecustomView()
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(com.techjays.chatlibrary.R.layout.lib_dialog_option_selection, null)

        val builder =
            AlertDialog.Builder(context, com.techjays.chatlibrary.R.style.BottomSheetDialog)
        builder.setCancelable(false)

        val aRecycler =
            view.findViewById<RecyclerView>(com.techjays.chatlibrary.R.id.option_recycler)
        aRecycler.adapter = DialogOptionAdapter(context, list, callback)

        view.findViewById<ImageView>(com.techjays.chatlibrary.R.id.dialog_close_button)
            .setOnClickListener {
                custom_dialog!!.dismiss()
            }

        builder.setView(view)
        custom_dialog = builder.create()
        custom_dialog!!.show()
    }


    fun showFromDatedialogwithToday(
        context: Context,
        datepickListner: DatePickerDialog.OnDateSetListener
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(context, datepickListner, year, month, dayOfMonth)
        dialog.datePicker.minDate = System.currentTimeMillis() - 60 * 60 * 1000
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "SELECT", dialog)
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "", dialog)
        dialog.setTitle("")
        dialog.show()
    }

    @SuppressLint("InlinedApi")
    fun showTimedialog(context: Context, timepickListner: TimePickerDialog.OnTimeSetListener) {

        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(
            context,
            R.style.Theme_DeviceDefault_Light_Dialog_Alert,
            timepickListner,
            hourOfDay,
            minute,
            false
        )
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "SELECT", dialog)
        dialog.setTitle("")
        dialog.show()
    }


    fun hidePopupWindow() {
        if (mPopupWindow != null && mPopupWindow!!.isShowing)
            mPopupWindow!!.dismiss()
    }

    fun hideSingleChoice() {
        if (selection_dialog != null && selection_dialog!!.isShowing)
            selection_dialog!!.dismiss()
    }

    fun hideBottomDialog() {
        if (bottom_dialog != null && bottom_dialog!!.isShowing)
            bottom_dialog!!.dismiss()
    }


    /**
     * Show Toast Message
     *
     * @param context Context
     * @param desc    String
     */
    fun showToastDialog(context: Context, desc: String) {
        Toast.makeText(context, desc, Toast.LENGTH_SHORT).show()
    }

    /**
     * No Data Layout
     *
     * @param view View
     * @param show show/hide
     * @param icon Image
     * @param msg Message to show
     */


}