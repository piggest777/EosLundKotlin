package se.eoslund.piggest.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream

object DataService {

    fun Context.getResource(name:String): Drawable? {
        val resID = this.resources.getIdentifier(name , "drawable", this.packageName)

        return if (resID == 0) {
            null
        } else {
            ActivityCompat.getDrawable(this,resID)
        }
    }

    fun pngToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }



}