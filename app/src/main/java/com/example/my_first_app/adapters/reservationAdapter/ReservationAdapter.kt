package com.example.my_first_app.adapters.reservationAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.Reservation
import com.example.my_first_app.R

class ReservationAdapter(private val itemsList: List<Reservation>, private val onClickListener:(Reservation) -> Unit): RecyclerView.Adapter<ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ReservationViewHolder(layoutInflater.inflate(R.layout.layout_item_reservation, parent, false))
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val item = itemsList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = itemsList.size

}