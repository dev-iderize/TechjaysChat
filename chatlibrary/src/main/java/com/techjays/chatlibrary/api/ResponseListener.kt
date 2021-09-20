package com.techjays.chatlibrary.api

interface ResponseListener {

    /**
     * @param r - The model class that is passed on the parser
     */
    fun onResponse(r: Response?)
}