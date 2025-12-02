package com.hfad.stars.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.stars.R
import android.widget.CheckBox
import com.hfad.stars.model.CosmicObject
import com.squareup.picasso.Picasso

class CosmicObjectAdapter(
    private val onItemClick: (CosmicObject) -> Unit,
    private val onItemLongClick: (CosmicObject) -> Boolean = { false }
) : RecyclerView.Adapter<CosmicObjectAdapter.ViewHolder>() {

    private var items = emptyList<CosmicObject>()
    private val selectedItems = mutableSetOf<CosmicObject>()

    // Теперь публичные переменные и методы
    var isSelectionMode = false
        private set

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): Set<CosmicObject> = selectedItems.toSet()

    fun submitList(newItems: List<CosmicObject>) {
        items = newItems
        if (!isSelectionMode) selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cosmic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = selectedItems.contains(item)
        holder.bind(item, isSelectionMode, isSelected)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(cosmicObject: CosmicObject, selectionMode: Boolean, isSelected: Boolean) {
            titleTextView.text = cosmicObject.name

            cosmicObject.imageUrl?.let { url ->
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_cosmo)
                    .error(R.drawable.ic_cosmo)
                    .into(imageView)
            } ?: imageView.setImageResource(R.drawable.ic_cosmo)

            // Чекбокс
            checkBox.visibility = if (selectionMode) View.VISIBLE else View.GONE
            checkBox.isChecked = isSelected

            // Клик по элементу
            itemView.setOnClickListener {
                if (selectionMode) {
                    if (isSelected) {
                        selectedItems.remove(cosmicObject)
                    } else {
                        selectedItems.add(cosmicObject)
                    }
                    notifyItemChanged(adapterPosition)
                } else {
                    onItemClick(cosmicObject)
                }
            }

            // Долгое нажатие — включаем режим выбора
            itemView.setOnLongClickListener {
                if (!isSelectionMode) {
                    setSelectionMode(true)
                    selectedItems.add(cosmicObject)
                    notifyDataSetChanged()
                }
                onItemLongClick(cosmicObject)
            }
        }
    }
}