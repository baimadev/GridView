package com.baima.jetpack

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HomeContentViewModel @ViewModelInject constructor(
val data:ServiceTest,
@Assisted val state:SavedStateHandle

): ViewModel() {


    fun print(){
        data.hiltTest();
    }
}