package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.databinding.ActivityNewsBinding
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding

    val TAG ="NewsACTIVITY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate:  is called for hte newsACTIVITY")


        val newsRepository = NewsRepository(ArticleDatabase(this))
//        val newsRepository = NewsRepository()
        val newsVIewModelProviderFactory = NewsVIewModelProviderFactory(application,newsRepository)

        viewModel = ViewModelProvider(this , newsVIewModelProviderFactory).get(NewsViewModel::class.java)



//        val newHostfragment =
//        val navHostFragment =
//                supportFragmentManager.findFragmentById(binding.flFragment.id) as NavHostFragment?
//        val nn = newHostfragment.

        val navhostfragmetn = supportFragmentManager.findFragmentById(binding.newNavHostfragment.id) as NavHostFragment
        val navController = navhostfragmetn.navController
        binding.bottomNavigationView.setupWithNavController(navController)

    }
}
