package com.example.finalproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finalproject.ui.theme.FinalProjectTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Expense(
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopBar(
    expenses: List<Expense>,
    setFilter: (datetime: LocalDateTime?, category: String?) -> Unit
) {
    var selectFilter by remember { mutableStateOf(false) }
    var tagFilter by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("记账") },
        actions = {
            IconButton(onClick = { selectFilter = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        }
    )
    if (selectFilter) {
        val selectDatetime = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { selectFilter = false },
            title = { Text("选择统计开始日") },
            text = {
                Column {
                    DatePicker(
                        state = selectDatetime
                    )
                    TextButton(onClick = { selectFilter = false; tagFilter = true }) {
                        Text("使用分类进行筛选")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectFilter = false
                        if (selectDatetime.selectedDateMillis != null) {
                            val datetime = Instant
                                .ofEpochMilli(selectDatetime.selectedDateMillis!!)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                            setFilter(datetime, null)
                        }
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
    if (tagFilter) {
        AlertDialog(
            onDismissRequest = { tagFilter = false },
            title = { Text("选择使用的统计标签") },
            text = {
                Column {
                    LazyColumn {
                        val category = expenses.map { x -> x.category }.toSet()
                        items(category.toList()) { x ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        setFilter(null, x)
                                    }
                            ) {
                                Text(text = x)
                            }
                        }
                    }
                    TextButton(onClick = { tagFilter = false; selectFilter = true }) {
                        Text("使用日期进行筛选")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        tagFilter = false
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseListScreen(expenses: List<Expense>) {
    val filteredExpenses = expenses // 初始设为所有支出

    var filterCategory by remember { mutableStateOf<String?>(null) }
    var filterDatetime by remember { mutableStateOf<LocalDateTime>(LocalDateTime.now()) }
    val setFilter = { datetime: LocalDateTime?, category: String? ->
        filterCategory = category
        filterDatetime = datetime ?: filterDatetime
    }

    Scaffold(
        topBar = { AccountTopBar(expenses, setFilter) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column {
                // 显示汇总值
                val dayTotalAmount = filteredExpenses.filter { x ->
                    val dayFormatter = DateTimeFormatter.ofPattern("dd")
                    dayFormatter.format(filterDatetime) == x.date.split('-')[2]
                }.sumOf { x -> x.amount }
                val monthTotalAmount = filteredExpenses.filter { x ->
                    val monthFormatter = DateTimeFormatter.ofPattern("MM")
                    monthFormatter.format(filterDatetime) == x.date.split('-')[1]
                }.sumOf { x -> x.amount }
                val yearTotalAmount = filteredExpenses.filter { x ->
                    val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
                    yearFormatter.format(filterDatetime) == x.date.split('-')[0]
                }.sumOf { x -> x.amount }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text("日总计: $dayTotalAmount")
                    Text("月总计：$monthTotalAmount")
                    Text("年总计：$yearTotalAmount")
                }
            }
            // 显示筛选后的支出清单
            LazyColumn {
                if (filterCategory != null) {
                    items(expenses.filter { x -> x.category == filterCategory })
                    { x ->
                        ExpenseItem(x)
                    }
                } else {
                    items(expenses.filter { x -> x.date == DateTimeFormatter.ofPattern("yyyy-MM-dd").format(filterDatetime) })
                    { x ->
                        ExpenseItem(x)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseItem(expense: Expense) {
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text("Amount: ${expense.amount}")
        Text("Category: ${expense.category}")
        Text("Date: ${expense.date.format(dateFormat)}")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewAccount() {
    val expenses = listOf(
        Expense("餐饮", 20.0, "2021-10-01", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-02", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-03", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-04", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-05", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-06", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-07", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-08", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-09", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-10", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-11", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-12", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-13", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-14", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-15", "餐饮"),
        Expense("餐饮", 20.0, "2021-10-16", "餐饮"),
        Expense("餐饮", 20.0, "2024-01-05", "餐饮"),
    )
    ExpenseListScreen(expenses)
}

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {

            }
        }
    }
}