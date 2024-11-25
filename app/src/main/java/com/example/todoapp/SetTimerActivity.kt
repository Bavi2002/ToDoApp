package com.example.todoapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class SetTimerActivity : AppCompatActivity() {

    private lateinit var timerInput: EditText
    private lateinit var startTimerButton: Button
    private lateinit var currentTodo: Todo
    private val channelId = "todoTimerChannel"
    private val postNotificationPermissionRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_timer)

        timerInput = findViewById(R.id.timerInput)
        startTimerButton = findViewById(R.id.startTimerButton)

        // Fetch the 'Todo' object safely based on the Android version
        currentTodo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("todo", Todo::class.java)
        } else {
            intent.getParcelableExtra("todo")
        } ?: return  // Return early if 'todo' is null

        // Check for notification permission (for API 33 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), postNotificationPermissionRequestCode)
            }
        }

        // Create the notification channel
        createNotificationChannel()

        // Handle button click for starting the timer and sending notification
        startTimerButton.setOnClickListener {
            val timeInSeconds = timerInput.text.toString().toLongOrNull() ?: 0L

            if (timeInSeconds > 0) {
                startTimer(timeInSeconds * 1000)

                // Immediately navigate to HomeActivity
                redirectToHome()
            } else {
                timerInput.error = "Please enter a valid time"
            }
        }
    }

    private fun startTimer(duration: Long) {
        object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Optionally update the UI with remaining time
            }

            override fun onFinish() {
                // When timer finishes, send notification with the to-do title
                sendNotification(currentTodo.title)

                // Redirect to HomeActivity after timer finishes
                redirectToHome()
            }
        }.start()
    }

    private fun sendNotification(todoTitle: String) {
        val intent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Timer Finished")
            .setContentText("Timer is over for task: $todoTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(1, notification)  // Show the notification
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Todo Timer Channel"
            val descriptionText = "Channel for Todo Timer notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@SetTimerActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == postNotificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for notifications
            } else {
                // Permission denied, handle accordingly
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
