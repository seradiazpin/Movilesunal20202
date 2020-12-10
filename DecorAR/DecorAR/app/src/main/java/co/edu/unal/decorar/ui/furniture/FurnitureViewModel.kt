package co.edu.unal.decorar.ui.furniture

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.edu.unal.decorar.models.Furniture

class FurnitureViewModel(selectedFurniture: Furniture) : ViewModel() {
    private val _furniture: MutableLiveData<Furniture> = MutableLiveData()
    val furniture: LiveData<Furniture> = _furniture

    init {
        _furniture.postValue(selectedFurniture)
    }
}