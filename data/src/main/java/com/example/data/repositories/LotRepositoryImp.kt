package com.example.data.repositories

import com.example.data.utils.ParkingMapper
import com.example.data.local_data_base.ParkingDataBase
import com.example.data.local_data_base.entities.LotRoom
import com.example.data.service.ParkingService
import com.example.domain.entities.ParkingLotListModel
import com.example.domain.entities.ParkingLotModel
import com.example.domain.repositories.LotRepository
import com.example.domain.entities.Result

class LotRepositoryImp(
    private val parkingService: ParkingService,
    private val parkingDataBase: ParkingDataBase
                                ) : LotRepository{

    override suspend fun getLotList(localDataBase: Boolean): Result<ParkingLotListModel> {
        return when(localDataBase){
            true -> {
                getLocalLotList()
            }
            else -> {
                val lotListFromService = getServiceLotList()
                    updateDataBase(lotListFromService)
                    lotListFromService
            }
        }
    }

    private suspend fun updateDataBase(lotList: Result<ParkingLotListModel>){
        when(lotList){
            is Result.Success -> {
                val lotListFromService = lotList.value?.lotList
                lotListFromService?.forEach { lot ->
                    parkingDataBase.lotDataBaseDao().insertNewLot(LotRoom(lot.parkingLot))
                }
            }
            else -> listOf<ParkingLotModel>()
        }
    }

    private suspend fun getServiceLotList(): Result<ParkingLotListModel> {
        return when (val result =  parkingService.getLotList()){
            is Result.Success -> {
                Result.Success(ParkingMapper.toParkingListResponseToModel(result.value))
            }
            is Result.Failure -> {
                Result.Failure(result.exception)
            }
        }
    }

    private suspend fun getLocalLotList(): Result<ParkingLotListModel>{
        var lotList = parkingDataBase.lotDataBaseDao().findLotList()
        return Result.Success(ParkingMapper.lotRoomListToParkingLotListModel(lotList))
    }
}