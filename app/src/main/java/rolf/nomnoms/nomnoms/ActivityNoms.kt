package rolf.nomnoms.nomnoms


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_noms.*
import rolf.nomnoms.nomnoms.adapter.AdapterNoms
import rolf.nomnoms.nomnoms.adapter.AdapterNomsEvent
import rolf.nomnoms.nomnoms.dataaccess.DataAccess
import rolf.nomnoms.nomnoms.model.NomSort

class ActivityNoms : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms)

        list_noms.layoutManager = LinearLayoutManager(this)
        list_noms.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        list_noms.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        refreshAdapter()
    }

    private fun refreshAdapter(){
        val allNoms = DataAccess(this).getAll()
        val adapter = AdapterNoms(this, allNoms, this::onNomEvent)
        list_noms!!.adapter = adapter
    }

    private fun startNewNomActivity(){
        startActivityForResult(Intent(this, ActivityNomsAdd::class.java), 666)
    }

    private fun onNomEvent(event: AdapterNomsEvent, itemId: Long, sharedView: Array<Pair<View,String>>?) {
        when( event ) {
            AdapterNomsEvent.NEW_EVENT_NOMS ->
                startNewNomActivity()

            AdapterNomsEvent.DELETE_NOMS -> {
                val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)

                val nom = DataAccess(this).getNomById(itemId)!!

                builder.setTitle("Delete '${nom.name}'?")
                builder.setNeutralButton("DELETE") { _, _ ->

                    DataAccess(this).deleteNomById(itemId)
                    refreshAdapter()
                    //todo: get id from somewhere
                    //update recyclerview
                }

                builder.show()
            }

            AdapterNomsEvent.VIEW_NOMS -> {
                val intent = Intent(this@ActivityNoms, ActivityNomsView::class.java)
                intent.putExtra("nom_id", itemId )

                val shared = sharedView!!
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *shared)
                startActivityForResult(intent, 666, options.toBundle())
            }

            AdapterNomsEvent.OPEN_GALLERY -> {
                val intent = Intent(this@ActivityNoms, ActivityGallery::class.java)
                intent.putExtra("nom_id", itemId)

                val shared = sharedView!!
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *shared)
                startActivityForResult(intent, 665, options.toBundle())
            }
            else -> {
                throw Exception("Unknown eventId in onNomEvent $itemId")
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
            (list_noms.adapter as AdapterNoms).sort( NomSort.Alphabetical )
            return true
        }

        if( item.itemId == R.id.menu_nom_sort_ascending ){
            (list_noms.adapter as AdapterNoms).sort( NomSort.Ascending )
            return true
        }

        if( item.itemId == R.id.menu_nom_sort_descending ){
            (list_noms.adapter as AdapterNoms).sort( NomSort.Descending )
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
