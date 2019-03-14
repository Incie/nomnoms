package rolf.nomnoms.nomnoms

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import kotlinx.android.synthetic.main.activity_noms_events.*
import rolf.nomnoms.nomnoms.adapter.AdapterNomEventLog
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

class ActivityNomEvents : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_events)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                setFilter(newText)
                return true
            }
        })

        val events = DataAccess(this).getNomEvents()
        recyclerview_events.layoutManager = LinearLayoutManager(this)
        recyclerview_events.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerview_events.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview_events.adapter = AdapterNomEventLog(this, events)
    }

    private fun setFilter(newFilter : String ) {
        val adapter = recyclerview_events.adapter as AdapterNomEventLog
        adapter.setFilter(newFilter)
    }
}
