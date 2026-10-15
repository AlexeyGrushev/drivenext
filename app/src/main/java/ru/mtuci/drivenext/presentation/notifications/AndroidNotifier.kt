package ru.mtuci.drivenext.presentation.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.time.LocalDate
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.domain.repository.AppSettingsRepository
import ru.mtuci.drivenext.domain.repository.Notifier
import ru.mtuci.drivenext.presentation.MainActivity
import ru.mtuci.drivenext.presentation.common.formatDate

/** Уведомления через NotificationManager. Канал создаётся один раз при запуске приложения. */
class AndroidNotifier(
    private val context: Context,
    private val settings: AppSettingsRepository,
) : Notifier {

    init {
        // Канал нужен на Android 8+; пользователь может настроить его и в системных настройках
        val channel = NotificationChannel(
            CHANNEL_BOOKINGS,
            context.getString(R.string.notification_channel_bookings),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun hasPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    override fun bookingCreated(carTitle: String, start: LocalDate, end: LocalDate) {
        if (!settings.bookingNotifications) return
        val res = context.resources
        show(
            id = ID_BOOKING,
            title = context.getString(R.string.notification_booking_created_title),
            text = context.getString(
                R.string.notification_booking_created_text, carTitle, res.formatDate(start), res.formatDate(end),
            ),
        )
    }

    override fun bookingCancelled(carTitle: String) {
        if (!settings.bookingNotifications) return
        show(
            id = ID_BOOKING,
            title = context.getString(R.string.notification_booking_cancelled_title),
            text = context.getString(R.string.notification_booking_cancelled_text, carTitle),
        )
    }

    override fun sendTest() = show(
        id = ID_TEST,
        title = context.getString(R.string.notification_test_title),
        text = context.getString(R.string.notification_test_text),
    )

    private fun show(id: Int, title: String, text: String) {
        if (!settings.notificationsEnabled || !hasPermission()) return
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        // Разрешение проверено выше в hasPermission()
        @Suppress("MissingPermission")
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    private companion object {
        const val CHANNEL_BOOKINGS = "bookings"
        const val ID_BOOKING = 1
        const val ID_TEST = 2
    }
}
