package com.sample.factophile

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        return suspendCoroutine { cont ->
            db.collection("topics")
                .get()
                .addOnFailureListener {
                    cont.resume(Result.failure())
                }
                .addOnSuccessListener { snapshot ->
                    val topic = snapshot.documents.mapNotNull { it.toObject<Topics>() }.random()
                    db.collection("facts")
                        .whereEqualTo("topic_id", topic.id)
                        .get()
                        .addOnFailureListener { cont.resume(Result.failure()) }
                        .addOnSuccessListener { querySnapshot ->
                            val facts =
                                querySnapshot.documents.mapNotNull { it.toObject<Facts>() }
                                    .random()
                            applicationContext.createNotificationChannel()

                            applicationContext.buildNotification(facts,topic)
                            cont.resume(Result.success())
                        }
                }
        }
    }
}