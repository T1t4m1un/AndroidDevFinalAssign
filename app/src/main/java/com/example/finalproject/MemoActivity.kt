package com.example.finalproject

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.ui.theme.FinalProjectTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class Memo(val title: String, val creationTime: String, val content: String)

var memoList by mutableStateOf(
    listOf(
        Memo("购物清单", "2023-04-01", "牛奶、面包、鸡蛋"),
        Memo("会议提醒", "2023-04-02", "客户会议于下午 3:00"),
        Memo("旅行计划", "2023-04-03", "周末去海边度假"),
        Memo("读书笔记", "2023-04-04", "《Android开发艺术探索》第5章"),
        Memo("运动计划", "2023-04-05", "每周三、五去健身房锻炼"),
        Memo("Memo 1", "2022-01-14", "Content for memo 1"),
        Memo("Memo 2", "2022-02-21", "Content for memo 2"),
        Memo("Memo 3", "2022-07-27", "Content for memo 3"),
        Memo("Memo 4", "2022-02-14", "Content for memo 4"),
        Memo("Memo 5", "2022-09-02", "Content for memo 5"),
        Memo("Memo 6", "2022-01-02", "Content for memo 6"),
        Memo("Memo 7", "2022-04-02", "Content for memo 7"),
        Memo("Memo 8", "2022-06-09", "Content for memo 8"),
        Memo("Memo 9", "2022-02-04", "Content for memo 9"),
        Memo("Memo 10", "2022-02-19", "Content for memo 10"),
    )
)

@Composable
fun MemoItem(memo: Memo, idx: Int, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showDialog = true },
                    onTap = { navController.navigate("MemoEdit/${idx}") }
                )
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = memo.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = memo.creationTime,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = memo.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("删除备忘录") },
            text = { Text("确定要删除这条备忘录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val tmpList = memoList.toMutableList()
                        tmpList.removeAt(idx)
                        memoList = tmpList
                        showDialog = false
                    }
                ) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
fun MemoList(memoList: List<Memo>, modifier: Modifier, navController: NavController) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(memoList) { idx, memo ->
            MemoItem(memo, idx, navController)
        }
    }
}

@Composable
fun DefaultText(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // 内部内容的内边距
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            fontSize = 18.sp,
            text = "还没有备忘录噢",
            fontWeight = FontWeight.Bold, // 字体粗细
            color = MaterialTheme.colorScheme.primary // 字体颜色
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoTopBar(onSortSelected: (String) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier.background(Color.Blue),
        title = { Text(text = "备忘录") },
        actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onSortSelected("time")
                        showMenu = false
                    },
                    text = { Text("按时间排序") })
                DropdownMenuItem(
                    onClick = {
                        onSortSelected("title")
                        showMenu = false
                    },
                    text = { Text("按标题排序") })
            }
        }
    )
}

@Composable
fun MemoFloatingButton(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate("MemoEdit") },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(Icons.Filled.Add, contentDescription = "添加备忘")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoScreen(navController: NavController) {
    val onSortSelected = { sortType: String ->
        memoList = when (sortType) {
            "time" -> memoList.sortedBy { it.creationTime }
            "title" -> memoList.sortedBy { it.title }
            else -> memoList
        }
    }

    Scaffold(
        topBar = { MemoTopBar(onSortSelected) },
        floatingActionButton = { MemoFloatingButton(navController) }
    ) {innerPadding ->
        if (memoList.isEmpty()) {
            DefaultText(modifier = Modifier.padding(innerPadding))
        } else {
            MemoList(memoList = memoList, modifier = Modifier.padding(innerPadding), navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoEditScreen(idx: Int? = null, navController: NavController) {
    val memo = if (idx != null) memoList[idx] else null
    var title by remember { mutableStateOf(memo?.title ?: "") }
    var content by remember { mutableStateOf(memo?.content ?: "") }

    val onSave = {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-DD HH:MM:SS")
        val datetime = formatter.format(current)

        val tmpList = memoList.toMutableList()
        if (idx == null) {
            val newMemo = Memo(title, datetime, content)
            tmpList.add(newMemo)
        } else {
            tmpList[idx] = Memo(title, datetime, content)
        }
        memoList = tmpList
        navController.navigate("Memo")
    }

    val onCancel = {
        navController.navigate("Memo")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑备忘录") },
                actions = {
                    TextButton(onClick = onSave) {
                        Text("确认")
                    }
                    TextButton(onClick = onCancel) {
                        Text("取消")
                    }
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(innerPadding)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("内容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = 10
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MemoApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "Memo") {
        composable("Memo") { MemoScreen(navController) }
        composable("MemoEdit") { MemoEditScreen(navController = navController) }
        composable("MemoEdit/{MemoId}") { backStackEntry ->
            println(backStackEntry.arguments?.getString("MemoId"))
            val memoId = backStackEntry.arguments?.getString("MemoId")?.toInt()
            MemoEditScreen(memoId, navController)
        }
    }
}


@Preview
@Composable
fun PreviewMemoScreen() {
    val navController = rememberNavController()
    HomeScreen(navController)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewMemoEditScreen() {
    val navController = rememberNavController()
    MemoEditScreen(navController = navController)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewMemoApp() {
    MemoApp()
}

class MemoActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoList = loadMemo()

        setContent {
            FinalProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MemoApp()
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        saveMemo(memoList)
    }
    private fun loadMemo(): List<Memo> {
        val sharedPreferences = getSharedPreferences("MemoActivity", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("memoList", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<Memo>>() {}.type)
        } else {
            emptyList()
        }
    }

    private fun saveMemo(memoList: List<Memo>) {
        val sharedPreferences = getSharedPreferences("MemoActivity", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(memoList)
        editor.putString("memoList", json)
        editor.apply()
    }
}