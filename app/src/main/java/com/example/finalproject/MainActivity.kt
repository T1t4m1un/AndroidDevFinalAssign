package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.theme.FinalProjectTheme
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    Scaffold(
        topBar = { MyAppBar() },
        content = {paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                FunctionList(name = "备忘录", MemoActivity::class)
                Divider()
                FunctionList(name = "代办事项", TodoActivity::class)
                Divider()
                FunctionList(name = "记账", AccountActivity::class)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar() {
    TopAppBar(
        title = { Text("安卓大作业", color = Color.White) },
        actions = {
            IconButton(onClick = { /* 执行设置相关的动作 */ }) {
                Icon(Icons.Filled.Settings, contentDescription = "设置")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black),
    )
}

@Composable
fun FunctionList(name: String, nextIndent: KClass<out Activity>) {
    val context = LocalContext.current
    val onClick = {
        val intent = Intent(context, nextIndent.java)
        context.startActivity(intent)
    }
    Text(
        text = name,
        style = TextStyle(fontSize = 25.sp),
        modifier = Modifier.clickable(onClick = onClick).height(50.dp)
    )
}