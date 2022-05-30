package com.sample.factophile

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun viewToImage(view: View, @ColorInt color: Int): Bitmap {
    val returnedBitmap: Bitmap =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable: Drawable? = view.background
    if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(color)
    view.draw(canvas)
    return returnedBitmap
}

fun createDir(context: Context) {
    val dir = File(context.filesDir,"images")
    if (!dir.exists()) dir.mkdir()
}

fun Bitmap.getBitmapUri(context: Context): Uri {
    val filename = UUID.randomUUID().toString()
    createDir(context)
    val file = File.createTempFile(filename,".jpg", File(context.filesDir,"images"))
    val fOut = FileOutputStream(file)
    this.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
    fOut.flush()
    fOut.close()
    return FileProvider.getUriForFile(context,context.applicationContext.packageName+ ".fileprovider",file)
}

const val CHANNEL_ID = "random-fact"
const val CHANNEL_NAME = "Random Fact"

fun Context.createNotificationChannel() {
    val nm = ContextCompat.getSystemService(this, NotificationManagerCompat::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        nm?.createNotificationChannel(notificationChannel)
    }
}

fun Context.buildNotification(facts: Facts, topics: Topics) {
    val nm = ContextCompat.getSystemService(this, NotificationManagerCompat::class.java)
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Random fact")
        .setContentText(facts.fact)
        .setStyle(
            NotificationCompat.BigTextStyle().bigText(facts.fact).setBigContentTitle(topics.title)
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    nm?.notify(1, notification)
}
