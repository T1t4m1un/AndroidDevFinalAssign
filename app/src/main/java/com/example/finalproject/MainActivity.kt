package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.ui.theme.FinalProjectTheme
import kotlin.reflect.KClass

data class FunctionModule(val name: String, val functionClass: KClass<out Activity>, var enabled: Boolean)

var functionModules by mutableStateOf(
    listOf(
        FunctionModule("备忘录", MemoActivity::class, true),
        FunctionModule("代办事项", TodoActivity::class, true),
        FunctionModule("记账本", AccountActivity::class, true)
    )
)

@Composable
fun FunctionModuleItem(module: FunctionModule) {
    val context = LocalContext.current
    val onClick = {
        val intent = Intent(context, module.functionClass.java)
        context.startActivity(intent)
    }

    // 添加样式的 Column
    Column(
        modifier = Modifier
            .fillMaxWidth() // 使 Column 填充最大宽度
            .padding(8.dp) // 周围添加内边距
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)) // 边框
            .background(Color.White, RoundedCornerShape(8.dp)) // 背景和圆角
            .clickable(onClick = onClick) // 点击事件
            .padding(16.dp), // 内部内容的内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
        Text(
            text = module.name,
            fontSize = 18.sp, // 字体大小
            fontWeight = FontWeight.Bold, // 字体粗细
            color = MaterialTheme.colorScheme.primary // 字体颜色
        )
    }
}

@Composable
fun FunctionModuleList(functionModules: List<FunctionModule>, modifier: Modifier) {
    LazyColumn(modifier=modifier) {
        items(functionModules) { module ->
            if (module.enabled)
                FunctionModuleItem(module)
        }
    }
}

@Preview
@Composable
fun PreviewFunctionModuleList() {
    val mock = functionModules
    mock[1].enabled = false
    FunctionModuleList(
        functionModules = mock,
        modifier = Modifier.padding(10.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController) {
    TopAppBar(
        modifier = Modifier.background(Color.Blue),
        title = { Text("移动应用开发") },
        actions = {
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(Icons.Filled.Settings, contentDescription = "设置")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { HomeTopBar(navController) },
        content = {innerPadding ->
            FunctionModuleList(functionModules, Modifier.padding(innerPadding))
        }
    )
}

@Composable
fun FunctionModuleSettingsItem(module: FunctionModule) {
    val onCheckedChange = { isChecked: Boolean ->
        functionModules = functionModules.map {
            if (it.name == module.name) it.copy(enabled = isChecked) else it
        }
    }
    Row(modifier = Modifier.padding(16.dp)) {
        Text(
            text = module.name,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Switch(
            checked = module.enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun FunctionModuleSettingsList(functionModules: List<FunctionModule>, modifier: Modifier) {
    LazyColumn(modifier=modifier) {
        items(functionModules) { module ->
            FunctionModuleSettingsItem(module=module)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    TopAppBar(
        modifier = Modifier.background(Color.Blue),
        title = { Text("功能设置") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = { SettingsTopBar() }
    ) { innerPadding ->
        FunctionModuleSettingsList(functionModules = functionModules, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("settings") { SettingsScreen() }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                MyApp()
            }
        }
    }
}

