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

        setupHeader()
        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Сбрасываем режим выбора каждый раз при открытии экрана
        adapter.setSelectionMode(false)
        binding.deleteButton.visibility = View.GONE
    }

    private fun setupHeader() {
        // Кнопка «Домой»
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        // Кнопка «Мусорка» — удаление выбранных
        binding.deleteButton.setOnClickListener {
            val selected = adapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                selected.forEach { viewModel.removeFromFavorites(it) }
                adapter.setSelectionMode(false)
                binding.deleteButton.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CosmicObjectAdapter(
            onItemClick = { cosmicObject ->
                // Обычный клик — открываем детали
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("object_id", cosmicObject.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { cosmicObject ->
                // Долгое нажатие — включаем режим выбора и показываем мусорку
                adapter.setSelectionMode(true)
                binding.deleteButton.visibility = View.VISIBLE
                true
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.favorites.observe(this) { list ->
            adapter.submitList(list)

            binding.emptyTextView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE

            // Если список пустой — гарантированно убираем мусорку
            if (list.isEmpty()) {
                adapter.setSelectionMode(false)
                binding.deleteButton.visibility = View.GONE
            }
        }
    }
}



















