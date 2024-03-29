package com.example.finalproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


data class Todo(
    val title: String,
    val category: String,
    val datetime: String,
    val alarmType: String = "none",
)

class AlarmReceiver(private val prompt: String = "Alarm! Wake up! Wake up!") : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, prompt, Toast.LENGTH_LONG).show()
    }
}

private fun setSingleAlarm(context: Context, triggerTimeInMillis: Long) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent)
}

fun setRepeatingAlarm(context: Context, triggerTimeInMillis: Long, intervalMillis: Long) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, intervalMillis, pendingIntent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItem(
    todo: Todo,
    onEdit: (Todo) -> Unit, // 点击事件处理
    onDelete: (Todo) -> Unit // 长按事件处理
) {    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onEdit(todo) },
                onLongClick = { onDelete(todo) },
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Category",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = todo.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Time Icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = todo.datetime,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    todoList: List<Todo>,
    setFilter: (String?) -> Unit
) {
    TopAppBar(
        title = { Text("待办清单") },
        actions = {
            var showFilterSelection by remember { mutableStateOf("") }
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
                        showFilterSelection = ""
                        setFilter(null)
                    },
                    text = { Text("全部代办") },
                )
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        showFilterSelection = "按类别"
                    },
                    text = { Text("按类别") }
                )
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        showFilterSelection = "按日期"
                    },
                    text = { Text("按日期") },
                )
            }

            if (showFilterSelection.isNotEmpty()) {
                val filterList = todoList.map { if (showFilterSelection == "按类别") it.category else it.datetime }
                var selectedFilter by remember { mutableStateOf<String?>(null) }
                AlertDialog(
                    onDismissRequest = { showFilterSelection = "" },
                    title = { Text("选择过滤器") },
                    text = {
                        Column {
                            filterList.toSet().forEach { filter ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedFilter = filter
                                            setFilter(filter)
                                            showFilterSelection = ""
                                        }
                                ) {
                                    RadioButton(
                                        selected = filter == selectedFilter,
                                        onClick = { /* 这里不做处理，防止重复触发 */ }
                                    )
                                    Text(text = filter)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showFilterSelection = "" }) {
                            Text("取消")
                        }
                    }
                )

            }
        }
    )
}

