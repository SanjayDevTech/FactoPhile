package com.sample.factophile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sample.factophile.databinding.ActivityMainBinding
import com.sample.factophile.databinding.DialogProgressBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private val db = Firebase.firestore
    private lateinit var adapter: TopicsAdapter
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val notificationWorker =
//            PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
//                .setInitialDelay(1, TimeUnit.MINUTES)
//                .setConstraints(Constraints
//                    .Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//                )
//                .build()
//
//        WorkManager.getInstance(this)
//            .enqueueUniquePeriodicWork("random-fact", ExistingPeriodicWorkPolicy.KEEP, notificationWorker)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.topicsRv.layoutManager = GridLayoutManager(this, 2)
        adapter = TopicsAdapter(this)
        binding.topicsRv.adapter = adapter

        val dialogLayout = DialogProgressBinding.inflate(layoutInflater)
        dialogLayout.titleDialog.text = "Topics Loading..."
        val dialog = AlertDialog.Builder(this)
            .setView(dialogLayout.root)
            .setCancelable(false)
            .create()
        dialog.show()
        db.collection("topics")
            .get()
            .addOnCompleteListener {taskQuery ->
                dialog.dismiss()
                if(taskQuery.isSuccessful) {
                    val topicsList : ArrayList<Topics> = ArrayList()
                    taskQuery.result?.let { result ->
                        topicsList.addAll(
                            result.documents.mapNotNull { it.toObject<Topics>() }
                        )
                    }

                    adapter.setTopicsList(topicsList)
                } else {
                    Toast.makeText(this, "Failed to retrieve", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Error => ${taskQuery.exception?.message}")
                }
            }
    }
}