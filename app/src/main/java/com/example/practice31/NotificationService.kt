package com.example.practice31// NotificationService.kt
import com.example.practice31.NotificationService.NotificationActions.ACTION_CUSTOM
import com.example.practice31.NotificationService.NotificationActions.ACTION_DELETE
import com.example.practice31.NotificationService.NotificationActions.ACTION_SNOOZE
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SNOOZE -> {
                // Обработка команды "Отложить"
                Toast.makeText(this, "Оповещение отложено", Toast.LENGTH_SHORT).show()
                // Здесь можно добавить логику откладывания
            }
            ACTION_DELETE -> {
                // Обработка команды "Удалить"
                Toast.makeText(this, "Оповещение удалено", Toast.LENGTH_SHORT).show()
            }
            ACTION_CUSTOM -> {
                // Обработка пользовательской команды
                val data = intent.getStringExtra("data")
                Toast.makeText(this, "Выполнено: $data", Toast.LENGTH_SHORT).show()
            }
        }

        return START_NOT_STICKY
    }

    object NotificationActions {
        const val ACTION_SNOOZE = "action_snooze"
        const val ACTION_DELETE = "action_delete"
        const val ACTION_CUSTOM = "action_custom"
    }
}