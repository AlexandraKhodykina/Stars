package com.hfad.stars

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hfad.stars.adapter.CosmicObjectAdapter
import com.hfad.stars.databinding.ActivityMainBinding
import com.hfad.stars.viewmodel.MainViewModel
import android.view.View
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: CosmicObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupRecyclerView()
        setupSearch()
        setupObservers()

        viewModel.refresh() // первая загрузка
    }

    private fun setupHeader() {
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }
    private fun setupRecyclerView() {
        adapter = CosmicObjectAdapter(
            onItemClick = { cosmicObject ->
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("object_id", cosmicObject.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { cosmicObject ->
                viewModel.toggleFavorite(cosmicObject)
                true
            }
        )

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        binding.retryButton.setOnClickListener { viewModel.refresh() }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim() ?: ""
                viewModel.search(query)  // ← Вот он — поиск!
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.cosmicObjects.observe(this) { list ->
            adapter.submitList(list)
            val isEmpty = list.isEmpty()
            binding.emptyTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                binding.errorTextView.text = error
                binding.errorTextView.visibility = View.VISIBLE
                binding.retryButton.visibility = View.VISIBLE
            } else {
                binding.errorTextView.visibility = View.GONE
                binding.retryButton.visibility = View.GONE
            }
        }
    }
}