@Composable
fun TodoList(
    todos: List<Todo>,
    setTodos: (List<Todo>) -> Unit,
    filter: String?,
    modifier: Modifier,
    navController: NavController,
) {

    var showDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    val showDeleteConfirmation: (Todo) -> Unit = { todo ->
        todoToDelete = todo
        showDialog = true
    }

    val onEdit: (Todo) -> Unit = { todo ->
        navController.navigate("EditTodo/${todos.indexOf(todo)}")
    }

    if (showDialog and (todoToDelete != null)) {
        AlertDialog(
            onDismissRequest = { showDialog = false; todoToDelete = null },
            confirmButton = {
                val onClick = {
                    setTodos(todos.filter { todo: Todo -> todo != todoToDelete })
                    showDialog = false; todoToDelete = null
                }
                Button(onClick = onClick) { Text("确定") }
            },
            title = { Text(text = "删除待办事项") },
            text = { Text(text = "确定要删除这个待办事项吗：${todoToDelete?.title}?") },
        )
    }

    LazyColumn(
        modifier = modifier
    ) {
        items(todos.filter { todo: Todo ->
            when (filter) {
                null -> true
                else -> todo.datetime == filter || todo.category == filter
            }
        }) { todo ->
            TodoItem(
                todo = todo,
                onEdit = onEdit,
                onDelete = showDeleteConfirmation
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(todos: List<Todo>, setTodos: (List<Todo>) -> Unit, navController: NavController) {
    var filter by remember { mutableStateOf<String?>(null) }

    val navigateToAddTodo = { navController.navigate("NewTodo") }

    Scaffold(
        topBar = { TopBar(todoList = todos, setFilter = { filter = it }) },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAddTodo) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Todo")
            }
        }
    ) {innerPadding ->
        TodoList(
            todos = todos,
            setTodos = setTodos,
            filter = filter,
            modifier = Modifier.padding(innerPadding),
            navController = navController,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(setTodos: (List<Todo>) -> Unit, todoList: List<Todo>, idx: Int? = null, navController: NavController, context: Context) {

    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var title by remember { mutableStateOf(if (idx == null) "" else todoList[idx].title) }
    var category by remember { mutableStateOf(if (idx == null) "" else todoList[idx].category) }
    var alarmType by remember { mutableStateOf("none") }
    var datetime by remember {
        mutableStateOf(
            if (idx == null) {
                LocalDateTime.now()
            } else {
                LocalDateTime.parse(todoList[idx].datetime, datetimeFormatter)
            }
        )
    }
    var handleAlarm by remember { mutableStateOf(false) }

    var handlePickDatetime by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    Scaffold(topBar = { TopAppBar(title = { Text("编辑待办") }) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("类别") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Time Icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = datetime.format(datetimeFormatter),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { handlePickDatetime = 2 }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (alarmType) {
                        "none" -> "无提醒"
                        "single" -> "单次提醒"
                        "repeating" -> "重复提醒"
                        else -> "无提醒"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { handleAlarm = true }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val newTodo = Todo(
                        title = title,
                        category = category,
                        datetime = datetimeFormatter.format(datetime),
                        alarmType = alarmType,
                    )
                    val milli = datetime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli()
                    println(milli)
                    when (alarmType) {
                        "single" -> {
                            setSingleAlarm(context, milli)
                        }
                        "repeating" -> {
                            setRepeatingAlarm(context, milli, 1000 * 60 * 60) // 一小时一响
                        }
                    }
                    if (idx == null) {
                        setTodos(todoList + newTodo)
                    } else {
                        setTodos(todoList.map { x -> if (x == todoList[idx]) newTodo else x })
                    }
                    navController.navigate("TodoHome")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("保存", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }


    if (handlePickDatetime == 2) {
        val datePickerState = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { handlePickDatetime = 0 },
            confirmButton = {
                val onClick = {
                    if (datePickerState.selectedDateMillis == null) {
                        Toast.makeText(context, "请选择日期", Toast.LENGTH_SHORT).show()
                    } else {
                        datetime = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime()
                        handlePickDatetime = 1
                    }
                }
                Button(onClick = onClick) { Text("确定") }
            },
            text = {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
    }
    if (handlePickDatetime == 1) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { handlePickDatetime = 0 },
            confirmButton = {
                Button(onClick = {
                    datetime = datetime
                        .plusHours(timePickerState.hour.toLong())
                        .plusMinutes(timePickerState.minute.toLong())
                    handlePickDatetime = 0
                }) { Text("确定") }
            },
            text = {
                TimePicker(state = timePickerState, modifier = Modifier.padding(16.dp))
            }
        )
    }
    if (handleAlarm) {
        val alarmTypeMap = mapOf(
            Pair("none", "无提醒"),
            Pair("single", "单次提醒"),
            Pair("repeating", "重复提醒"),
        )
        AlertDialog(
            onDismissRequest = { handleAlarm = false },
            confirmButton = {
                TextButton(
                    onClick = { handleAlarm = false },
                ) { Text(text = "确认") }
            },
            text = {
                Column {
                    alarmTypeMap.forEach { (type, prompt) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { alarmType = type }
                        ) {
                            RadioButton(
                                selected = alarmType == type,
                                onClick = { /* 这里不做处理，防止重复触发 */ }
                            )
                            Text(text = prompt)
                        }
                    }
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoApp(context: Context) {
    val todos = remember { mutableStateListOf<Todo>(
        Todo("安卓应用开发", "作业", "2024-01-03 18:30"),
        Todo("jyyLab", "LAB", "2024-02-09 14:43"),
        Todo("CS144", "LAB", "2024-01-03 18:30"),
    ) }
    val setTodos = { todoList: List<Todo> ->
        todos.clear()
        todos.addAll(todoList)
        Unit
    }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "TodoHome"
    ) {
        composable("TodoHome") { TodoScreen(todos = todos, setTodos = setTodos, navController = navController) }
        composable("NewTodo") { EditTodoScreen(setTodos = setTodos, todoList = todos, navController = navController, context = context) }
        composable("EditTodo/{todoId}") { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toInt()
            EditTodoScreen(setTodos = setTodos, todoList = todos, idx = todoId, navController = navController, context = context)
        }
    }
}

class TodoActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoApp(this)
        }
    }
}
