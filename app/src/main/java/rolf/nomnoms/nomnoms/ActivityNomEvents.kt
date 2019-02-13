package rolf.nomnoms.nomnoms

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import rolf.nomnoms.nomnoms.adapter.AdapterNomEventLog
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

const val TAG : String = "NomEvents"

class ActivityNomEvents : AppCompatActivity() {

    lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_events)

        val events = DataAccess(this).getNomEvents()

        val sv = findViewById<SearchView>(R.id.searchView)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i(TAG,"Press querysubmit")
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                Log.i(TAG,"Press querytextchange")
                SetFilter(newText)
                return true
            }
        })

        recyclerview = findViewById(R.id.recyclerview_events)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.adapter = AdapterNomEventLog(this, events)
    }

    private fun SetFilter( newFilter : String ) {
        val adapter = recyclerview.adapter as AdapterNomEventLog
        adapter.setFilter(newFilter)
    }
}
