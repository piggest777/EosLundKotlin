package se.eoslund.piggest.model

import android.graphics.Bitmap
import android.util.Log
import io.realm.*
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import se.eoslund.piggest.services.DataService
import se.eoslund.piggest.utilites.Constants.PLAYER_NUMBER
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE
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
            player: PlayerFSO,
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

        fun getAllPlayerFromBase(): RealmQuery<PlayerRO>? {
            val realm = Realm.getDefaultInstance()
            return realm.where(PlayerRO::class.java)
        }

        fun getPlayersFromLeague(league: String) : RealmResults<PlayerRO>{
            val realm = Realm.getDefaultInstance()

            return realm.where(PlayerRO::class.java)
                .equalTo(TEAM_LEAGUE, league)
                .sort(PLAYER_NUMBER, Sort.ASCENDING)
                .findAll()
            //TODO: map to FSOObj(look in pong)
        }
    }
}