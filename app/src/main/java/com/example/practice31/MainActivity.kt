package com.example.practice31// MainActivity.kt
import NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission

class MainActivity : ComponentActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = NotificationManager(this)
        requestNotificationPermission()

        setContent {
            NotificationApp(notificationManager)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationApp(notificationManager: NotificationManager) {
    val context = LocalContext.current
    var showPermissionWarning by remember {
        mutableStateOf(!notificationManager.areNotificationsEnabled())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление уведомлениями") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showPermissionWarning) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Разрешите уведомления в настройках приложения",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 1. Базовое уведомление
            Button(
                onClick = @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS) { notificationManager.showBasicNotification() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Базовое уведомление")
            }

            // 2. Уведомление, открывающее приложение
            Button(
                onClick = @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS) { notificationManager.showNotificationThatOpensApp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Уведомление для открытия приложения")
            }

            // 3. Уведомление с командами
            Button(
                onClick = @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS) { notificationManager.showNotificationWithCommand() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Уведомление с командами")
            }

            // Разделитель для разных каналов
            Text(
                text = "Разные каналы уведомлений:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 4. Уведомления с разными каналами
            Button(
                onClick = @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS) {
                    notificationManager.showChannelSpecificNotification(
                        NotificationManager.CHANNEL_DEFAULT
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text("Обычный канал")
            }

            Button(
                onClick = @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS) {
                    notificationManager.showChannelSpecificNotification(
                        NotificationManager.CHANNEL_HIGH_PRIORITY
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text("Важный канал")
            }

            Button(
                onClick = {
                    notificationManager.showChannelSpecificNotification(
                        NotificationManager.CHANNEL_LOW_PRIORITY
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("Фоновый канал")
            }

            // 5. Уведомление на экране блокировки
            Button(
                onClick = { notificationManager.showLockscreenNotification() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text("Уведомление на блокировке")
            }

            // Кнопка для проверки разрешений
            Button(
                onClick = {
                    showPermissionWarning = !notificationManager.areNotificationsEnabled()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Проверить разрешения")
            }
        }
    }
}