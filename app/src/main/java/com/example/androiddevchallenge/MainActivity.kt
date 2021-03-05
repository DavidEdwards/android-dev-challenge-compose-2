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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.app_name)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = LocalContext.current.getString(R.string.app_name))
            })
        },
        content = {
            TimerScreen()
        }
    )
}

@Composable
fun TimerScreen() {
    val vm: MainViewModel = viewModel()
    val listData by vm.list().observeAsState(initial = ListData(emptyList()))

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Timer()

            Spacer(modifier = Modifier.height(16.dp))

            Results(listData = listData)
        }
    }
}

@Composable
fun Timer() {
    val vm: MainViewModel = viewModel()
    val time by vm.current().observeAsState(initial = TimePair(0, 0))

    Column {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp),
                color = MaterialTheme.colors.surface,
                elevation = 16.dp,
                shape = CircleShape
            ) {
                TimerCircle(time = time)

                TimerInnerText(time = time)
            }
        }
    }
}

@Composable
fun TimerCircle(time: TimePair) {
    val vm: MainViewModel = viewModel()

    val progress = if (time.remainingTime != 0 && time.totalTime != 0) {
        (time.remainingTime.toFloat() / time.totalTime.toFloat())
    } else 0f


    val progressState = animateFloatAsState(
        targetValue = progress
    )

    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                vm.start(1)
            },
        progress = progressState.value,
        strokeWidth = 16.dp,
        color = MaterialTheme.colors.secondaryVariant
    )
}

@Composable
fun TimerInnerText(time: TimePair) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            if (time.remainingTime == 0) {
                Text(
                    text = "Tap me!",
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "${time.remainingTime}",
                    style = MaterialTheme.typography.h1
                )
            }
        }
    }
}

@Composable
fun Results(listData: ListData) {
    Column {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Completed timers",
            style = MaterialTheme.typography.h6
        )

        if (listData.list.isNotEmpty()) {
            LazyColumn {
                items(listData.list) { result ->
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "$result second timer"
                    )
                }
            }
        } else {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "No timers have run, please click on the button to start a timer."
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
