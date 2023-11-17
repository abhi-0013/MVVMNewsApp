package com.androiddevs.mvvmnewsapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.databinding.ItemArticlePreviewBinding
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
//import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder( val binding: ItemArticlePreviewBinding): RecyclerView.ViewHolder(binding.root)

    val TAG = "NEWSADAPTER"
    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        Log.d(TAG, "onBindViewHolder:  primary Key is ${article.id}")
//        Log.d(TAG, "onBindViewHolder: articleURL is ${article.source.id} &&&&&&&&&&&")
        holder.binding.apply {
            if(article.urlToImage != "null")
        Glide.with(root).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt

            root.setOnClickListener {
                onItemClickeListener?.let { it(article) }
            }
        }
    }

    private var onItemClickeListener: ((Article)-> Unit)?=null

    fun setOnItemClickListener(listener: (Article)-> Unit){
        onItemClickeListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}