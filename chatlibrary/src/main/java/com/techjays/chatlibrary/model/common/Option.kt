package com.techjays.chatlibrary.model.common


import android.graphics.drawable.Drawable
import com.techjays.chatlibrary.api.Response


/**
 * Created by Sharon on 10.7.20.
 **/

class Option(var mId:Int,var mName: String) : Response() {
    var mOptions = ArrayList<Option>()
}