package pl.andrzejressel.layerschecker

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.text.Spanned
import android.widget.TextView
import butterknife.bindView
import com.bitsyko.liblayers.Layer
import java.lang.ref.WeakReference

class LayerDetail : Activity() {

    public val textView: TextView by bindView(R.id.textview)

    var createLog: AsyncTask<Void, Spanned, Spanned>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val packageName = intent.getStringExtra("PackageName")

        val layer = Layer.layerFromPackageName(packageName, this)

        createLog = CreateLog(WeakReference(textView), layer).execute()

    }

    override fun onDestroy() {
        createLog?.cancel(true)
        super.onDestroy()
    }
}
