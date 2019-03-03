package rolf.nomnoms.nomnoms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import rolf.nomnoms.nomnoms.adapter.AdapterGallery
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.NomImageModel

class ActivityGallery : AppCompatActivity() {

    private val _invalidId: Long = -1
    private val galleryView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nomsgallery)

        val nomId = intent.getLongExtra("nom_id", _invalidId).toInt()

        if( nomId == _invalidId.toInt() ){
            finish()
            Toast.makeText(this, "nomId == _invalidId $_invalidId", Toast.LENGTH_SHORT).show()
            return
        }

        val dataAccess = DataAccess(this)
        val nomModel = dataAccess.getNomById(nomId.toLong())!!

        title = nomModel.name

        val imageList = dataAccess.getImagesForNomById(nomId)

        val galleryView = findViewById<ImageView>(R.id.image_test)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_images)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = AdapterGallery(this, imageList, nomModel.defaultImage, this::onImageSelect)

        val image = imageList.find{ it.imageId == nomModel.defaultImage }
        if( image != null ) {
            Glide.with(this).load(image.imagePath).into(galleryView)
        }
    }

    private fun onImageSelect(image: NomImageModel){
        Glide.with(this).load(image.imagePath).placeholder(R.drawable.ai_launcher).into(galleryView!!)
    }
}
