package com.sample.factophile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.factophile.databinding.TopicListItemBinding

class TopicsAdapter(private val context: Context) : RecyclerView.Adapter<TopicsAdapter.TopicViewHolder>() {

    private var topicsList : ArrayList<Topics> = ArrayList()

    fun setTopicsList(topicsList : ArrayList<Topics>) {
        this.topicsList = topicsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(TopicListItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return topicsList.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic: Topics = topicsList[position]
        holder.binding.titleTopic.text = topic.title
        holder.binding.topicLayout.setOnClickListener {
            Intent(context, FactsActivity::class.java).apply {
                putExtra("topic_id", topic.id)
                putExtra("title", topic.title)
                context.startActivity(this)
            }

        }
        holder.binding.imgTopic.load(topic.img) {
            crossfade(true)
        }
    }

    class TopicViewHolder(val binding: TopicListItemBinding) : RecyclerView.ViewHolder(binding.root)
}