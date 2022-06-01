package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this to have access into view model in main activity
        viewModel = (activity as MainActivity).viewModel


        // to receive clicked items
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it) }
        }

        // save article to db when press on fav
        fab.setOnClickListener {
            viewModel.insertArticle(article)
            Snackbar.make(view, "Save Article", Snackbar.LENGTH_SHORT).show()

        }

    }

}