package com.yourname.redgifboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GifAdapter(
    private val onGifClick: (GifItem) -> Unit,
    private val onLoadMoreClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_GIF = 0
        private const val TYPE_LOAD_MORE = 1
    }

    private val gifs = mutableListOf<GifItem>()
    var showLoadMore = false

    inner class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gifImageView)
    }

    inner class LoadMoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: TextView = view.findViewById(R.id.loadMoreBtn)
    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoadMore && position == gifs.size) TYPE_LOAD_MORE else TYPE_GIF
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_GIF) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false)
            GifViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.load_more_item, parent, false)
            LoadMoreViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GifViewHolder) {
            val gif = gifs[position]
            // Try thumbnail first, fall back to sd url
            val url = "https://i.redgifs.com/i/${gif.id}.gif"
            Glide.with(holder.imageView.context)
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.holo_red_dark)
                .into(holder.imageView)
            holder.imageView.setOnClickListener { onGifClick(gif) }
        } else if (holder is LoadMoreViewHolder) {
            holder.button.setOnClickListener { onLoadMoreClick() }
        }
    }

    override fun getItemCount() = if (showLoadMore) gifs.size + 1 else gifs.size

    fun setGifs(newGifs: List<GifItem>) {
        gifs.clear()
        gifs.addAll(newGifs)
        notifyDataSetChanged()
    }

    fun appendGifs(newGifs: List<GifItem>) {
        val start = gifs.size
        gifs.addAll(newGifs)
        notifyItemRangeInserted(start, newGifs.size)
    }

    fun clearGifs() {
        gifs.clear()
        notifyDataSetChanged()
    }
}
