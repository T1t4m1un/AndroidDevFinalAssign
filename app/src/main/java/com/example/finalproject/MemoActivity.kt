package com.example.finalproject

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.theme.FinalProjectTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
fun MemoItem(memo: Memo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
}

@Composable
fun MemoList(memoList: List<Memo>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(memoList) {memo ->
            MemoItem(memo)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val onSortSelected = { sortType: String ->
        memoList = when (sortType) {
            "time" -> memoList.sortedBy { it.creationTime }
            "title" -> memoList.sortedBy { it.title }
            else -> memoList
        }
    }

    Scaffold(
        topBar = { MemoTopBar(onSortSelected) }
    ) {innerPadding ->
        if (memoList.isEmpty()) {
            DefaultText(modifier = Modifier.padding(innerPadding))
        } else {
            MemoList(memoList = memoList, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Preview
@Composable
fun PreviewMemoScreen() {
    HomeScreen()
}

class MemoActivity : ComponentActivity() {
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
                    Text("Android")
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