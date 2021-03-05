/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val countdown = MutableLiveData<TimePair>()
    fun current(): LiveData<TimePair> = countdown

    private val countdownList = MutableLiveData<ListData>()
    fun list(): LiveData<ListData> = countdownList

    fun start(time: Int) = viewModelScope.launch {
        val remainingTime = countdown.value?.remainingTime ?: 0
        val totalTime = countdown.value?.totalTime ?: 0

        if (remainingTime > 0) {
            countdown.value =
                TimePair(remainingTime = remainingTime + time, totalTime = totalTime + time)
            return@launch
        }

        var currentTimeRemaining = time
        var currentTotalTime = time
        countdown.value = TimePair(remainingTime = currentTimeRemaining, totalTime = currentTotalTime)

        while (currentTimeRemaining > 0) {
            delay(1000L)
            currentTimeRemaining = countdown.value?.remainingTime ?: currentTimeRemaining
            currentTotalTime = countdown.value?.totalTime ?: currentTotalTime
            Log.v("TIMER", "Timer at $currentTimeRemaining / $currentTotalTime")
            currentTimeRemaining -= 1
            countdown.value = TimePair(remainingTime = currentTimeRemaining, totalTime = currentTotalTime)
        }

        addResult(currentTotalTime)
    }

    private fun addResult(time: Int) {
        viewModelScope.launch {
            val data = countdownList.value ?: ListData(listOf())
            val list = ArrayList(data.list)
            list.add(0, time)
            countdownList.value = ListData(list)
        }
    }

}