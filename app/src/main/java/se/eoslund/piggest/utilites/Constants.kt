package se.eoslund.piggest.utilites

import se.eoslund.piggest.model.TeamFSO

object Constants {

    //team const
    const val TEAM_NAME = "teamName"
    const val TEAM_CITY = "teamCity"
    const val HOME_ARENA = "homeArena"
    const val LOGO_PATH_NAME = "logoPathName"
    const val DEFAULT_TEAM_LOGO = "defaultTeamLogo.png"
    const val TEAM_INFO_UPDATE_DATE = "teamInfoUpdateDate"

    const val SBLD_LEAGUE = "SBLD"
    const val SE_HERR_LEAGUE = "SE Herr"
    const val BE_DAM_LEAGUE = "BE Dam"
    const val ALL_LEAGUES = "All"

    const val EOS_CODE = "eosCode"
    const val OPPOSITE_TEAM_CODE = "oppositeTeamCode"
    const val IS_HOME_GAME = "isHomeGame"
    const val EOS_SCORES = "eosScores"
    const val OPPOSITE_TEAM_SCORES = "oppositeTeamScores"
    const val GAME_DATE_AND_TIME = "gameDateAndTime"
    const val EOS_PLAYERS = "eosPlayers"
    const val OPPOSITE_TEAM_PLAYERS = "oppositeTeamPlayers"
    const val TEAM_LEAGUE = "teamLeague"
    const val GAME_DESCRIPTION = "gameDescription"
    const val STATISTIC_LINK = "statsLink"
    const val GAME_COVER_URL = "gameCoverUrl"

    const val PLAYER_NAME = "playerName"
    const val PLAYER_NUMBER = "playerNumber"
    const val PLAYER_POSITION = "playerPosition"
    const val PLAYER_IMAGE_URL = "playerImageURL"
    const val PLAYER_UPDATE_DATE = "playerUpdateDate"
    const val DAY_OF_BIRTH = "dayOfBirth"
    const val PLAYER_HEIGHT = "playerHeight"
    const val PLAYER_NATIONALITY = "playerNationality"
    const val PLAYER_ORIGINAL_CLUB = "playerOriginalClub"
    const val PLAYER_IN_EOS_FROM  = "playerInEOSFrom"
    const val PLAYER_BIG_IMAGE_URL = "playerBigImageUrl"

//    URLS
    const val DEFAULT_IMAGE_URL = "gs://eoslund-4ceb4.appspot.com/defaultAvatar.png"
    const val EOS_WEB_BASE_URL = "https://www.eoslund.se/"
    const val EOS_NEWS_URL = "${EOS_WEB_BASE_URL}om-eos/nyheter?page="

    //refs
    const val GAMES_REF = "games"
    const val BASE_UPDATE_DATE = "baseUpdateDate"
    const val PLAYER_BASE_UPDATE_DATE = "playersBaseUpdateDate"
    const val DATE = "date"
    const val PLAYERS_REF = "players"


    val EOS_TEAM: TeamFSO = TeamFSO("Eos Basket","Eos Basket", "Lund", "Eoshallen","eos_logo")


    //youtube API
    const val YT_API_KEY = "AIzaSyBxQ4uUEYTfTBp72F4EJzYueGLZa3v7Kmc"
    const val YT_CHANNEL_ID = "UCfoQAv5xCoEEEutwU998--w"
    const val YT_PART = "snippet,id"
    const val YT_ORDER = "date"
}