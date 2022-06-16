package com.example.my_first_app.viewModel.reservationsViewModelPackage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.data.local_data_base.ReservationDataBase
import com.example.data.repositories.DeleteRepositoryImp
import com.example.data.service.ParkingService
import com.example.domain.usecases.DeleteReservationUseCase

class ReservationViewModelFactory(private val context: Context) : NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == ReservationViewModel::class.java) {
            ReservationViewModel(DeleteReservationUseCase().apply {
                deleteReservationRepository = DeleteRepositoryImp(
                    ParkingService(),
                    ReservationDataBase.getInstance(context)
                )
            }) as T
        } else {
            super.create(modelClass)
        }
    }

}