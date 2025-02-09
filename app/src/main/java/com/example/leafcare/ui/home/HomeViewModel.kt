package com.example.leafcare.ui.home

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.model.PlantCreateRequest
import com.example.leafcare.data.model.Plant
import com.example.leafcare.data.model.PlantNote
import com.example.leafcare.data.model.PlantNoteCreateRequest
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.network.PlantsApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class HomeViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // network status value for home fragment
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    // specific network status value for AddPlantFragment
    private val _addPlantStatus = MutableLiveData<ApiStatus>()
    val addPlantStatus: LiveData<ApiStatus> = _addPlantStatus

    // specific network status value for AddPlantNoteFragment
    private val _addPlantNoteStatus = MutableLiveData<ApiStatus>()
    val addPlantNoteStatus: LiveData<ApiStatus> = _addPlantNoteStatus

    // specific network status value for Notes in MyPlantFragment
    private val _noteStatus = MutableLiveData<ApiStatus>()
    val noteStatus: LiveData<ApiStatus> = _noteStatus

    // error message
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // plants list
    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> = _plants

    // particular picked plant
    private val _plant = MutableLiveData<Plant>()
    val plant: LiveData<Plant> = _plant

    // plant types list
    private val _plantTypesNames = MutableLiveData<List<String>>()
    val plantTypesNames: LiveData<List<String>> = _plantTypesNames
//    private val _plantTypes = MutableLiveData<List<PlantType>>()
//    val plantTypes: LiveData<List<PlantType>> = _plantTypes

    // specific plant notes list
    private val _plantNotes = MutableLiveData<List<PlantNote>>()
    val plantNotes: LiveData<List<PlantNote>> = _plantNotes

    // particular picked note
    private val _note = MutableLiveData<PlantNote>()
    val note: LiveData<PlantNote> = _note

    // boolean for check if _plants empty
    private val _isPlantsEmpty = MutableLiveData<Boolean>()
    val isPlantsEmpty: LiveData<Boolean> = _isPlantsEmpty

    // boolean for check if user is logged in
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn



    init {
        _isPlantsEmpty.value = false
        PlantsApi.init(tokenManager)
        getMyPlants() //TODO: nav bug
    }



    // GET MY PLANTS
    fun getMyPlants() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                val plantList = PlantsApi.retrofitService
                    .getMyPlants()
