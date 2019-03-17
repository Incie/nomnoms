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
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT),
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
                val defaultImageId = getInt(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT))
                items.add(ModelNoms(itemId, name, subtitle, description, latestDate, defaultImageId))
            }
        }

        readableDatabase!!.close()

        items.forEach( {modelNoms: ModelNoms -> Log.i("DataAccess", "[${modelNoms.nomId}, ${modelNoms.name}, ${modelNoms.subtitle}]") })

        Log.i("DataAccess", "Got ${items.size} from db")
        return items
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
            NomsDataAccessContract.FeedEntry.COLUMN_NAME_DATE)

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

    fun getImagesForNomById(nomId: Int): List<NomImageModel> {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_IMAGE,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_ID,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGEPATH,
                NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY),
            "${NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY}=?",
            arrayOf(nomId.toString()),
            null,
            null,
            null)

        val imageList = ArrayList<NomImageModel>()
        with(cursor){
            while(moveToNext()){
                val imageId = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_ID))
                val path = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGEPATH))
                val nomid = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY))

                imageList.add(NomImageModel(imageId.toInt(), path, nomid.toInt()))
            }
        }

        return imageList
    }

    fun getNomById(nomId: Long) : ModelNoms? {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_NOMS,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_ID,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_LATEST_DATE,
                NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT),
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
                val defaultImageId = getInt(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT))
                modelNoms = ModelNoms(itemId, name, subtitle, description, latestDate, defaultImageId)
            }
        }

        readableDatabase!!.close()

        Log.i("DataAccess", "getNomById() nom title:'${modelNoms?.name}' from db")
        return modelNoms;
    }

    fun getNomEvents() : List<NomEventViewModel> {
        val cursor = readableDatabase!!.rawQuery(
            NomsDataAccessContract.SQL_RAWQUERY_GET_EVENTS,
            arrayOf()
        )

        val nomEventViewModels = ArrayList<NomEventViewModel>()
        with(cursor) {
            while(moveToNext()) {
                val eventId = getLong(getColumnIndexOrThrow("eventId"))
                val nomId = getLong(getColumnIndexOrThrow("nomId"))
                val title = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE))
                val subtitle = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE))
                val date = getLong(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DATE))
                val defaultImage = getInt(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT))

                val nomEvent = ModelNomEvent(eventId, nomId, date)
                val nom = ModelNoms(nomId, title, subtitle, "", 0, defaultImage)
                nomEventViewModels.add(NomEventViewModel(nom, nomEvent))
            }
        }

        return nomEventViewModels
    }

    fun getImagePathById(id:Int) : String {
        val cursor = readableDatabase!!.query(
            NomsDataAccessContract.FeedEntry.TABLE_IMAGE,
            arrayOf(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGEPATH),
            "${NomsDataAccessContract.FeedEntry.COLUMN_ID}=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var imagePath:String? = null
        with(cursor) {
            while(moveToNext()){
                imagePath = getString(getColumnIndexOrThrow(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGEPATH))
            }
        }

        readableDatabase!!.close()

        Log.i("DataAccess", "Got imagePath: ${imagePath!!} from db")
        return imagePath!!
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

    fun insertImage(path: String, nomId: Int):Int {
        val values = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGEPATH, path)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY, nomId)
        }
        val insertRet = writableDatabase?.insert(NomsDataAccessContract.FeedEntry.TABLE_IMAGE, null, values)
        return insertRet!!.toInt()
    }

    fun updateNoms(id: Long, title: String, subtitle: String, description: String){
        val updateValues = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_TITLE, title)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle)
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_DESCRIPTION, description)
        }

        val updatesReturn = writableDatabase!!.update(
            NomsDataAccessContract.FeedEntry.TABLE_NOMS,
            updateValues,
            "${NomsDataAccessContract.FeedEntry.COLUMN_ID}=?",
            arrayOf(id.toString()) )

        Log.i("DataAccess", "updateNoms() update() returned $updatesReturn")
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

        writableDatabase.close()

        Log.i("DataAccess", "Updated id $id with $date")
    }

    private fun deleteIdFromTable(nomId: Long, table: String, column: String){
        val deletedItems = writableDatabase.delete(
            table,
            "$column=?",
            arrayOf(nomId.toString()))

        Log.i("DataAccess", "Deleted $deletedItems (id $nomId) from $table")
    }

    fun deleteNomById(id: Long){
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_IMAGE, NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY)
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_EVENT, NomsDataAccessContract.FeedEntry.COLUMN_NOMS_ID_KEY)
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_NOMS, NomsDataAccessContract.FeedEntry.COLUMN_ID)
    }

    fun deleteEventById(id: Long){
        deleteIdFromTable(id, NomsDataAccessContract.FeedEntry.TABLE_EVENT, NomsDataAccessContract.FeedEntry.COLUMN_ID)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(NomsDataAccessContract.SQL_CREATE_ENTRIES)
        db.execSQL(NomsDataAccessContract.SQL_CREATE_IMAGES)
        db.execSQL(NomsDataAccessContract.SQL_CREATE_EVENTS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        if( oldVersion == 10 && newVersion == 11 ){
            db!!.execSQL( "ALTER TABLE ${NomsDataAccessContract.FeedEntry.TABLE_NOMS} ADD COLUMN ${NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT} INTEGER DEFAULT -1" )
        } else {
            db!!.execSQL(NomsDataAccessContract.SQL_DELETE_ENTRIES)
            db.execSQL(NomsDataAccessContract.SQL_DELETE_IMAGES)
            db.execSQL(NomsDataAccessContract.SQL_DELETE_EVENTS)
            onCreate(db)
        }
    }

    fun setDefaultImage(nomId: Int, imageId: Int) {
        val updateValues = ContentValues().apply {
            put(NomsDataAccessContract.FeedEntry.COLUMN_NAME_IMAGE_DEFAULT, imageId)
        }

        writableDatabase!!.update(
            NomsDataAccessContract.FeedEntry.TABLE_NOMS,
            updateValues,
            "${NomsDataAccessContract.FeedEntry.COLUMN_ID}=?",
            arrayOf(nomId.toString()) )

        Log.i("DataAccess", "Updated id $nomId with defaultImageId $imageId")
    }
}