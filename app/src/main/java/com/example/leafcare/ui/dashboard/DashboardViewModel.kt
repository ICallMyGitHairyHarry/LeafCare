package com.example.leafcare.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.model.Plant
import com.example.leafcare.data.model.PlantNote
import com.example.leafcare.data.model.PlantSchedule
import com.example.leafcare.data.model.PlantType
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.network.PlantsApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DashboardViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // network status value for home fragment
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    // error message
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // plants schedules list
    private val _plantSchedules = MutableLiveData<List<PlantSchedule>>()
    val plantSchedules: LiveData<List<PlantSchedule>> = _plantSchedules

    // plants schedules list
    private val _plantTypes = MutableLiveData<List<PlantType>>()
    val plantTypes: LiveData<List<PlantType>> = _plantTypes

    // boolean for check if user is logged in
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn


    init {
        PlantsApi.init(tokenManager)
        getAvailablePlantTypes()
    }

    // GET PLANT TYPES
    private fun getAvailablePlantTypes() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                _plantTypes.value = PlantsApi.retrofitService
                    .getPlantTypes()
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                handleError(e)
            }
        }
    }

    // GET SCHEDULES
    fun getMySchedules() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
//                var plantList = PlantsApi.retrofitService
//                    .getMyPlants()
//                plantList = listOf(
//                    Plant("Name", "succulent", "photo", "_plants.value = "),
//                    Plant("CAACTUS", "succulent", "photo", "I found him in the trash, it was so cute.")
//                )
//                _schedules.value = plantList
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _plantSchedules.value = listOf()
                handleError(e)
            }
        }
    }


    private fun handleError (e: Exception) {
        when (e) {
            is HttpException -> {
                // Handle HTTP exceptions (like 422, 401, etc.)
                val errorCode = e.code()
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(
                    "HTTP Error",
                    "Code: $errorCode, Body: ${errorBody ?: "Unknown error"}"
                )

                // Handle specific HTTP error codes if necessary
                when (errorCode) {
                    401 -> {
                        tokenManager.clearTokens()
                        _isLoggedIn.value = false
                    }
                    else -> _error.value = "Неизвестная ошибка сервера"
                }
            }

            // Handle timeout exceptions
            is java.net.SocketTimeoutException -> {
                Log.e(
                    "Timeout Error",
                    e.toString()
                )
                _error.value = "Ошибка соединения, сервер не отвечает"
            }

            // Handle no internet connection or bad URL
            is java.net.UnknownHostException -> {
                Log.e(
                    "Unknown Host Error",
                    e.toString()
                )
                _error.value = "Нет соединения или сервер недоступен"
            }

            // Handle generic I/O exceptions (e.g., no connection)
            is IOException -> {
                Log.e(
                    "IO Error",
                    e.toString()
                )
                _error.value = "Ошибка соединения"
            }

            // Handle any other exceptions
            else -> {
                Log.e(
                    "Other Error",
                    e.toString()
                )
                _error.value = "Неизвестная ошибка"
            }

        }
    }

}