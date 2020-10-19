package co.edu.unal.tictactoe

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class CustomList(
    private val context: Activity,
    private val names: MutableList<String>,
    private val desc: MutableList<Int>,
    private val imageid: MutableList<Int>
) :
    ArrayAdapter<String?>(context, R.layout.list_layout, names as List<String?>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem: View = inflater.inflate(R.layout.list_layout, null, true)
        val textViewName = listViewItem.findViewById(R.id.textViewName) as TextView
        val textViewDesc = listViewItem.findViewById(R.id.textViewDesc) as TextView
        val image: ImageView = listViewItem.findViewById(R.id.imageView) as ImageView
        textViewName.text = names[position]
        textViewDesc.text = "Players: "+desc[position].toString()+ "/2"
        image.setImageResource(imageid[position])
        return listViewItem
    }

}