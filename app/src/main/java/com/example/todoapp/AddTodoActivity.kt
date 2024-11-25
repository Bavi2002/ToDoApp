package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddTodoActivity : AppCompatActivity() {

    private lateinit var todoTitle: EditText
    private val sharedPrefs by lazy { getSharedPreferences("todos", Context.MODE_PRIVATE) }
    private val gson by lazy { Gson() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        todoTitle = findViewById(R.id.todoTitle)

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val title = todoTitle.text.toString().trim()

            if (title.isNotEmpty()) {
                val newTodo = Todo(title = title)
                saveTodoInList(newTodo)

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                todoTitle.error = "Task title cannot be empty"
            }
        }
    }

    private fun saveTodoInList(newTodo: Todo) {
        val todosJson = sharedPrefs.getString("todo_list", null)
        val todoList: MutableList<Todo> = if (todosJson != null) {
            val type = object : TypeToken<MutableList<Todo>>() {}.type
            gson.fromJson(todosJson, type)
        } else {
            mutableListOf()
        }

        todoList.add(newTodo)

        val editor = sharedPrefs.edit()
        editor.putString("todo_list", gson.toJson(todoList))
        editor.apply()
    }
}
