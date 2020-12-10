package co.edu.unal.decorar.ui.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unal.decorar.R
import co.edu.unal.decorar.models.Furniture
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_furniture_item.view.*

class CatalogRecyclerViewAdapter(private var onFurnitureListener: OnFurnitureListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var items: MutableList<Furniture> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FurnitureViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_furniture_item, parent, false),
            onFurnitureListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FurnitureViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(furnitureList: List<Furniture> ){
        items.clear()
        items.addAll(furnitureList)
    }

    class FurnitureViewHolder constructor(
        itemView: View,
        var onFurnitureListener: OnFurnitureListener
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val itemImage: ImageView = itemView.catalog_item_image
        val itemName: TextView = itemView.catalog_item_text
        val itemPrice: TextView = itemView.catalog_item_price
        fun bind(furniture: Furniture){
            itemName.text = furniture.nombre
            itemPrice.text = furniture.precio
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(furniture.foto)
                .into(itemImage)

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onFurnitureListener.onFurnitureClick(adapterPosition)
        }
    }

     interface OnFurnitureListener{
             fun onFurnitureClick(position: Int)
     }
}
