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

        setupToolbar()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupToolbar() {
        // Делаем иконку "Домой" в тулбаре кликабельной
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
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
                // Удаляем из избранного долгим нажатием
                viewModel.removeFromFavorites(cosmicObject)
                Toast.makeText(
                    this,
                    "${cosmicObject.name} удалён из избранного",
                    Toast.LENGTH_SHORT
                ).show()
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






















