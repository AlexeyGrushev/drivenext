package ru.mtuci.drivenext.presentation.cars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ItemCarBinding
import ru.mtuci.drivenext.domain.model.Car

/**
 * Адаптер списка автомобилей для RecyclerView.
 * ListAdapter + DiffUtil сами считают, какие карточки изменились, и обновляют только их.
 */
class CarAdapter(
    private val onBook: (Car) -> Unit,
    private val onDetails: (Car) -> Unit,
) : ListAdapter<Car, CarAdapter.CarViewHolder>(DIFF) {

    /** ViewHolder хранит ссылки на элементы карточки, чтобы не искать их при каждой прокрутке. */
    class CarViewHolder(val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root)

    // Создаёт View карточки и ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    // Заполняет карточку данными автомобиля по позиции
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = getItem(position)
        val context = holder.itemView.context
        with(holder.binding) {
            carModel.text = car.model
            carBrand.text = car.brand
            carPrice.text = context.getString(R.string.car_price_format, car.pricePerDay)
            carTransmission.text = car.transmission
            carFuel.text = car.fuel
            carImage.load(car.imageUrl) {
                placeholder(R.drawable.car_placeholder)
                error(R.drawable.car_placeholder)
            }
            bookButton.setOnClickListener { onBook(car) }
            detailsButton.setOnClickListener { onDetails(car) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Car>() {
            override fun areItemsTheSame(oldItem: Car, newItem: Car) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Car, newItem: Car) = oldItem == newItem
        }
    }
}
