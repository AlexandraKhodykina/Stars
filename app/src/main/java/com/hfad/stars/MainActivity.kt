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

        setupUI()
        setupObservers()

        viewModel.loadData()
    }

    private fun setupUI() {
        binding.toolbar.title = getString(R.string.title_main)

        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        adapter = CosmicObjectAdapter(
            onItemClick = { cosmicObject ->
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("object_id", cosmicObject.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { cosmicObject ->
                viewModel.toggleFavorite(cosmicObject)
                val message = if (!cosmicObject.isFavorite) {
                    "${cosmicObject.name} добавлен в избранное"
                } else {
                    "${cosmicObject.name} удален из избранного"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                true
            }
        )

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        binding.retryButton.setOnClickListener {
            viewModel.refresh()
        }

        // Настройка поиска (теперь searchView точно есть в макете)
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: "")
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.cosmicObjects.observe(this) { objects ->
            adapter.submitList(objects)

            if (objects.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

        viewModel.networkAvailable.observe(this) { isAvailable ->
            binding.networkWarning.visibility = if (!isAvailable) View.VISIBLE else View.GONE
        }
    }
}