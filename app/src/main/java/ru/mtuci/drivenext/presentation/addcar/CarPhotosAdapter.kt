package ru.mtuci.drivenext.presentation.addcar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.mtuci.drivenext.databinding.ItemAddPhotoBinding
import ru.mtuci.drivenext.databinding.ItemCarPhotoBinding

/** Элемент списка: фото или плитка «Добавить фото» (пока их меньше пяти). */
sealed interface PhotoItem {
    data class Photo(val uri: String, val isMain: Boolean) : PhotoItem
    data object Add : PhotoItem
}

class CarPhotosAdapter(
    private val onRemove: (String) -> Unit,
    private val onAdd: () -> Unit,
) : ListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFF) {

    class PhotoViewHolder(val binding: ItemCarPhotoBinding) : RecyclerView.ViewHolder(binding.root)
    class AddViewHolder(val binding: ItemAddPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = if (getItem(position) is PhotoItem.Add) TYPE_ADD else TYPE_PHOTO

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ADD) {
            AddViewHolder(ItemAddPhotoBinding.inflate(inflater, parent, false))
        } else {
            PhotoViewHolder(ItemCarPhotoBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PhotoItem.Photo -> with((holder as PhotoViewHolder).binding) {
                // Главное (первое) фото крупнее остальных, как в макете
                val params = photo.layoutParams as ConstraintLayout.LayoutParams
                params.dimensionRatio = if (item.isMain) "342:230" else "167:112"
                photo.layoutParams = params
                photo.load(item.uri)
                removeButton.setOnClickListener { onRemove(item.uri) }
            }
            PhotoItem.Add -> (holder as AddViewHolder).binding.addTile.setOnClickListener { onAdd() }
        }
    }

    private companion object {
        const val TYPE_PHOTO = 0
        const val TYPE_ADD = 1

        val DIFF = object : DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem) = when {
                oldItem is PhotoItem.Photo && newItem is PhotoItem.Photo -> oldItem.uri == newItem.uri
                else -> oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem) = oldItem == newItem
        }
    }
}
