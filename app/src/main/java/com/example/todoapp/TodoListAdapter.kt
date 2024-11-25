package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoListAdapter(
    private val todoList: MutableList<Todo>,
    private val onActionClick: (Todo, String) -> Unit
) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    // ViewHolder class that defines each item view
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle: TextView = itemView.findViewById(R.id.todoTitle)
        val timerButton: ImageButton = itemView.findViewById(R.id.timerButton)
        val updateButton: ImageButton = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_list_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.todoTitle.text = todo.title

        // Timer button click listener
        holder.timerButton.setOnClickListener {
            onActionClick(todo, "timer")
        }

        // Set click listener for the update button
        holder.updateButton.setOnClickListener {
            onActionClick(todo, "update")
        }

        // Set click listener for the delete button
        holder.deleteButton.setOnClickListener {
            onActionClick(todo, "delete")
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}
