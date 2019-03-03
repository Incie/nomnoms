package rolf.nomnoms.nomnoms.dataaccess

import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.support.annotation.RequiresApi
import android.util.Log
import java.io.File
import java.nio.file.Paths

object NomsDataAccessContract {
    private const val DATABASE_VERSION = 11
    private const val DATABASE_DEBUG_VERSION = 1

    const val useDebugDatabase = false

    fun dbVersion() : Int {
        if( useDebugDatabase )
            return DATABASE_DEBUG_VERSION
        return DATABASE_VERSION
    }

    fun dbPath() : String {
        val rootPath = Environment.getExternalStorageDirectory().toString()
            val dbName = if(useDebugDatabase) FeedEntry.DATABASE_NAME_DEBUG else FeedEntry.DATABASE_NAME

            val dbPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Paths.get(rootPath,
                    FeedEntry.DB_PATH,
                    dbName)
                    .toString()
        } else {
            val dbFile = File(File(rootPath, FeedEntry.DB_PATH), dbName)
            Log.i("DbPath", dbFile.toString())
            return dbFile.toString()
        }

        Log.i("DbPath", dbPath)
        return dbPath
    }

    const val SQL_CREATE_ENTRIES = "CREATE TABLE nomstable ( ${FeedEntry.COLUMN_ID} INTEGER PRIMARY KEY, " +
            "${FeedEntry.COLUMN_NAME_TITLE} TEXT NOT NULL, " +
            "${FeedEntry.COLUMN_NAME_SUBTITLE} TEXT NOT NULL, " +
            "${FeedEntry.COLUMN_NAME_LATEST_DATE} INTEGER, " +
            "${FeedEntry.COLUMN_NAME_DESCRIPTION} TEXT NOT NULL)" +
            "${FeedEntry.COLUMN_NAME_IMAGE_DEFAULT} INTEGER DEFAULT -1"

    const val SQL_CREATE_EVENTS = "CREATE TABLE nomsevent (${FeedEntry.COLUMN_ID} INTEGER PRIMARY KEY, " +
            "${FeedEntry.COLUMN_NOMS_ID_KEY} INTEGER NOT NULL, " +
            "${FeedEntry.COLUMN_NAME_DATE} INTEGER, " +
            "FOREIGN KEY(${FeedEntry.COLUMN_NOMS_ID_KEY}) REFERENCES nomstable(${FeedEntry.COLUMN_ID}) )"

    const val SQL_CREATE_IMAGES = "CREATE TABLE nomsimage (${FeedEntry.COLUMN_ID} INTEGER PRIMARY KEY, " +
            "${FeedEntry.COLUMN_NOMS_ID_KEY} INTEGER NOT NULL, " +
            "${FeedEntry.COLUMN_NAME_IMAGEPATH} TEXT NOT NULL, " +
            "FOREIGN KEY(${FeedEntry.COLUMN_NOMS_ID_KEY}) REFERENCES nomstable(${FeedEntry.COLUMN_ID}) )"

    const val SQL_RAWQUERY_GET_EVENTS = "SELECT t_event.${FeedEntry.COLUMN_ID}, " +
            "t_noms.${FeedEntry.COLUMN_ID}, " +
            "t_noms.${FeedEntry.COLUMN_NAME_TITLE}, " +
            "t_noms.${FeedEntry.COLUMN_NAME_SUBTITLE}, " +
            "t_noms.${FeedEntry.COLUMN_NAME_IMAGE_DEFAULT}, " +
            "t_event.${FeedEntry.COLUMN_NAME_DATE} FROM " +
            "${FeedEntry.TABLE_EVENT} t_event " +
            "INNER JOIN ${FeedEntry.TABLE_NOMS} t_noms " +
            "ON t_event.${FeedEntry.COLUMN_NOMS_ID_KEY}=t_noms.${FeedEntry.COLUMN_ID} " +
            "WHERE t_noms.${FeedEntry.COLUMN_ID} = t_event.${FeedEntry.COLUMN_NOMS_ID_KEY} " +
            "ORDER BY t_event.${FeedEntry.COLUMN_NAME_DATE} DESC"


    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NOMS}"
    const val SQL_DELETE_EVENTS = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_EVENT}"
    const val SQL_DELETE_IMAGES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_IMAGE}"

    object FeedEntry : BaseColumns {
        const val TABLE_NOMS = "nomstable"
        const val TABLE_EVENT = "nomsevent"
        const val TABLE_IMAGE = "nomsimage"

        const val COLUMN_ID = "nom_id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_LATEST_DATE = "latest_date"
        const val COLUMN_NAME_IMAGE_DEFAULT = "image_default"

        const val COLUMN_NOMS_ID_KEY = "noms_fkey"
        const val COLUMN_NAME_DATE = "date"

        const val COLUMN_NAME_IMAGEPATH = "image_path"
        const val DB_PATH = "nomnoms"
        const val IMAGE_PATH = "nomnoms/images"
        const val DATABASE_NAME = "noms.db"
        const val DATABASE_NAME_DEBUG = "noms_dbg.db"
    }
}