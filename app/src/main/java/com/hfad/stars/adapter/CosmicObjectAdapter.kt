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

private val onItemClick: (CosmicObject) -> Unit,
private val onItemLongClick: (CosmicObject) -> Unit
) : RecyclerView.Adapter<CosmicObjectAdapter.ViewHolder>() {

    private var items = emptyList<CosmicObject>()

    fun submitList(newItems: List<CosmicObject>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cosmic, parent, false)
        return ViewHolder(view, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(
        itemView: View,
        private val onItemClick: (CosmicObject) -> Unit,
        private val onItemLongClick: (CosmicObject) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val favoriteIcon: ImageView? = itemView.findViewById(R.id.favoriteIcon)

        fun bind(cosmicObject: CosmicObject) {
            titleTextView.text = cosmicObject.name

            // Показываем иконку избранного
            favoriteIcon?.visibility = if (cosmicObject.isFavorite) View.VISIBLE else View.GONE

            // Загружаем изображение
            cosmicObject.imageUrl?.let { url ->
                Picasso.get()
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imageView)
            } ?: run {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Назначаем обработчики
            itemView.setOnClickListener { onItemClick(cosmicObject) }
            itemView.setOnLongClickListener {
                onItemLongClick(cosmicObject)
                true
            }
        }
    }
}