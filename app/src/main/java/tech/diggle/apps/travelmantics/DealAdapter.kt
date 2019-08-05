package tech.diggle.apps.travelmantics

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item, parent, false
        )
        return DealViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        holder.bind(deals[position])
    }

    var deals: ArrayList<Deal> = arrayListOf()
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var databaseReference: DatabaseReference
    lateinit var childEventListener: ChildEventListener

    constructor(){
        databaseReference = firebaseDatabase.reference.child("deals")

        databaseReference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val td = dataSnapshot.getValue(Deal::class.java)!!
                Log.d("Deal: ", td.title)
                td.id = dataSnapshot.key
                deals.add(td)
                notifyItemInserted(deals.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }

        })

    }


    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var tvTitle: TextView
        internal var tvDescription: TextView
        internal var tvPrice: TextView
        internal var imageDeal: ImageView

        init {
            tvTitle = itemView.findViewById<View>(R.id.tvTitle) as TextView
            tvDescription = itemView.findViewById<View>(R.id.tvDescription) as TextView
            tvPrice = itemView.findViewById<View>(R.id.tvPrice) as TextView
            imageDeal = itemView.findViewById<View>(R.id.itemImage) as ImageView
            itemView.setOnClickListener(this)
        }

        fun bind(deal: Deal) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            tvPrice.text = deal.price
            showImage(deal.imageUrl)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            Log.d("Click", position.toString())
            val selectedDeal = deals[position]
            val intent = Intent(view.context, AdminActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            view.context.startActivity(intent)
        }

        private fun showImage(url: String?) {
            if (url != null && !url.isEmpty()) {
                Picasso.get()
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(imageDeal)
            }
        }
    }

}