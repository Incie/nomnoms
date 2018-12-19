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

        Log.i("DataAccess", "Got ${items.size} from db")
        return items;
    }

    fun getEventsByNomId(nomId: Long) : List<ModelNomEvent> {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_EVENT,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_ID,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_DATE),
            "${NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY}=?",
            arrayOf(nomId.toString()),
            null,
            null,
            NomsDataAccessContract.FeedEntry.COLUMN_NAME_DATE);

        val eventList = ArrayList<ModelNomEvent>()
        with(cursor){
            while(moveToNext()){
                val eventId = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_ID))
                val date = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DATE))

                eventList.add( ModelNomEvent(eventId, nomId, date))
            }
        }

        return eventList
    }

    fun getNomById(nomId: Long) : ModelNoms? {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_NOMS,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_ID,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE),
            "${NomsDataAccessContract.FeedEntry.COLUMN_ID}=?",
            arrayOf(nomId.toString()),
            null,
            null,
            NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE
        )

        var modelNoms:ModelNoms? = null
        with(cursor) {
            while(moveToNext()){
                val itemId = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE))
                val subtitle = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE))
                val description = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION))
                val latestDate = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE))
                modelNoms = ModelNoms(itemId, name, subtitle, description, latestDate)
            }
        }

        readableDatabase!!.close()

        Log.i("DataAccess", "Got ${modelNoms?.name} from db")
        return modelNoms;
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

        Log.i("DataAccess", "Updated id $id with $date")
    }

    private fun deleteIdFromTable(nomId: Long, table: String, column: String){
        val deletedItems = writableDatabase.delete(
            table,
            "${column}=?",
             arrayOf(nomId.toString()))
        Log.i("DataAccess", "Deleted $deletedItems (nomId $nomId) from $table")
    }

    fun deleteNomById(id: Long){
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_IMAGE, NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY)
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_EVENT, NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY)
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_NOMS, NomsDataAccessContract.FeedEntry.COLUMN_ID)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(NomsDataAccessContract.SQL_CREATE_ENTRIES)
        db.execSQL(NomsDataAccessContract.SQL_CREATE_IMAGES)
        db.execSQL(NomsDataAccessContract.SQL_CREATE_EVENTS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(NomsDataAccessContract.SQL_DELETE_ENTRIES)
        db.execSQL(NomsDataAccessContract.SQL_DELETE_IMAGES)
        db.execSQL(NomsDataAccessContract.SQL_DELETE_EVENTS)
        onCreate(db)
    }
}