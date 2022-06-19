package com.example.data.repositories

import com.example.data.local_data_base.ParkingDataBase
import com.example.data.local_data_base.entities.ReservationRoom
import com.example.data.utils.ParkingMapper
import com.example.data.service.ParkingService
import com.example.domain.entities.*
import com.example.domain.repositories.ReservationRepository
import com.example.domain.usecases.AddUseCase

class ReservationRepositoryImp(
    private val parkingService : ParkingService,
    private val parkingDataBase: ParkingDataBase
                                        ): ReservationRepository {


    override suspend fun getReservationList(localDataBase: Boolean): Result<ReservationListModel> {
        return when(localDataBase) {
            true -> {
                getLocalReservationList()
            }
            else -> {
                val reservationListFromService = getServiceReservationList()
                updateDataBase(reservationListFromService)
                reservationListFromService
            }
        }
    }

    private suspend fun updateDataBase(reservationList: Result<ReservationListModel>){
        when(reservationList){
            is Result.Success -> {

                // Check if any reservation is missing
                val reservationListFromService = reservationList.value?.reservationList
                reservationListFromService?.forEach { reservation ->
                    parkingDataBase.reservationDataBaseDao().insertNewReservation(
                        ParkingMapper.reservationModelToReservationRoom(reservation)
                    )
                }
                // Check if any spare reservation
                var reservationDoNotExistInService = true
                val reservationListFromDataBase = parkingDataBase.reservationDataBaseDao().findReservationList()
                reservationListFromDataBase.forEach { reservationFromDataBase ->
                    reservationListFromService?.forEach { reservationFromService ->
                        if (reservationFromDataBase.id == reservationFromService.id){
                            reservationDoNotExistInService = false
                        }
                    }
                    if(reservationDoNotExistInService){
                        parkingDataBase.reservationDataBaseDao().deleteReservation(reservationFromDataBase.id)
                        reservationDoNotExistInService = true
                    }
                }
            }
        }
    }

    private suspend fun getServiceReservationList(): Result<ReservationListModel>{
        val result =  parkingService.getReservationList()
        return when (result){
            is Result.Success -> {
                Result.Success(ParkingMapper.toReservationListResponseToModel(result.value))
            }
            is Result.Failure -> {
                Result.Failure(result.exception)
            }
        }
    }

    private suspend fun getLocalReservationList(): Result<ReservationListModel>{
        val reservationList = parkingDataBase.reservationDataBaseDao().findReservationList()
        return Result.Success(ParkingMapper.reservationRoomListToReservationListModel(reservationList))
    }
}

/*
private var reservations = mutableListOf<Reservation>(
        Reservation("ingreseId",1539525000,1539525000, "1",1),
        Reservation("ingreseId",5553125099,5553125099, "1",2),
        Reservation("ingreseId",9153925055,9153925055, "1",3),
        Reservation("ingreseId",1539525000,1539525000, "1",1),
    )

    fun addReservation(reservation : Reservation){
        reservations.add(reservation)
    }

    fun deleteReservation(reservation: Reservation){
        reservations.remove(reservation)
    }

    fun getReservationList(): List<Reservation> {
        return reservations
    }
 */