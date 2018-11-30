package rolf.nomnoms.nomnoms

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import rolf.nomnoms.nomnoms.adapter.AdapterNoms
import rolf.nomnoms.nomnoms.adapter.AdapterNomsEvent
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

class ActivityNoms : AppCompatActivity() {

    private var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms)

        recyclerView = findViewById(R.id.list_noms)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        refreshAdapter()
    }

    private fun refreshAdapter(){
        val allNoms = DataAccess(this).getAll()
        val adapter = AdapterNoms(this, allNoms, this::onNomEvent)
        recyclerView!!.adapter = adapter
    }

    private fun onNomEvent(event: AdapterNomsEvent, itemId: Long) {
        when( event ) {
            AdapterNomsEvent.NEW_EVENT_NOMS ->
                startActivityForResult(Intent(this, ActivityNomsAdd::class.java), 666)

            AdapterNomsEvent.DELETE_NOMS -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete?")
                builder.setPositiveButton("YES") { dialog, which ->

                    DataAccess(this).deleteNomById(itemId)
                    refreshAdapter()
                    //get id from somewhere
                    //update recyclerview
                }

                builder.show()
            }

            AdapterNomsEvent.VIEW_NOMS -> {
                val intent = Intent(this@ActivityNoms, ActivityNomsView::class.java)
                intent.putExtra("nom_id", itemId )
                startActivity(intent)
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == 666 )
            refreshAdapter()

        super.onActivityResult(requestCode, resultCode, data)
    }
}
