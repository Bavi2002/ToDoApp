package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {
    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var todoList: MutableList<Todo>
    private val sharedPrefs by lazy { getSharedPreferences("todos", Context.MODE_PRIVATE) }
    private val gson by lazy { Gson() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        todoList = loadTodos()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        todoListAdapter = TodoListAdapter(todoList) { todo, action ->
            when (action) {
                "update" -> navigateToUpdate(todo)
                "delete" -> deleteTodoItem(todo)
                "timer" -> navigateToSetTimer(todo)
            }
        }
        recyclerView.adapter = todoListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java))
        }
    }

    private fun loadTodos(): MutableList<Todo> {
        val todosJson = sharedPrefs.getString("todo_list", null)
        return if (todosJson != null) {
            val type = object : TypeToken<MutableList<Todo>>() {}.type
            gson.fromJson(todosJson, type)
        } else {
            mutableListOf()
        }
    }

    private fun deleteTodoItem(todo: Todo) {
        todoList.remove(todo)
        todoListAdapter.notifyDataSetChanged()
        saveTodos()
    }

    private fun saveTodos() {
        val editor = sharedPrefs.edit()
        val todosJson = gson.toJson(todoList)
        editor.putString("todo_list", todosJson)
        editor.apply()
    }

    private fun navigateToUpdate(todo: Todo) {
        val intent = Intent(this, UpdateTodoActivity::class.java)
        intent.putExtra("todo", todo)
        startActivity(intent)
    }

    private fun navigateToSetTimer(todo: Todo) {
        val intent = Intent(this, SetTimerActivity::class.java)
        intent.putExtra("todo", todo)
        startActivity(intent)
    }
}
