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
import android.widget.Toast
import androidx.activity.viewModels


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


        // Создаем адаптер с лямбдой
        adapter = CosmicObjectAdapter(
            onItemClick = { cosmicObject ->
                // Обычный клик - открываем детали
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("object_id", cosmicObject.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { cosmicObject ->
                viewModel.removeFromFavorites(cosmicObject)
                Toast.makeText(this, "${cosmicObject.name} удалён из избранного", Toast.LENGTH_SHORT).show()
                true
            }
        )


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






















