package se.eoslund.piggest.model

import android.graphics.Bitmap
import android.util.Log
import io.realm.*
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import se.eoslund.piggest.services.DataService
import java.util.*

open class PlayerRO(

    @PrimaryKey
    var id: String = "",

    var image: ByteArray? = null,
    var bigImage: String? = "",
    var updateDate: Date = Date(),
    var height: String? = "",
    var dayOfBirth: String? = "",
    var nationality: String? = "",
    var originalClub: String? = "",
    var inEosFrom: String? = "",
    @Required
    var name: String = "",
    var position: String = "",
    var league: String = "",
    @Index
    var number: Int = 0,
) : RealmObject() {

    companion object {

        fun updatePlayerInBase(
            player: Player,
            playerImage: Bitmap,
            completionHandler: (Boolean) -> Unit
        ) {
            val pObject = PlayerRO()

            pObject.id = player.id
            pObject.name = player.name
            pObject.position = player.position
            pObject.image = DataService.pngToByteArray(playerImage)
            pObject.league = player.league
            pObject.updateDate = Date()
            pObject.height = player.height
            pObject.dayOfBirth = player.dateOfBirth
            pObject.nationality = player.nationality
            pObject.originalClub = player.originalClub
            pObject.inEosFrom = player.inEosFrom
            pObject.bigImage = player.bigImageURL
            pObject.number = player.number.toInt()

            val realm = Realm.getDefaultInstance()

            realm.executeTransactionAsync({ transaction ->
                transaction.insertOrUpdate(pObject)
            }, {
                completionHandler(true)
                Log.v("REALM", "Successfully completed the transaction for ${pObject.name}")
            }, { error ->
                completionHandler(false)
                Log.e("REALM", "Failed the transaction: $error, ${pObject.name}")
            })
        }

        fun addAllPlayersWithCallback(playerList: List<PlayerRO>, completion: (Boolean) -> Unit) {
            val realm: Realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync({ transactionRealm ->
                playerList.forEach {
                    transactionRealm.insertOrUpdate(it)
                }
            }, {
                completion(true)
                Log.v("REALM", "Successfully completed player transaction")
            }, { error ->
                completion(false)
                Log.e("REALM", "Failed player transaction: $error")
            })
        }

        fun getAllPlayerFromBase(): MutableList<PlayerRO> {
            val realm = Realm.getDefaultInstance()
            val results = realm.where(PlayerRO::class.java).findAll()
            return realm.copyFromRealm(results)
        }

        fun getPlayersFromLeague(league: String) : MutableList<PlayerRO> {
            val realm = Realm.getDefaultInstance()

            val results =  realm.where(PlayerRO::class.java)
                .equalTo("league", league)
                .sort("number", Sort.ASCENDING)
                .findAll()
            return realm.copyFromRealm(results)
        }

        fun deletePlayerById(id: String) {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction { r: Realm ->
                r.where(PlayerRO::class.java)
                    .equalTo("id", id)
                    .findFirst()?.deleteFromRealm()
            }
        }

        fun findPlayerById(id: String): PlayerRO {
            val realm = Realm.getDefaultInstance()

            return realm.where(PlayerRO::class.java)
                .equalTo("id", id)
                .findFirst() ?: PlayerRO()
        }


//        private fun mapPlayer(playerRO: PlayerRO) : PlayerFSO{
//            val pImage = DataService.byteArrayToImage(playerRO.image)
//            return PlayerFSO (
//                playerRO.name,
//                playerRO.number,
//                playerRO.dayOfBirth,
//                playerRO.height,
//                playerRO.position,
//                playerRO.id,
//                playerRO.imageUrl,
//                playerRO.league,
//                playerRO.updateDate,
//                playerRO.nationality,
//                playerRO.originalClub,
//                playerRO.inEosFrom,
//                playerRO.bigImage
//            )
//        }
    }
}