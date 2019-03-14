package rolf.nomnoms.nomnoms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_noms_view.*
import kotlinx.coroutines.*
import rolf.nomnoms.nomnoms.adapter.AdapterNomEvents
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNomEvent
import rolf.nomnoms.nomnoms.model.ModelNoms
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class ActivityNomsView : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    private var nomId = -1L

    private lateinit var model: ModelNoms
    private var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_view)

        nomId = intent.getLongExtra("nom_id", -1L)

        if( nomId == -1L ){
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_events)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        camera.setOnClickListener { startCamera() }

        gallery!!.setOnClickListener(this::onGalleryImageClick)
    }

    override fun onStart(){
        super.onStart()
        setupData()
    }

    private fun setupData(){
        this.launch {
            var imagePath:String? = null
            var nomEvents: List<ModelNomEvent>? = null;
            val backgroundTask = async(Dispatchers.Default){
                val dataAccess = DataAccess(this@ActivityNomsView )
                val modelNoms = dataAccess.getNomById(nomId)

                if( modelNoms == null ){
                    Toast.makeText(this@ActivityNomsView, "Could not find nom by id $nomId", Toast.LENGTH_SHORT).show()
                    finish()
                }

                model = modelNoms!!

                nomEvents = dataAccess.getEventsByNomId(nomId)

                if( model.defaultImage >= 0 )
                    imagePath = dataAccess.getImagePathById(model.defaultImage)
            }

            backgroundTask.await()

            recyclerview_events.adapter = AdapterNomEvents(this@ActivityNomsView, nomEvents!!.reversed() )
            textview_name.text = model.name
            textview_subtitle_nom.text = model.subtitle
            textview_description_noms.text = model.description

            if( imagePath != null )
                Glide.with(this@ActivityNomsView).load(imagePath).into(gallery!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext[Job]!!.cancel()
    }

    private fun onGalleryImageClick(view: View ){
        val intent = Intent(this@ActivityNomsView, ActivityGallery::class.java)
        intent.putExtra("nom_id", model.itemId)
        startActivityForResult(intent, 665)
    }

    private fun startCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("ActivityNomsView", "$ex")
                    Toast.makeText(this, "${ex.message}", Toast.LENGTH_SHORT).show()
                    null
                }

                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", it)
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(pictureIntent, 1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == 1 ) {
            if( resultCode != Activity.RESULT_OK ){
                Toast.makeText(this, "No Image Returned from Camera", Toast.LENGTH_SHORT).show()
                deleteFile(mCurrentPhotoPath)
                Log.i("Photo", "Deleted $mCurrentPhotoPath")
                return
            }

            Log.e("Photo", "$mCurrentPhotoPath resultCode: $resultCode ${Activity.RESULT_OK} = OK")

            val dataAccess = DataAccess(this)
            val imageId = dataAccess.insertImage(mCurrentPhotoPath, model.itemId.toInt() )

            if( model.defaultImage < 0 ) {
                dataAccess.setDefaultImage(model.itemId.toInt(), imageId)
                Glide.with(this).load(mCurrentPhotoPath).into(gallery!!)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val rootPath = Environment.getExternalStorageDirectory().toString()
        val path = Paths.get(rootPath,"nomnoms/images").toFile()

        if( !path.exists() ) {
            path.mkdir()
            Log.i("ActivityNomsView", "Created path $path")
        }

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", path).apply { mCurrentPhotoPath = absolutePath }
    }
}
