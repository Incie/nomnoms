package rolf.nomnoms.nomnoms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import rolf.nomnoms.nomnoms.R.id.gallery
import rolf.nomnoms.nomnoms.adapter.AdapterNomEvents
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.ModelNoms
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

class ActivityNomsView : AppCompatActivity() {

    private var nomId = -1L
    private var galleryImage : ImageView? = null

    private var model: ModelNoms? = null
    private var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_view)

        nomId = intent.getLongExtra("nom_id", -1L)

        if( nomId == -1L ){
            finish()
            return
        }

        model = DataAccess(this).getNomById(nomId)!!

        if( model == null ){
            finish()
            return
        }

        findViewById<TextView>(R.id.textview_name).text = model!!.name
        findViewById<TextView>(R.id.textview_subtitle_nom).text = model!!.subtitle
        findViewById<TextView>(R.id.textview_description_noms).text = model!!.description
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_events)

        val nomEvents = DataAccess(this).getEventsByNomId(nomId)
        recyclerView.adapter = AdapterNomEvents(this, nomEvents.reversed() )
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        findViewById<Button>(R.id.camera).setOnClickListener { startCamera() }

        galleryImage = findViewById(R.id.gallery)

        galleryImage!!.setOnClickListener {
            val intent = Intent(this@ActivityNomsView, ActivityGallery::class.java)
            intent.putExtra("nom_id", model!!.itemId)
            startActivityForResult(intent, 665)
        }

        if( model!!.defaultImage >= 0 ) {
            val imagePath = DataAccess(this).getImagePathById(model!!.defaultImage)
            Glide.with(this).load(imagePath).into(galleryImage!!)
        }
    }

    private fun startCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
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
        if( requestCode == 1) {
            Log.e("Photo", "$mCurrentPhotoPath resultCode: $resultCode ${Activity.RESULT_OK} = OK")

            val dataAccess = DataAccess(this)
            val imageId = dataAccess.insertImage(mCurrentPhotoPath, model!!.itemId.toInt() )

            if( model?.defaultImage!! < 0 ) {
                dataAccess.setDefaultImage(model!!.itemId.toInt(), imageId)
                Glide.with(this).load(mCurrentPhotoPath).into(galleryImage!!)
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

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", path).apply { mCurrentPhotoPath = absolutePath }
    }
}
