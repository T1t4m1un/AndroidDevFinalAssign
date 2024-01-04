package com.example.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finalproject.ui.theme.FinalProjectTheme
import java.time.LocalDateTime

data class Todo(
    val title: String,
    val category: String,
    val datetime: String,
)

@Composable
fun TodoItem(todo: Todo) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(todo.title)
            Text(todo.category)
            Text(todo.datetime)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onFilterSelected: (String) -> Unit) {
    TopAppBar(
        title = { Text("待办清单") },
        actions = {
            var expanded by remember { mutableStateOf(false) }

            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onFilterSelected("全部")
                    },
                    text = { Text("全部代办") },
                )
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onFilterSelected("按类别")
                    },
                    text = { Text("按类别") }
                )
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onFilterSelected("按日期")
                    },
                    text = { Text("按日期") },
                )
            }
        }
    )
}

@Composable
fun TodoList(todos: List<Todo>, filter: String?, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(todos.filter { todo: Todo ->
            when (filter) {
                null -> true
                else -> todo.datetime == filter || todo.category == filter
            }
        }) { todo ->
            TodoItem(todo)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen() {
    var todos = remember { mutableStateListOf<Todo>(
        Todo("安卓应用开发", "作业", "2024-01-03 18:30:00")
    ) }
    var filter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopBar(onFilterSelected = { filter = it }) }
    ) {innerPadding ->
        TodoList(todos = todos, filter = filter, modifier = Modifier.padding(innerPadding))
    }
}

@Preview
@Composable
fun Preview() {
    TodoScreen()
}

class TodoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting3("Android")
                }
            }
        }
    }
}