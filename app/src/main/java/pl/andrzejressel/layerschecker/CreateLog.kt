package pl.andrzejressel.layerschecker

import android.os.AsyncTask
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import com.bitsyko.liblayers.Commands
import com.bitsyko.liblayers.Layer
import com.bitsyko.liblayers.LayerFile
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.zip.ZipFile


class CreateLog(val textView: WeakReference<TextView>, val layer: Layer) : AsyncTask<Void, Spanned, Spanned>() {

    var text = ""
    val tempDir = layer.cacheDir + File.separator + layer.name
    val cacheFolder = File(tempDir)

    var i = 0

    //We'll update textView every "step" times
    val step = 8

    override fun doInBackground(vararg params: Void?): Spanned? {

        layer.close()

        val (colorLayers, generalLayers) = layer.layersInPackage.partition { e -> e.isColor }

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
            text += ("<br />")
            text += (createColor("white", "Color folders exist"))
            text += ("<br />")

            updateTextView()


            layer.colors.forEach { color ->

                text += ("<br />")
                text += (createColor("white", "Checking $color: "))

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

        return Html.fromHtml(text)
    }

    fun updateTextView() {
        publishProgress(Html.fromHtml(text))
    }

    override fun onProgressUpdate(vararg values: Spanned?) {
        // Log.d("LayersChecker", text)

        i++
        i %= step

        if (i == step - 1) {
            textView.get()?.setText(values[0], TextView.BufferType.SPANNABLE)
        }
    }

    fun createColor(color: String, data: String): String {
        return "<font color='$color'>$data</font>"
    }


    fun checkType(type: String, layers: List<LayerFile>) {

        val assetManager = layer.resources.assets

        var generalInputStream: InputStream? = null

        try {
            generalInputStream = assetManager.open(("Files" + File.separator + layer.name + "_" + type + ".zip").replace(" ".toRegex(), ""))
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

                if (isCancelled) {
                    return@forEach
                }

                val apkName = ("${layer.name}_${layerFile.name}.apk").replace(" ".toRegex(), "")

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

            zf.close()

            updateTextView()


        }
    }

    override fun onPostExecute(result: Spanned?) {
        textView.get()?.setText(result, TextView.BufferType.SPANNABLE)
        layer.close()
    }
}