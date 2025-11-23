// NotificationManager.kt
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.practice31.MainActivity
import com.example.practice31.NotificationService
import com.example.practice31.R

class NotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_DEFAULT = "default_channel"
        const val CHANNEL_HIGH_PRIORITY = "high_priority_channel"
        const val CHANNEL_LOW_PRIORITY = "low_priority_channel"
        const val CHANNEL_LOCKSCREEN = "lockscreen_channel"

        const val NOTIFICATION_ID_BASIC = 1
        const val NOTIFICATION_ID_OPEN_APP = 2
        const val NOTIFICATION_ID_COMMAND = 3
        const val NOTIFICATION_ID_LOCKSCREEN = 4
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Основной канал
            val defaultChannel = NotificationChannel(
                CHANNEL_DEFAULT,
                "Основные уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для основных уведомлений"
            }

            // Канал с высоким приоритетом
            val highPriorityChannel = NotificationChannel(
                CHANNEL_HIGH_PRIORITY,
                "Важные уведомления",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для важных уведомлений"
                setShowBadge(true)
            }

            // Канал с низким приоритетом
            val lowPriorityChannel = NotificationChannel(
                CHANNEL_LOW_PRIORITY,
                "Фоновые уведомления",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Канал для фоновых уведомлений"
            }

            // Канал для экрана блокировки
            val lockscreenChannel = NotificationChannel(
                CHANNEL_LOCKSCREEN,
                "Уведомления на блокировке",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления, показываемые на экране блокировки"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannels(
                listOf(
                    defaultChannel,
                    highPriorityChannel,
                    lowPriorityChannel,
                    lockscreenChannel
                )
            )
        }
    }

    // 1. Базовое оповещение с иконкой, заголовком и текстом
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showBasicNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_DEFAULT)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setContentTitle("Базовое уведомление")
            .setContentText("Это пример базового уведомления с текстом")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Это расширенный текст уведомления. " +
                            "Он может содержать больше информации, чем обычное уведомление. " +
                            "Пользователь может развернуть его для просмотра полного текста.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASIC, notification)
    }

    // 2. Оповещение, которое открывает приложение
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotificationThatOpensApp() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DEFAULT)
            .setSmallIcon(R.drawable.ic_baseline_open_in_browser_24)
            .setContentTitle("Открыть приложение")
            .setContentText("Нажмите, чтобы открыть приложение")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_baseline_home_24,
                "Открыть",
                pendingIntent
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID_OPEN_APP, notification)
    }

    // 3. Оповещение, которое отправляет команду в сервис
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotificationWithCommand() {
        // Intent для команды "Отложить"
        val snoozeIntent = Intent(context, NotificationService::class.java).apply {
            action = "action_snooze"
        }
        val snoozePendingIntent = PendingIntent.getService(
            context,
            0,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent для команды "Удалить"
        val deleteIntent = Intent(context, NotificationService::class.java).apply {
            action = "action_delete"
        }
        val deletePendingIntent = PendingIntent.getService(
            context,
            0,
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent для пользовательской команды
        val customIntent = Intent(context, NotificationService::class.java).apply {
            action = "action_custom"
            putExtra("data", "Пользовательская команда выполнена!")
        }
        val customPendingIntent = PendingIntent.getService(
            context,
            0,
            customIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HIGH_PRIORITY)
            .setSmallIcon(R.drawable.ic_baseline_settings_24)
            .setContentTitle("Уведомление с командами")
            .setContentText("Используйте действия для управления")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                R.drawable.ic_baseline_snooze_24,
                "Отложить",
                snoozePendingIntent
            )
            .addAction(
                R.drawable.ic_baseline_play_arrow_24,
                "Выполнить",
                customPendingIntent
            )
            .setDeleteIntent(deletePendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_COMMAND, notification)
    }

    // 4. Оповещения с разными каналами
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showChannelSpecificNotification(channelId: String) {
        val title = when (channelId) {
            CHANNEL_HIGH_PRIORITY -> "ВАЖНОЕ уведомление"
            CHANNEL_LOW_PRIORITY -> "Фоновое уведомление"
            else -> "Обычное уведомление"
        }

        val text = when (channelId) {
            CHANNEL_HIGH_PRIORITY -> "Это уведомление с высоким приоритетом"
            CHANNEL_LOW_PRIORITY -> "Это уведомление с низким приоритетом"
            else -> "Это обычное уведомление"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_baseline_tag_24)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // 5. Оповещение на экране блокировки
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showLockscreenNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_LOCKSCREEN)
            .setSmallIcon(R.drawable.ic_baseline_lock_24)
            .setContentTitle("Уведомление на блокировке")
            .setContentText("Это уведомление видно на экране блокировки")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(null, true) // Может отображаться как полноэкранное
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_LOCKSCREEN, notification)
    }

    // Проверка разрешений
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }
}