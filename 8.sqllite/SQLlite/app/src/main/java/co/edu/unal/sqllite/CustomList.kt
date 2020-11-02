package co.edu.unal.sqllite

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class CustomList(
    private val context: Activity,
    private val enterptises: MutableList<Enterprise>
) :
    ArrayAdapter<Enterprise?>(context, R.layout.list_layout, enterptises as List<Enterprise>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem: View = inflater.inflate(R.layout.list_layout, null, true)
        val textViewName = listViewItem.findViewById(R.id.textViewName) as TextView
        val textViewDesc = listViewItem.findViewById(R.id.textViewDesc) as TextView
        val image: ImageView = listViewItem.findViewById(R.id.imageView) as ImageView
        textViewName.text = enterptises[position].name
        textViewDesc.text = enterptises[position].products
        if(enterptises[position]!!.classification == "consultoria;"){ enterptises[position]!!.icon = R.drawable.suport}
        else if(enterptises[position]!!.classification == "desarollo;") {enterptises[position]!!.icon = R.drawable.dev}
        else if(enterptises[position]!!.classification == "fabricacion;") {enterptises[position]!!.icon = R.drawable.build}
        else {enterptises[position]!!.icon = R.drawable.human}
        enterptises[position].icon?.let { image.setImageResource(it) }
        return listViewItem
    }

}