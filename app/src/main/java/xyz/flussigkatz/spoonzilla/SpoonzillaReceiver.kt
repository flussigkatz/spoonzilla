package xyz.flussigkatz.spoonzilla

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_REMINDER_NOTIFICATION_DISH

class SpoonzillaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(KEY_REMINDER_NOTIFICATION_DISH)
        bundle?.let { NotificationHelper.createDishReminderNotification(context, it) }
    }
}