package rolf.nomnoms.nomnoms

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import rolf.nomnoms.nomnoms.adapter.AdapterNomEventLog
import rolf.nomnoms.nomnoms.dataaccess.DataAccess

class ActivityNomEvents : AppCompatActivity() {
    lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noms_events)

        val events = DataAccess(this).getNomEvents()

        recyclerview = findViewById(R.id.recyclerview_events)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.adapter = AdapterNomEventLog(this, events)
    }
}
