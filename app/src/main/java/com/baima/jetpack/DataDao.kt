package com.baima.jetpack

import android.content.Context
import android.util.Log
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class DataDao constructor(private val msg:String){

    fun hiltTest(){
        Log.e("xia","$msg hilt test")
    }
}


class Test constructor(private val msg:String){

    fun hiltTest(){
        Log.e("xia","$msg hilt test")
    }
}

class ServiceTest @Inject constructor(){
    fun hiltTest(){

        Log.e("xia"," hilt test ")
    }
}

