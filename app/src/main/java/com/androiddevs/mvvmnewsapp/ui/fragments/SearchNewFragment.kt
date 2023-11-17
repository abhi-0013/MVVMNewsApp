package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.utils.Constants
import com.androiddevs.mvvmnewsapp.utils.Resources
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewFragment: Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private lateinit var binding : FragmentSearchNewsBinding


    val TAG= "SearchNewsGragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNewsBinding.bind(view)
        viewModel = (activity as NewsActivity).viewModel
        setrecyclerViewAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("articles",it)
            }
            findNavController().navigate(
                R.id.action_searchNewFragment_to_articleNewFragment,
                bundle
            )
        }


        var job: Job?= null
        binding.etSearch.addTextChangedListener{editable->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.getSearchedNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resources.Success -> {
                    hideProgressBar()
//                    Log.d(TAG, "onViewCreated:  it is a SUCCESS")
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.SearchNewsPage == totalPages

//                        if(isLastPage){
//                            binding.rvSearchNews.setPadding(0,0,0,0)
//                        }
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let{message ->
                        Toast.makeText(context,"Error has occured: $message", Toast.LENGTH_LONG).show()
//                        Log.e(TAG,"AN error has occured : $message")
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }

            }

        })
    }

    var isLoading = false
    var isLastPage =false
    var isScrolling =false

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleITemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCOunt = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleITemPosition + visibleItemCOunt >= totalItemCount
            val isNotAtBegining = firstVisibleITemPosition>=0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBegining && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.getSearchedNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility= View.GONE
        isLoading = false
    }
    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setrecyclerViewAdapter(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewFragment.scrollListener)
        }
    }
}