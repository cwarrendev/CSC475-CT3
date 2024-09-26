package com.example.todoapp

data class ToDoItem(
    val id: Long = 0,
    val task: String,
    val completed: Boolean = false
)