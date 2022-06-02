package se.eoslund.piggest.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import se.eoslund.piggest.utilites.Constants.DAY_OF_BIRTH
import se.eoslund.piggest.utilites.Constants.PLAYER_BIG_IMAGE_URL
import se.eoslund.piggest.utilites.Constants.PLAYER_HEIGHT
import se.eoslund.piggest.utilites.Constants.PLAYER_IMAGE_URL
import se.eoslund.piggest.utilites.Constants.PLAYER_IN_EOS_FROM
import se.eoslund.piggest.utilites.Constants.PLAYER_NAME
import se.eoslund.piggest.utilites.Constants.PLAYER_NATIONALITY
import se.eoslund.piggest.utilites.Constants.PLAYER_NUMBER
import se.eoslund.piggest.utilites.Constants.PLAYER_ORIGINAL_CLUB
import se.eoslund.piggest.utilites.Constants.PLAYER_POSITION
import se.eoslund.piggest.utilites.Constants.PLAYER_UPDATE_DATE
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE
import java.util.*

class Player(
    var name: String,
    var number: Long,
    var dateOfBirth: String?,
    var height: String?,
    var position: String,
    var id: String,
    var imageUrl: String?,
    var league: String,
    var updateDate: Date,
    var nationality: String?,
    var originalClub: String?,
    var inEosFrom: String?,
    var bigImageURL: String?
) {

    companion object{
        private val storage = Firebase.storage
        val fbStorage = storage.reference

        fun parsePlayer( snapshot: DocumentSnapshot) : Player? {
            val data = snapshot.data ?: return null

            val id  = snapshot.id
            val name = data[PLAYER_NAME] as? String
            val number = data[PLAYER_NUMBER] as? Long
            val position = data[PLAYER_POSITION] as? String
            val imageUrl = data[PLAYER_IMAGE_URL] as? String
            val bigImageUrl = data[PLAYER_BIG_IMAGE_URL] as? String
            val timeStampUpdateDate = data[PLAYER_UPDATE_DATE] as? Timestamp ?: Timestamp(Date())
            val javaDate = timeStampUpdateDate.toDate()
            val league = data[TEAM_LEAGUE] as? String
            val height = data[PLAYER_HEIGHT] as? String
            val dateOfBirth = data[DAY_OF_BIRTH] as? String
            val nationality = data[PLAYER_NATIONALITY] as? String
            val inEosFrom = data[PLAYER_IN_EOS_FROM] as? String
            val originalClub = data[PLAYER_ORIGINAL_CLUB] as? String

            if (name != null && number != null && league != null && position != null ) {
                return Player(
                    name,
                    number,
                    dateOfBirth,
                    height,
                    position,
                    id,
                    imageUrl,
                    league,
                    javaDate,
                    nationality,
                    originalClub,
                    inEosFrom,
                    bigImageUrl
                )
            }else {
                return null
            }
        }

        fun parsePlayers(snapshot: QuerySnapshot) : MutableList<Player> {
            val players = mutableListOf<Player>()

            snapshot.forEach { it->
                parsePlayer(it)?.let {
                    players.add(it)
                }
            }

            return players
        }

        fun getPlayerImage(imageUrl: String, completionHandler: (Boolean, ByteArray?)-> Unit) {

            val gsReference = storage.getReferenceFromUrl(imageUrl)
            val ONE_MEGABYTE: Long = 1024 * 1024
            gsReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener { returnedByteArray ->
                    Log.d("Storage", "succesfully getted image fo url $imageUrl")
                completionHandler(true, returnedByteArray)
            }.addOnFailureListener {
                Log.d("STORAGE", "can`t load image from storage")
                completionHandler(false, null)
            }

        }
    }




}