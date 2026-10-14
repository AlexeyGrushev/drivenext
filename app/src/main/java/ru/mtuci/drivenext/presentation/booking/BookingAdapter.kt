package ru.mtuci.drivenext.presentation.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ItemBookingBinding
import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.model.BookingState
import ru.mtuci.drivenext.presentation.common.formatDate
import ru.mtuci.drivenext.presentation.common.formatDateTime

/** Список бронирований: активные открываются по нажатию, завершённые и отменённые — нет. */
class BookingAdapter(
    private val onActiveClick: (Booking) -> Unit,
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(DIFF) {

    class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookingViewHolder(ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = getItem(position)
        val res = holder.itemView.resources
        val state = booking.stateAt(Instant.now())
        with(holder.binding) {
            bookingTitle.text = "${booking.brand} ${booking.model}"
            bookingSubtitle.text = when (state) {
                BookingState.ACTIVE -> res.getString(R.string.booking_start_format, res.formatDateTime(booking.startAt))
                BookingState.COMPLETED -> res.getString(R.string.booking_completed_format, res.formatDate(booking.endAt))
                BookingState.CANCELLED ->
                    res.getString(R.string.booking_cancelled_format, res.formatDate(booking.cancelledAt ?: booking.endAt))
            }
            if (state == BookingState.ACTIVE) {
                root.setOnClickListener { onActiveClick(booking) }
            } else {
                root.setOnClickListener(null)
                root.isClickable = false
            }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Booking>() {
            override fun areItemsTheSame(oldItem: Booking, newItem: Booking) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Booking, newItem: Booking) = oldItem == newItem
        }
    }
}
