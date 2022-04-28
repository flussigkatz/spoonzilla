package xyz.flussigkatz.spoonzilla.view.notification

import android.app.*
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.core.app.NotificationManagerCompat
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.SpoonzillaReceiver
import xyz.flussigkatz.spoonzilla.util.AppConst.DISH_REMINDER_ALARM
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_LOCAL_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_REMINDER_NOTIFICATION_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.NOTIFICATION_CHANNEL_NAME
import xyz.flussigkatz.spoonzilla.util.AppConst.REMINDER_NOTIFICATION
import xyz.flussigkatz.spoonzilla.view.MainActivity

object NotificationHelper {
    private const val NOTIFICATION_CHANNEL_ID = "notification_channel_1"
    private lateinit var notification: Notification.Builder

    fun initNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = NOTIFICATION_CHANNEL_NAME
            val importance = IMPORTANCE_DEFAULT
            val nChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                importance
            )
            notificationManager.createNotificationChannel(nChannel)
            notification = Notification.Builder(
                context,
                NOTIFICATION_CHANNEL_ID
            )
        } else {
            @Suppress("DEPRECATION")
            notification = Notification.Builder(context)
        }
    }

    fun createDishReminderNotification(context: Context, bundle: Bundle) {
        val notificationManager = NotificationManagerCompat.from(context)
        val dishAdvancedInfo = bundle.getParcelable<DishAdvancedInfo>(KEY_DISH)
        val localId = bundle.getInt(KEY_DISH_LOCAL_ID)
        dishAdvancedInfo?.let {
            val initIntentDishReminder = Intent(context, MainActivity::class.java)
            initIntentDishReminder.action = REMINDER_NOTIFICATION
            initIntentDishReminder.putExtra(KEY_REMINDER_NOTIFICATION_DISH, bundle)
            val initPendingIntent = getActivity(
                context,
                localId,
                initIntentDishReminder,
                FLAG_UPDATE_CURRENT
            )
            val title = context.getString(R.string.dish_reminder_title)
            val text = context.getString(R.string.dish_reminder_text) + it.title
            notification.setContentTitle(title)
                .setContentText(text)
                .setContentIntent(initPendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_spoonzilla)
            notificationManager.notify(
                localId,
                notification.build()
            )
        }
    }

    fun setDishRemind(
        context: Context,
        dishAdvancedInfo: DishAdvancedInfo,
        alarmTime: Long,
        localId: Int
    ) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val bundle = Bundle()
        val alarmDishReminderIntent = Intent(context, SpoonzillaReceiver::class.java)
        alarmDishReminderIntent.action = DISH_REMINDER_ALARM
        bundle.putParcelable(KEY_DISH, dishAdvancedInfo)
        bundle.putInt(KEY_DISH_LOCAL_ID, localId)
        alarmDishReminderIntent.putExtra(KEY_REMINDER_NOTIFICATION_DISH, bundle)
        val alarmPendingIntent = getBroadcast(
            context,
            localId,
            alarmDishReminderIntent,
            FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC, alarmTime, alarmPendingIntent)
    }

    fun cancelDishRemind(context: Context, localId: Int) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmDishReminderIntent = Intent(context, SpoonzillaReceiver::class.java)
        alarmDishReminderIntent.action = DISH_REMINDER_ALARM
        val alarmPendingIntent = getBroadcast(
            context,
            localId,
            alarmDishReminderIntent,
            FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(alarmPendingIntent)
    }
}