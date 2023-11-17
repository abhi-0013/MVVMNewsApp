package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.databinding.FragmentArticleBinding
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleNewFragment: Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentArticleBinding
    val args: ArticleNewFragmentArgs by navArgs()
    val TAG = "ArticleNewFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        viewModel = (activity as NewsActivity).viewModel

        val article = args.articles
        binding.webView.apply {

            webViewClient = WebViewClient()
           loadUrl(article.url?: "news.org")
        }

        binding.fab.setOnClickListener{
            Log.d(TAG, "onViewCreated: prim ${article.id}")
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article is saved Successfully",Snackbar.LENGTH_SHORT).show()
        }
    }
}