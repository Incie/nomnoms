package rolf.nomnoms.nomnoms.dataaccess

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.provider.BaseColumns
import android.util.Log
import rolf.nomnoms.nomnoms.ModelNoms
import java.nio.file.Paths


object NomsDataAccessContract {
    const val SQL_CREATE_ENTRIES = "CREATE TABLE nomstable (${FeedEntry.COLUMN_ID} INTEGER PRIMARY KEY, ${FeedEntry.COLUMN_NAME_TITLE} TEXT NOT NULL, ${FeedEntry.COLUMN_NAME_SUBTITLE} TEXT NOT NULL)";
    const val SQL_CREATE_EVENTS = "CREATE TABLE nomsevent (${FeedEntry.COLUMN_ID} INTEGER PRIMARY KEY, FOREIGN KEY(${FeedEntry.COLUMN_NOMS_ID_KEY}) REFERENCES nomstable(${FeedEntry.COLUMN_ID}, ${FeedEntry.COLUMN_NAME_DATE} INTEGER)";
    const val SQL_CREATE_IMAGES = "CREATE TABLE nomsimage (key INTEGER PRIMARY KEY, FOREIGN KEY(nom_id) REFERENCES nomstable(nom_id), path TEXT NOT NULL"
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"

    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "nomstable"
        const val COLUMN_ID = "nom_id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
        const val COLUMN_NOMS_ID_KEY = "noms_fkey"
        const val COLUMN_NAME_DATE = "date"

        const val DATABASE_NAME = "noms.db"
        const val DB_PATH = "nomnoms"
    }

    fun dbPath() : String {
        val rootPath = Environment.getExternalStorageDirectory().toString()
        val dbPath = Paths.get(rootPath, FeedEntry.DB_PATH, FeedEntry.DATABASE_NAME).toString()
        Log.i("DbPath", dbPath)
        return dbPath
    }
}

class DataAccess(context: Context) : SQLiteOpenHelper(context, NomsDataAccessContract.dbPath(), null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 1
    }

    fun getAll() : List<ModelNoms> {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_NAME,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_ID, NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE, NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE),
            null,
            null,
            null,
            null,
            NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE)

        val items = mutableListOf<ModelNoms>()
        with(cursor) {
            while(moveToNext()){
                val itemId = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE))
                val subtitle = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE))
                items.add(ModelNoms(itemId, name, subtitle))
            }
        }

        readableDatabase!!.close()

        items.forEach( {modelNoms: ModelNoms -> Log.i("DataAccess", "[${modelNoms.itemId}, ${modelNoms.name}, ${modelNoms.subtitle}]") })

        return items;
    }

    fun insert(noms: ModelNoms) {
        val values = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE, noms.name)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE, noms.subtitle)
        }

        val insertRet = writableDatabase?.insert(NomsDataAccessContract.FeedEntry.TABLE_NAME, null, values)
        Log.i("DataAccess", "Insert returned $insertRet")
        writableDatabase!!.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(NomsDataAccessContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(NomsDataAccessContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}