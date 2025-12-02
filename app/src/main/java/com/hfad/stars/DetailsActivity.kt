package com.hfad.stars
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hfad.stars.databinding.ActivityDetailsBinding
import com.hfad.stars.viewmodel.DetailsViewModel
import com.squareup.picasso.Picasso


class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarButtons()
        loadObject()
    }
    private fun setupToolbarButtons() {
        // Кнопка "Домой"
        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Кнопка профиля
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Кнопка сохранения
        binding.saveButton.setOnClickListener {
            viewModel.toggleFavorite()
            Toast.makeText(this, "Объект сохранен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadObject() {
        val objectId = intent.getStringExtra("object_id") ?: return

        viewModel.loadObject(objectId)

        viewModel.cosmicObject.observe(this) { cosmicObject ->
            cosmicObject ?: return@observe

            binding.titleTextView.text = cosmicObject.name
            binding.typeTextView.text = "Тип: ${cosmicObject.type ?: "Неизвестно"}"
            binding.descriptionTextView.text = cosmicObject.description ?: "Нет описания"

            cosmicObject.imageUrl?.let { url ->
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_cosmo)
                    .error(R.drawable.ic_cosmo)
                    .into(binding.objectImageView)
            }

            // Обновляем текст кнопки
            binding.saveButton.text = if (cosmicObject.isFavorite) {
                "Убрать из избранного"
            } else {
                "Добавить в избранное"
            }
        }
    }

}