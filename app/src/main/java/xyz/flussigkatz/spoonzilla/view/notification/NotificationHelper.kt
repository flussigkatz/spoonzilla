package xyz.flussigkatz.searchmovie.view.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.SpoonzillaReceiver
import xyz.flussigkatz.spoonzilla.util.AppConst.DISH_REMINDER_NOTIFICATION_ALARM
import xyz.flussigkatz.spoonzilla.util.AppConst.DISH_REMINDER_NOTIFICATION_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_REMINDER_NOTIFICATION_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.NOTIFICATION_CHANNEL_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.NOTIFICATION_CHANNEL_NAME
import xyz.flussigkatz.spoonzilla.util.AppConst.REMINDER_NOTIFICATION_DISH_ACTION
import xyz.flussigkatz.spoonzilla.view.MainActivity

object NotificationHelper {
    lateinit var notification: Notification.Builder

    fun initNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = NOTIFICATION_CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
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
        val dishAdvancedInfo = bundle.getParcelable<DishAdvancedInfo>(KEY_DISH)
        dishAdvancedInfo?.let {
            val notificationManager = NotificationManagerCompat.from(context)
            val intentDishReminderInit = Intent(context, MainActivity::class.java)
            intentDishReminderInit.action = REMINDER_NOTIFICATION_DISH_ACTION
            intentDishReminderInit.putExtra(KEY_REMINDER_NOTIFICATION_DISH, bundle)
            val pendingIntentInit = PendingIntent.getActivity(
                context,
                it.id,
                intentDishReminderInit,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val title = context.getString(R.string.dish_reminder_title)
            val text = context.getString(R.string.dish_reminder_text) + it.title
            notification.setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntentInit)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_spoonzilla)
            notificationManager.notify(
                DISH_REMINDER_NOTIFICATION_ID,
                notification.build()
            )
        }
    }

    fun setDishRemind(context: Context, dishAdvancedInfo: DishAdvancedInfo, alarmTime: Long) {
        val bundle = Bundle()
        val intentDishReminderAlarm = Intent(
            context,
            SpoonzillaReceiver::class.java
        )
        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        intentDishReminderAlarm.action = DISH_REMINDER_NOTIFICATION_ALARM
        bundle.putParcelable(
            KEY_DISH,
            dishAdvancedInfo
        )
        intentDishReminderAlarm.putExtra(
            KEY_REMINDER_NOTIFICATION_DISH, bundle
        )
        val pendingIntentAlarm = PendingIntent.getBroadcast(
            context,
            dishAdvancedInfo.id,
            intentDishReminderAlarm,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(
            AlarmManager.RTC,
            alarmTime,
            pendingIntentAlarm
        )
    }

}