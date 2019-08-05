package tech.diggle.apps.travelmantics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }

    override fun onResume() {
        super.onResume()
        val adapter = DealAdapter()
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvDeals.adapter = adapter
        rvDeals.layoutManager = layoutManager
    }

}
