package pl.andrzejressel.layerschecker

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import butterknife.bindView
import com.bitsyko.liblayers.Commands
import com.bitsyko.liblayers.Layer
import com.bitsyko.liblayers.LayerFile
import org.apache.commons.io.FileUtils
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class LayerDetail : Activity() {

    public val textView: TextView by bindView(R.id.textview)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val packageName = intent.getStringExtra("PackageName")

        val layer = Layer.layerFromPackageName(packageName, this)

        CreateLog(textView, layer).execute()

    }


    class CreateLog(val textView: TextView, val layer: Layer) : AsyncTask<Void, Spanned, Void>() {

        var text = ""


        val tempDir = layer.cacheDir + File.separator + layer.name


        override fun doInBackground(vararg params: Void?): Void? {

            layer.close()

            val (colorLayers, generalLayers) = layer.layersInPackage.partition { e -> e.isColor }

            // publishProgress(createColor("green", "TESTING"))
            // publishProgress(createColor("white", "TESTING123"))


            val tempDir = layer.cacheDir + File.separator + layer.name

            //We're creating cache directory
            val cacheFolder = File(tempDir)

            if (!cacheFolder.exists()) {
                cacheFolder.mkdirs()
            }



            //Checking general

            if (!generalLayers.isEmpty()) {
                text += (createColor("white", "General folder exist"))
                text += ("<br />")
                text += (createColor("white", "Checking general folder: "))

                updateTextView()

                checkType("General", generalLayers)

            } else {
                text += (createColor("white", "No General folder"))
                text += ("<br />")
            }

            updateTextView()


            if (!colorLayers.isEmpty()) {
                text += (createColor("white", "Color folders exist"))
                text += ("<br />")

                updateTextView()


                layer.colors.forEach { color ->

                    text += (createColor("white", "Checking $color: "))
                    //publishProgress("<br />")

                    checkType(color, colorLayers)

                    updateTextView()


                }


            } else {
                text += (createColor("white", "No color folders"))
                text += ("<br />")
            }

            updateTextView()

            text += ("<br />")
            text += (createColor("white", "Finished"))
            text += ("<br />")

            updateTextView()

            return null
        }

        fun updateTextView() {
            publishProgress(Html.fromHtml(text))
        }

        override fun onProgressUpdate(vararg values: Spanned?) {
            Log.d("LayersChecker", text)
            textView.setText(values[0], TextView.BufferType.SPANNABLE)

        }

        fun createColor(color: String, data: String): String {
            return "<font color='$color'>$data</font>"
        }


        fun checkType(type: String, layers: List<LayerFile>) {

            val cacheFolder = File(tempDir)

            val assetManager = layer.resources.assets

            var generalInputStream: InputStream? = null

            try {
                generalInputStream = assetManager.open(("Files" + File.separator + layer.name + "_" + type + ".zip").replaceAll(" ", ""))
            } catch (e: IOException) {
                //No general folder
                text += (createColor("red", "Failed"))
                text += ("<br />")
            }

            updateTextView()

            if (generalInputStream != null) {
                //We have general folder, we're gonna check every file there

                text += (createColor("green", "found"))
                text += ("<br />")


                val zipFile = File(tempDir + File.separator + layer.name + "_" + type + ".zip")
                FileUtils.copyInputStreamToFile(generalInputStream, zipFile)

                var zf: ZipFile = ZipFile(zipFile)

                layers.forEach { layerFile ->

                    val apkName = (layer.name + "_" + layerFile.name + ".apk").replaceAll(" ", "")

                    text += (createColor("white", "Checking $apkName: "))

                    var ze = zf.getEntry(apkName)

                    if (ze == null) {
                        //publishProgress(createColor("red", "Error"))

                        val similarFile = Commands.getSimilarFileFromZip(zf, apkName)

                        if (similarFile == null) {
                            text += (createColor("red", "Error"))
                        } else {
                            text += (createColor("yellow", "Warning, found file with similar name $similarFile"))
                        }


                    } else {
                        text += (createColor("green", "found"))
                    }

                    text += ("<br />")


                    updateTextView()


                }

                updateTextView()


            }
        }

        override fun onPostExecute(result: Void?) {
            layer.close()
        }
    }
}
