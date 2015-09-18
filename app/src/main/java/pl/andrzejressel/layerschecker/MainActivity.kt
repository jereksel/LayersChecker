package pl.andrzejressel.layerschecker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import butterknife.bindView
import com.bitsyko.liblayers.Layer

public class MainActivity : Activity() {

    val listView: ListView by bindView(R.id.listview)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layersList = Layer.getLayersInSystem(this)


        val layersAdapter = LayersAdapter(this, R.layout.layer_row, layersList)
        layersAdapter.sort({ layer1, layer2 -> layer1.name.compareTo(layer2.name, ignoreCase = true) })


        listView.adapter = layersAdapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->

            val intent = Intent(this, LayerDetail::class.java)
            intent.putExtra("PackageName", layersList[position].packageName)

            startActivity(intent);
        }

    }
    /*
        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            val id = item.itemId

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true
            }

            return super.onOptionsItemSelected(item)
        }
        */
}
