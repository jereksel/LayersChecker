package pl.andrzejressel.layerschecker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bitsyko.liblayers.Layer

public class LayersAdapter(context: Context, val resource: Int, val layers: MutableList<Layer>) : ArrayAdapter<Layer>(context, resource, layers) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val rowView = inflater.inflate(resource, parent, false)

        val image = rowView.findViewById(R.id.iv_themeImage) as ImageView
        val name = rowView.findViewById(R.id.txtName) as TextView
        val author = rowView.findViewById(R.id.txtSurname) as TextView


        image.setImageDrawable(layers[position].icon)
        name.text = layers[position].name
        author.text = layers[position].developer


        return rowView
    }


}