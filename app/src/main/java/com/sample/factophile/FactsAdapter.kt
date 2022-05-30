package com.sample.factophile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sample.factophile.databinding.FactListItemBinding

class FactsAdapter(private val context: Context) :
    RecyclerView.Adapter<FactsAdapter.FactViewHolder>() {

    private var factLists: ArrayList<Facts> = ArrayList()
    private val colorsList = listOf(0xFF800080.toInt(), 0xFFDC143C.toInt(), 0xFF2E8B57.toInt(), 0xFF6A5ACD.toInt(), 0xFFFF6933.toInt())


    fun setFactLists(factLists: ArrayList<Facts>) {
        this.factLists = factLists
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        return FactViewHolder(
            FactListItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return factLists.size
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        val fact: Facts = factLists[position]
        val color = colorsList[(position)%colorsList.size]
        holder.binding.factText.text = fact.fact
        holder.binding.copyFact.setOnClickListener {
            val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Fact", fact.fact)
            clipBoard.setPrimaryClip(clipData)
            Toast.makeText(context, "Copied!!!", Toast.LENGTH_SHORT).show()
        }
        holder.binding.shareFact.setOnClickListener {
            holder.binding.copyFact.isVisible = false
            holder.binding.shareFact.isVisible = false
            val bitmap = viewToImage(holder.binding.baseLayout, color)
            holder.binding.copyFact.isVisible = true
            holder.binding.shareFact.isVisible = true
            val uri = bitmap.getBitmapUri(context)
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, fact.fact)
                putExtra(Intent.EXTRA_STREAM,uri)
                type = "image/jpg"
//                setDataAndType(uri,"image/jpg")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.let { intent->
                val shareIntent = Intent.createChooser(intent, "Share")
                context.startActivity(shareIntent)
            }
        }
        holder.binding.baseLayout.setCardBackgroundColor(color)
    }

    class FactViewHolder(val binding: FactListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}