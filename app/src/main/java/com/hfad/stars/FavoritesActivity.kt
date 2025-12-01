package com.hfad.stars
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.stars.adapter.CosmicObjectAdapter
import com.hfad.stars.databinding.ActivityFavoritesBinding
import com.hfad.stars.viewmodel.FavoritesViewModel


class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: CosmicObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Настройка Toolbar
        binding.toolbar.title = getString(R.string.title_favorites)

        // Кнопка домой
        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Настройка списка
        adapter = CosmicObjectAdapter { cosmicObject ->
            val intent = Intent(this, DetailsActivity::class.java).apply {
                putExtra("object_id", cosmicObject.id)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.favorites.observe(this) { favorites ->
            adapter.submitList(favorites)

            if (favorites.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }
}