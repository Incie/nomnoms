package rolf.nomnoms.nomnoms.dataaccess

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import rolf.nomnoms.nomnoms.model.*

class DataAccess(context: Context) : SQLiteOpenHelper(context, NomsDataAccessContract.dbPath(), null, NomsDataAccessContract.dbVersion()) {
    init {
        writableDatabase!!.setForeignKeyConstraintsEnabled(true)
        writableDatabase.close()
    }

    fun getAll() : List<ModelNoms> {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_NOMS,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_ID,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE),
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
                val description = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION))
                val latestDate = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE))
                items.add(ModelNoms(itemId, name, subtitle, description, latestDate))
            }
        }

        readableDatabase!!.close()

        items.forEach( {modelNoms: ModelNoms -> Log.i("DataAccess", "[${modelNoms.itemId}, ${modelNoms.name}, ${modelNoms.subtitle}]") })

        return items;
    }

    fun insertNomEvent(nomEvent: ModelNomEvent){
        val values = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DATE, nomEvent.date)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY, nomEvent.nomId)
        }
        val insertRet = writableDatabase?.insert(NomsDataAccessContract.FeedEntry.TABLE_EVENT, null, values)
        writableDatabase!!.close()

        Log.i("DataAccess", "Insert returned $insertRet")
    }

    fun insertNoms(noms: ModelNoms) {
        val values = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE, noms.name)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE, noms.subtitle)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION, noms.description)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE, noms.latestDate)
        }

        val insertRet = writableDatabase?.insert(NomsDataAccessContract.FeedEntry.TABLE_NOMS, null, values)
        writableDatabase!!.close()

        Log.i("DataAccess", "Insert returned $insertRet")
    }

    fun updateLatestNomDate(id: Long, date: Long){
        val updateValues = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE, date)
        }

        writableDatabase!!.update(
            NomsDataAccessContract.FeedEntry.TABLE_NOMS,
            updateValues,
            "${NomsDataAccessContract.FeedEntry.COLUMN_ID}=?",
            arrayOf(id.toString()) )
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(NomsDataAccessContract.SQL_CREATE_ENTRIES)
        db!!.execSQL(NomsDataAccessContract.SQL_CREATE_IMAGES)
        db!!.execSQL(NomsDataAccessContract.SQL_CREATE_EVENTS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(NomsDataAccessContract.SQL_DELETE_ENTRIES)
        db!!.execSQL(NomsDataAccessContract.SQL_DELETE_IMAGES)
        db!!.execSQL(NomsDataAccessContract.SQL_DELETE_EVENTS)
        onCreate(db)
    }
}