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

        setupHeader()
        loadObject()
    }
    private fun setupHeader() {
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
            Toast.makeText(this, "Сохранено в избранное", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadObject() {
        val objectId = intent.getStringExtra("object_id") ?: return

        viewModel.loadObject(objectId)

        viewModel.cosmicObject.observe(this) { obj ->
            obj ?: return@observe

            binding.titleTextView.text = obj.name
            binding.typeTextView.text = "Тип: ${obj.type ?: "Неизвестно"}"
            binding.descriptionTextView.text = obj.description ?: "Нет описания"

            obj.imageUrl?.let { url ->
                Picasso.get().load(url).into(binding.objectImageView)
            }

            binding.saveButton.text = if (obj.isFavorite)
                "Убрать из избранного"
            else
                "Добавить в избранное"
        }

    }

}