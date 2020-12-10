package co.edu.unal.decorar.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.edu.unal.decorar.models.Furniture
import co.edu.unal.decorar.repositories.FurnitureRepository
import java.util.*

class CatalogViewModel() : ViewModel() {
    private lateinit var mFurnitures: MutableLiveData<List<Furniture>>
    private lateinit var mFurnitureFiltered: MutableLiveData<List<Furniture>>
    val furnitures: LiveData<List<Furniture>>
        get() = mFurnitureFiltered

    fun init(type: Int){
        mFurnitures = FurnitureRepository.getFurniture(type)
        mFurnitureFiltered = MutableLiveData()
        mFurnitureFiltered.postValue(mFurnitures.value)
    }
    fun search(s: String){
        val furnituresFiltered = mutableListOf<Furniture>()
        for( f in mFurnitures.value!!){
            if (f.nombre.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                furnituresFiltered.add(f)
            }
        }
        mFurnitureFiltered.postValue(furnituresFiltered)
    }
    fun filter(priceSt: String, material: String, marca:String, s: String){
        val furnituresFiltered = mutableListOf<Furniture>()
        val price = if(priceSt == "") Int.MAX_VALUE else priceSt.toInt()

        for( f in mFurnitures.value!!){
            val currPrice = f.precio!!.replace("[^0-9]".toRegex(), "").toInt()
            if (f.nombre.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT)) and
                f.material!!.toLowerCase(Locale.ROOT).contains(material.toLowerCase(Locale.ROOT)) and
                f.marca!!.toLowerCase(Locale.ROOT).contains(marca.toLowerCase(Locale.ROOT)) and
                (currPrice < price)) {
                furnituresFiltered.add(f)
            }
        }
        mFurnitureFiltered.postValue(furnituresFiltered)
    }
}