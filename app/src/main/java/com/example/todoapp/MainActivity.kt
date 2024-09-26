package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import com.example.todoapp.ui.theme.ToDoAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var repository: ToDoRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ToDoRepository(this)
        setContent {
            ToDoAppTheme {
                ToDoApp(repository)
            }
        }
    }
}

@Composable
fun ToDoApp(repository: ToDoRepository) {
    var taskText by remember { mutableStateOf("") }
    var tasks by remember { mutableStateOf(repository.getAllTasks()) }
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Scaffold component for the add task dialog
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Task")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)) {

            // Incomplete Tasks Header
            Text("Incomplete Tasks", style = MaterialTheme.typography.titleLarge)
            tasks.filter { !it.completed }.forEach { task ->
                TaskRow(task, repository) {
                    tasks = repository.getAllTasks()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Task marked as completed")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Completed Tasks Header
            Text("Completed Tasks", style = MaterialTheme.typography.titleLarge)
            tasks.filter { it.completed }.forEach { task ->
                TaskRow(task, repository) {
                    tasks = repository.getAllTasks()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Task marked as incomplete")
                    }
                }
            }
        }
        // Dialog for adding a new task
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Enter Task") },
                text = {
                    BasicTextField(
                        value = taskText,
                        onValueChange = { taskText = it },
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (taskText.isNotEmpty()) {
                            repository.addTask(taskText)
                            taskText = ""
                            tasks = repository.getAllTasks()
                            showDialog = false
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
// TaskRow component to display a single task
fun TaskRow(task: ToDoItem, repository: ToDoRepository, onTaskUpdated: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(task.task)
        Row {
            Checkbox(
                checked = task.completed,
                onCheckedChange = {
                    repository.markTaskCompleted(task.id, it)
                    onTaskUpdated()
                }
            )
            IconButton(onClick = {
                repository.deleteTask(task.id)
                onTaskUpdated()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoAppPreview() {
    ToDoAppTheme {
        ToDoApp(ToDoRepository(LocalContext.current))
    }
}