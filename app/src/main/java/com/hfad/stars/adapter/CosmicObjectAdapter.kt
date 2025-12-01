package com.hfad.stars.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.stars.R
import com.hfad.stars.model.CosmicObject
import com.squareup.picasso.Picasso

class CosmicObjectAdapter(
    private val onltemClick: (CosmicObject) -> Unit,
    private val onItemLongClick: ((CosmicObject) -> Unit)? = null  // Убрал ? после Unit
) : RecyclerView.Adapter<CosmicObjectAdapter.ViewHolder>() {

    private var items = listOf<CosmicObject>()

    fun submitList(newItems: List<CosmicObject>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cosmic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onltemClick(item)
        }
        // Обработка долгого нажатия (только для избранного)
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(item)
                true
            }
        }


    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        fun bind(cosmicObject: CosmicObject) {
            // Используем поле 'name' из вашей модели
            // (которое мапится с JSON поля "title")
            titleTextView.text = cosmicObject.name

            // Используем поле 'imageUrl' из вашей модели
            // (которое мапится с JSON поля "url")
            cosmicObject.imageUrl?.let { url ->
                Picasso.get()
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Заглушка из Android
                    .error(android.R.drawable.ic_menu_report_image) // Иконка при ошибке
                    .into(imageView)
            } ?: run {
                // Если URL нет, показываем стандартную иконку
                imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}