package com.sample.factophile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sample.factophile.databinding.ActivityFactsBinding
import com.sample.factophile.databinding.DialogProgressBinding

class FactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFactsBinding
    private lateinit var adapter: FactsAdapter
    private val db = Firebase.firestore
    private val TAG = FactsActivity::class.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val intent = intent
        var topicId = ""
        intent.extras?.let { extras ->
            extras.getString("topic_id")?.let { string ->
                topicId = string
            }
            extras.getString("title")?.let { string->
                binding.toolbar.title = string
            }
        } ?: run {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            finish()
        }
        setSupportActionBar(binding.toolbar)

        adapter = FactsAdapter(this)
        binding.factsRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.factsRv.adapter = adapter
        val snapHelper: LinearSnapHelper  = LinearSnapHelper()

        snapHelper.attachToRecyclerView(binding.factsRv)


        val dialogLayout = DialogProgressBinding.inflate(layoutInflater)
        dialogLayout.titleDialog.text = "Facts Loading..."
        val dialog = AlertDialog.Builder(this)
            .setView(dialogLayout.root)
            .setCancelable(false)
            .create()
        dialog.show()

        db.collection("facts")
            .whereEqualTo("topic_id", topicId)
            .orderBy("id", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    val factList: ArrayList<Facts> = ArrayList()
                    task.result?.let { result ->
                        factList.addAll(result.documents.mapNotNull {
                            it.toObject<Facts>()
                        })
                    }
                    adapter.setFactLists(factList)
                } else {
                    Toast.makeText(this, "Failed to retrieve", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Error => ${task.exception?.message}")
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.fact_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.action_random_fact) {
            randomFact()
            return true
        } else if(id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun randomFact() {
        val pos = (Math.random() *(adapter.itemCount+1)).toInt()
        object :LinearSmoothScroller(this) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }.let {smoothScroller->
            smoothScroller.targetPosition = pos
            binding.factsRv.layoutManager?.startSmoothScroll(smoothScroller)
        }
    }
}