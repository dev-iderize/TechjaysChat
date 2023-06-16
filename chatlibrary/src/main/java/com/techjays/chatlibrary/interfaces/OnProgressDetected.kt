package com.techjays.chatlibrary.interfaces

interface FileUploadProgress {

    fun changeProgress(progress: Int)
    fun errorHappened()
    fun completedSuccessfully()
}