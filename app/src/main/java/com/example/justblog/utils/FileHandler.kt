package com.example.justblog.utils

import java.io.File

class FileHandler {

    fun saveFile(file : File): Boolean {

        if (!file.exists()) {
            return file.createNewFile()
        }
        return false
    }

    fun deleteFile(file: File): Boolean{
        if(file.exists()){
            return file.delete()
        }
        return false
    }
}