package xyz.flussigkatz.spoonzilla

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.AppConst.DISH_REMINDER_ALARM
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_REMINDER_NOTIFICATION_DISH
import xyz.flussigkatz.spoonzilla.view.notification.NotificationHelper
import javax.inject.Inject

class SpoonzillaReceiver : BroadcastReceiver() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "android.intent.action.BOOT_COMPLETED" -> initDishReminds(context)
            DISH_REMINDER_ALARM -> {
                val bundle = intent.getBundleExtra(KEY_REMINDER_NOTIFICATION_DISH)
                bundle?.let { NotificationHelper.createDishReminderNotification(context, it) }
            }
        }
    }

    private fun initDishReminds(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val dishAlarmList = interactor.getDishAlarmsToListFromDb()
            dishAlarmList.forEach { dishAlarm ->
                interactor.getSingleCashedAdvancedInfoDishFromDb(dishAlarm.id).let {
                    NotificationHelper.setDishRemind(
                        context,
                        it,
                        dishAlarm.alarmTime,
                        dishAlarm.localId
                    )
                }
            }
            this.cancel()
        }
    }
}