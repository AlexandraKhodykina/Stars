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

    private fun setupHeader() {
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        // Кнопка удаления
        binding.deleteButton.setOnClickListener {
            val selected = adapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                selected.forEach { cosmicObject ->
                    viewModel.removeFromFavorites(cosmicObject)
                }
                adapter.setSelectionMode(false)
                binding.deleteButton.visibility = View.GONE
                Toast.makeText(this, "Удалено ${selected.size} объектов", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupRecyclerView() {
        adapter = CosmicObjectAdapter(
            onItemClick = { cosmicObject ->
                // Открываем детальную страницу
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("object_id", cosmicObject.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { cosmicObject ->
                // Включаем режим выбора при долгом клике
                if (!adapter.isSelectionMode) {
                    adapter.setSelectionMode(true)
                    binding.deleteButton.visibility = View.VISIBLE
                }
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
        }

    }
}






















