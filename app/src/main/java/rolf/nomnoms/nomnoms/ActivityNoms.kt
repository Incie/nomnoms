package rolf.nomnoms.nomnoms

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import rolf.nomnoms.nomnoms.adapter.AdapterNoms
import rolf.nomnoms.nomnoms.adapter.AdapterNomsEvent
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.NomSort

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

    private fun startNewNomActivity(){
        startActivityForResult(Intent(this, ActivityNomsAdd::class.java), 666)
    }

    private fun onNomEvent(event: AdapterNomsEvent, itemId: Long, sharedView: Array<Pair<View,String>>?) {
        when( event ) {
            AdapterNomsEvent.NEW_EVENT_NOMS ->
                startNewNomActivity()

            AdapterNomsEvent.DELETE_NOMS -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete?")
                builder.setPositiveButton("YES") { _, _ ->

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

                val shared = sharedView!! as Array<Pair<View,String>>
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *shared)
                startActivityForResult(intent, 666, options.toBundle())
            }
            else -> {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if( item!!.itemId == R.id.menu_nom_events ){
            val intent = Intent(this@ActivityNoms, ActivityNomEvents::class.java)
            startActivity(intent)
            return true
        }

        if( item.itemId == R.id.menu_nom_new ){
            startNewNomActivity()
            return true
        }

        if( item.itemId == R.id.menu_nom_sort_alphabetical ){
            (recyclerView?.adapter as AdapterNoms).sort( NomSort.Alphabetical )
            return true
        }

        if( item.itemId == R.id.menu_nom_sort_ascending ){
            (recyclerView?.adapter as AdapterNoms).sort( NomSort.Ascending )
            return true
        }

        if( item.itemId == R.id.menu_nom_sort_descending ){
            (recyclerView?.adapter as AdapterNoms).sort( NomSort.Descending )
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == 666 )
            refreshAdapter()

        super.onActivityResult(requestCode, resultCode, data)
    }
}