//                plantList = listOf(
////                    Plant("Name", "succulent", "photo", "_plants.value = "),
////                    Plant("CAACTUS", "succulent", "photo", "I found him in the trash, it was so cute.")
//                    Plant(1,"Cactus1", "Cactus", "/9j/4AAQSkZJRgABAQAAAQABAAD/4gIoSUNDX1BST0ZJTEUAAQEAAAIYAAAAAAQwAABtbnRyUkdC\\nIFhZWiAAAAAAAAAAAAAAAABhY3NwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAA9tYAAQAA\\nAADTLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAlk\\nZXNjAAAA8AAAAHRyWFlaAAABZAAAABRnWFlaAAABeAAAABRiWFlaAAABjAAAABRyVFJDAAABoAAA\\nAChnVFJDAAABoAAAAChiVFJDAAABoAAAACh3dHB0AAAByAAAABRjcHJ0AAAB3AAAADxtbHVjAAAA\\nAAAAAAEAAAAMZW5VUwAAAFgAAAAcAHMAUgBHAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFhZWiAA\\nAAAAAABvogAAOPUAAAOQWFlaIAAAAAAAAGKZAAC3hQAAGNpYWVogAAAAAAAAJKAAAA+EAAC2z3Bh\\ncmEAAAAAAAQAAAACZmYAAPKnAAANWQAAE9AAAApbAAAAAAAAAABYWVogAAAAAAAA9tYAAQAAAADT\\nLW1sdWMAAAAAAAAAAQAAAAxlblVTAAAAIAAAABwARwBvAG8AZwBsAGUAIABJAG4AYwAuACAAMgAw\\nADEANv/bAEMAUDc8RjwyUEZBRlpVUF94yIJ4bm549a+5kcj/////////////////////////////\\n///////////////////////bAEMBVVpaeGl464KC6///////////////////////////////////\\n///////////////////////////////////////AABEIAKAAeAMBIgACEQEDEQH/xAAYAAEBAQEB\\nAAAAAAAAAAAAAAAAAQIDBP/EACcQAQACAQMDBAIDAQAAAAAAAAABEQIhUWEDEjEyQXGREyJCUqGB\\n/8QAFgEBAQEAAAAAAAAAAAAAAAAAAAEC/8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAwDAQACEQMR\\nAD8AgDKgooiggAAAAgoCAAgoClCqAoCFNAJSU0AzQ0gIUoDKNIggChEtRLEwzcoO0K5RlO7vGGnn\\n/FEG+zk7OQYG+zk7OQYR0/Hyn4+QYR0/Hy59WJwxuJBJZtjundLkG7GIAdcvDi75+HAGsZuYeyPD\\nx9P1PZAKAIoACKgDl1/Q6ufW9IryFmqA1j5Ex8gO/U8OF1Lt1fDjpYN9Pz/x64eXpxFTT1QCgCKC\\nSCoWAOfV8R8ujn1fEfIryTNWzbWURbINY+RMfYB36rjXP+N553EWzYOnTisZ1ejunZ5+n6J+XouA\\nO6djunZdDQEiZ2JmdmtDQGJmZ9ludl0NASJmPZjqTeLpox1PSDzZ4/tOrFcumcazqxIJAAEzdI6R\\n0Z3ajoR7zIM45zVQ3HUyvyR0Yj3lqOnjHyDpqap2ZcL2ZcAamqdmfB2Z8AupqnZnwdmfH2C6s5eK\\nOzPj7YziYnXyDGfqYdcsZnWNnOYkGRdQHoudi52QsFibhqGMdIaB0idFti3PPLKcqi6jkHe0t57z\\n5+y8+fsHost5rz3n7W895+wei3Hq+WO7KPMz9pOU7z9g6YZfrWzUuFzvJGUx7yDrOMSJjlGXyALC\\nQoKsaMwoLM6MRM+8QZ7M1yDdxwndG0Mdq1GwN90bJcbM+Cwb02S8dmb5SZ5Bu42Lj+rnfJc7g33R\\nH8RzuQHU86BAKDOc1AM3MzZcpa2BclyaoBqagACAUGoCxESJQD//2Q==\\n", "Caaaactus"),
//                    Plant(1,"Cactus1", "Cactus", "/9j/4AAQSkZJRgABAQAAAQABAAD/4gIoSUNDX1BST0ZJTEUAAQEAAAIYAAAAAAQwAABtbnRyUkdCIFhZWiAAAAAAAAAAAAAAAABhY3NwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAA9tYAAQAAAADTLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAlkZXNjAAAA8AAAAHRyWFlaAAABZAAAABRnWFlaAAABeAAAABRiWFlaAAABjAAAABRyVFJDAAABoAAAAChnVFJDAAABoAAAAChiVFJDAAABoAAAACh3dHB0AAAByAAAABRjcHJ0AAAB3AAAADxtbHVjAAAAAAAAAAEAAAAMZW5VUwAAAFgAAAAcAHMAUgBHAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFhZWiAAAAAAAABvogAAOPUAAAOQWFlaIAAAAAAAAGKZAAC3hQAAGNpYWVogAAAAAAAAJKAAAA+EAAC2z3BhcmEAAAAAAAQAAAACZmYAAPKnAAANWQAAE9AAAApbAAAAAAAAAABYWVogAAAAAAAA9tYAAQAAAADTLW1sdWMAAAAAAAAAAQAAAAxlblVTAAAAIAAAABwARwBvAG8AZwBsAGUAIABJAG4AYwAuACAAMgAwADEANv/bAEMAUDc8RjwyUEZBRlpVUF94yIJ4bm549a+5kcj////////////////////////////////////////////////////bAEMBVVpaeGl464KC6//////////////////////////////////////////////////////////////////////////AABEIAKAAeAMBIgACEQEDEQH/xAAYAAEBAQEBAAAAAAAAAAAAAAAAAQIDBP/EACYQAQACAQMDBAMBAQAAAAAAAAABEQIhMVEDEmEyQXGREyLwQlL/xAAWAQEBAQAAAAAAAAAAAAAAAAAAAQL/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCAMqCiiKCAAAACCgIACCgKUKoUUoCUU0AlFKAzQ0gJRSgMo0iCAKES1EsTDNyg7QrlGU8vRGGm6jI32eTs8gwN9nk7PIMI6fj8n4/IOaOn4/Ln1YnDG4kElm2LnlLkG7GIAdctnG3fPZwBrGbmHsjZ4+n6nsgFAEUABFQBy6/odXPrekV5CzVAax3Ex3Ad+ps4XUu3V2cdLBvp7z8PXDy9OIqaeqAUARRCQVCwBz6u0fLo59XaPkV5JmmbayiLZBrHcTH2Ad+q415bzzuItmwdOnFYzq9Fy8/T9E/L0XAHdPB3TwuhoCRM8EzPC3BcAzMzPstzwuhoCRMx7MdSbxdLhjq+kHmzx1nVivLpnGs6sSCQABM3SOkdGeWo6Ee8yDOOc1UNxnle6x0sY95WMMYB01Nf6E7MvC9mXgDX+g1/oTsy8HZlzALr/Qap2ZeDsy8AurGW1L2ZeGM4mJ13BjP1MOuWMzrHDnMTwDIuoD0XPBc8IWCxNw1DOOkKDpE6LbFuec5TlUbR5B3tLee8/P2Xn5+weiy3m/fmfs/fmfsHptx6u7F5RvM/aTlPM/YOmGX61w1Lz3PMrGUx7yDrOMSJjnGXyALCQoKsaMwoLM6MRM+9GeujNA13R4O6OIY7VqOAa7o4LjhnYsG9OE7o4ZtJ+Qb7o4Lj/lzvyX5BvuiP8jncgOpvoiwCmwznNQDNzM2XKWtgXJcmqAamoAAgFBqAsV7iUA//2Q==", "Caaaactus")
//                )
                _plants.value = plantList
                _isPlantsEmpty.value = plantList.isEmpty()
                _status.value = ApiStatus.DONE
                _error.value = ""
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _plants.value = listOf()
                handleError(e)
            }
        }
    }

    // GET PLANT TYPES
    fun getAvailablePlantTypes() {
        viewModelScope.launch {
            _addPlantStatus.value = ApiStatus.LOADING
            try {
                val plantTypes = PlantsApi.retrofitService
                    .getPlantTypes()
                _plantTypesNames.value = plantTypes.map { it.name }
                // _addPlantStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                _addPlantStatus.value = ApiStatus.ERROR
                handleError(e)
            }
        }
    }

    // ADD PLANT
    fun addPlant(name: String, plantType: String, bitmap: Bitmap, description: String, height: Double) {
        viewModelScope.launch {
            _addPlantStatus.value = ApiStatus.LOADING
            try {
                val base64Image = convertBitmapToBase64(bitmap).replace("\n", "").replace("\\n", "")
                PlantsApi.retrofitService.createPlant(PlantCreateRequest(
                    name, plantType, base64Image, description, height
                ))
                _addPlantStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                _addPlantStatus.value = ApiStatus.ERROR
                handleError(e)
            }
        }
    }

    // DELETE PLANT
    fun deletePlant(onSuccess: () -> Unit,
                    onFailure: () -> Unit) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                plant.value?.let {
                    PlantsApi.retrofitService.deletePlant(it.id)
                }
                _status.value = ApiStatus.DONE
                onSuccess()
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                onFailure()
            }
        }
    }

    // ADD PLANT NOTE
    fun addPlantNote(noteText: String, height: Double) {
        viewModelScope.launch {
            _addPlantNoteStatus.value = ApiStatus.LOADING
            try {
                plant.value?.let {
                    PlantsApi.retrofitService.createPlantNote(PlantNoteCreateRequest(
                        it.id, height, noteText
                    ))
                }
                _addPlantNoteStatus.value = ApiStatus.DONE
                _addPlantNoteStatus.value = ApiStatus.LOADING
            } catch (e: Exception) {
                _addPlantNoteStatus.value = ApiStatus.ERROR
                handleError(e)
            }
        }
    }

    // GET PLANT NOTES
    fun getMyPlantNotes() {
        viewModelScope.launch {
            _noteStatus.value = ApiStatus.LOADING
            try {
                plant.value?.let {
                    val notesList = PlantsApi.retrofitService
                        .getPlantGrowthLogs(it.id)
//                    notesList = listOf(
//                        PlantNote(1,"11-12-12", 99.99, "text"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(2,"1111-12-12", 939.1, "text1"),
//                        PlantNote(3,"111-12-12", 919.1, "text11"),
//                        PlantNote(4,"113-12-12", 99.99, "text11"),
//                    )
                    _plantNotes.value = notesList
                }
                _noteStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                _noteStatus.value = ApiStatus.ERROR
                _plantNotes.value = listOf()
                handleError(e)
            }
        }
    }



    // PLANT CLICKED
    fun onPlantClicked(plant: Plant) {
        _plant.value = plant
    }

    // NOTE CLICKED
    fun onNoteClicked(note: PlantNote) {
        _note.value = note
    }





    // HELPER FUNCTIONS

    // convert image to base64
    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Compress the image
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // handle error
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