package se.eoslund.piggest.services

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.eoslund.piggest.R
import se.eoslund.piggest.controller.App
import se.eoslund.piggest.controller.MainActivity

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.notification != null) {
            message.notification?.let {
                showNotification(it)

                Log.d("FirebaseMessagingService", "Notification received: ${it.body}")
            }
        }
    }

    override fun onNewToken(p0: String) {
        Log.e("TOKEN", App.instance.getString(R.string.msg_token_fmt, p0))
        super.onNewToken(p0)
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bitmapEosIcon = BitmapFactory.decodeResource(resources, R.drawable.eos_logo)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.eos_logo)
            .setLargeIcon(bitmapEosIcon)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setChannelId(channelId)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notificationBuilder.build())
    }




}
