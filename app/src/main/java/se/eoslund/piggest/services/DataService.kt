package se.eoslund.piggest.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import se.eoslund.piggest.R
import se.eoslund.piggest.controller.App
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

    fun byteArrayToImage(byteArray: ByteArray?) : Bitmap {
        return if (byteArray != null) {
            BitmapFactory.decodeByteArray(byteArray,0, byteArray.size)
        } else {
            val defaultPlayerImage =  BitmapFactory.decodeResource(App.instance.resources, R.drawable.default_avatar)!!
            defaultPlayerImage
        }
    }
}