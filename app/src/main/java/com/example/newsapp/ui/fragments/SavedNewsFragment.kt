package com.example.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapter.ArticleAdapter
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.newSingleThreadContext

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // this to have access into view model in main activity
        viewModel = (activity as MainActivity).viewModel

        // open adapter items in web view
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        // this to observe on saved articles on db
        // and differ in adapter will auotmatic calculate differnce between items and update changes
        viewModel.getAllNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })

        controlOnItems()
    }


    // handle recycler view
    private fun setupRecyclerView() {
        newsAdapter = ArticleAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    fun controlOnItems() {
        val itemTouchHelperCallBack =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                @SuppressLint("ShowToast")
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // to get adapter position of items first
                    val position = viewHolder.adapterPosition
                    // to get items positions
                    val article = newsAdapter.differ.currentList[position]
                    // to delete this items positions from db
                    viewModel.deleteArticle(article)
                    Snackbar.make(view!!, "Deleted Article", Snackbar.LENGTH_LONG).apply {
                        setAction("Undo") {
                            // if press undo saved article again into db
                            viewModel.insertArticle(article)
                        }
                        show()
                    }
                }
            }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }
}