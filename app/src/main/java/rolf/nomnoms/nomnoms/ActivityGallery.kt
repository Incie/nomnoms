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

class ActivityGallery : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nomsgallery)

        val nomId = intent.getLongExtra("nom_id", -1).toInt()

        if( nomId == -1 ){
            finish()
            Toast.makeText(this, "nomId == -1", Toast.LENGTH_SHORT).show()
            return
        }

        val da = DataAccess(this)
        val model = da.getNomById(nomId.toLong())

        val imageList = da.getImagesForNomById(nomId)

        val galleryView = findViewById<ImageView>(R.id.image_present)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_images)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = AdapterGallery(this, imageList){
            Glide.with(this).load(it.imagePath).into(galleryView)
        }
    }
}
