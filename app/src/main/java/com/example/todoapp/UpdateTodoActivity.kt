package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UpdateTodoActivity : AppCompatActivity() {

    private lateinit var todoTitle: EditText
    private lateinit var currentTodo: Todo
    private val sharedPrefs by lazy { getSharedPreferences("todos", Context.MODE_PRIVATE) }
    private val gson by lazy { Gson() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_todo)

        todoTitle = findViewById(R.id.todoTitle)

        // Get the current todo item using the correct method for Parcelable
        currentTodo = intent.getParcelableExtra<Todo>("todo") ?: return

        // Prefill the EditText with the current task title
        todoTitle.setText(currentTodo.title)

        val updateButton: Button = findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            // Get the updated title
            val updatedTitle = todoTitle.text.toString().trim()

            // Ensure the title is not empty
            if (updatedTitle.isNotEmpty()) {
                currentTodo.title = updatedTitle
                updateTodoInList(currentTodo)

                // Redirect back to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                todoTitle.error = "Task title cannot be empty"
            }
        }
    }

    private fun updateTodoInList(updatedTodo: Todo) {
        // Load the todo list from SharedPreferences
        val todosJson = sharedPrefs.getString("todo_list", null)
        val todoList: MutableList<Todo> = if (todosJson != null) {
            val type = object : TypeToken<MutableList<Todo>>() {}.type
            gson.fromJson(todosJson, type)
        } else {
            mutableListOf()
        }

        // Find the index of the todo by its unique ID, not by the title
        val index = todoList.indexOfFirst { it.id == currentTodo.id }

        // If the todo exists in the list, update it
        if (index != -1) {
            todoList[index] = updatedTodo

            // Save the updated list back to SharedPreferences
            val editor = sharedPrefs.edit()
            editor.putString("todo_list", gson.toJson(todoList))
            editor.apply()
        }
    }
}
