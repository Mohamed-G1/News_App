package com.example.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.*

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // DiffUtil to compare between two lists and only update items that different
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            // we use url bez every items has a unique url
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // that will take the two items and compare between them in background and calculate
    // the diffrence between them
    private val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.description
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt

            // on item clicked
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // this to open web page after pressing on item
    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}