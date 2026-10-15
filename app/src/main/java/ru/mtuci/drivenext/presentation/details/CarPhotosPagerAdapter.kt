package ru.mtuci.drivenext.presentation.details

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.mtuci.drivenext.R

/** Страницы галереи ViewPager2: одна страница — одно фото автомобиля на всю ширину. */
class CarPhotosPagerAdapter : RecyclerView.Adapter<CarPhotosPagerAdapter.PhotoHolder>() {

    private var urls: List<String> = emptyList()

    fun submit(newUrls: List<String>) {
        if (newUrls == urls) return
        urls = newUrls
        notifyDataSetChanged()
    }

    override fun getItemCount() = urls.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val image = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return PhotoHolder(image)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.image.load(urls[position]) {
            placeholder(R.drawable.car_placeholder)
            error(R.drawable.car_placeholder)
        }
    }

    class PhotoHolder(val image: ImageView) : RecyclerView.ViewHolder(image)
}
