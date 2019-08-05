package tech.diggle.apps.travelmantics

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.list_item.*

class AdminActivity : AppCompatActivity() {
    val PICTURE_RESULT = 255
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    lateinit var deal: Deal


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("deals")

        this.deal = intent.getSerializableExtra("Deal") as Deal? ?: Deal()

        etPrice.setText(deal.price)
        etTitle.setText(deal.title)
        etDescription.setText(deal.description)
        showImage(deal.imageUrl)

        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent,
                    "Insert Picture"), PICTURE_RESULT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_item_save -> {
                saveDeal()
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show()
                backToList()
                return true
            }

            R.id.menu_item_delete -> {
                deleteDeal()
                backToList()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri = data!!.data!!

            val storageReference = FirebaseStorage.getInstance().reference.child("deals").child(imageUri.lastPathSegment!!)
            Log.d("Path: ", storageReference.path)
            storageReference.putFile(imageUri).addOnSuccessListener { _ ->
                storageReference.downloadUrl.addOnCompleteListener {
                    Log.d("Url: ", it.result.toString())
                    deal.imageUrl = it.result.toString()
                    showImage(deal.imageUrl)
                }
            }

        }
    }

    private fun showImage(url: String?) {
        if (url != null && !url.isEmpty()) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Picasso.get()
                    .load(url)
                    .resize(width, width).centerCrop()
                    .into(imageView)
        }
    }

    private fun saveDeal() {
        deal.title = etTitle.text.toString()
        deal.description = etDescription.text.toString()
        deal.price = etPrice.text.toString()
        if (deal.id == null) {
            databaseReference.push().setValue(deal)
        } else {
            databaseReference.child(deal.id!!).setValue(deal)
        }
    }


    private fun deleteDeal() {
        if (deal.id == null) {
            Toast.makeText(this, "You can only delete saved deals", Toast.LENGTH_SHORT).show()
            return
        }
        databaseReference.child(deal.id!!).removeValue()
        if (deal.imageUrl != null && !deal.imageUrl!!.isEmpty()) {
            val picRef = FirebaseStorage.getInstance().reference.child(deal.imageUrl!!)
            picRef.delete().addOnSuccessListener { Log.d("Delete Image", "Image Successfully Deleted") }.addOnFailureListener { e -> Log.d("Delete Image", e.message) }
        }
        Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
    }

    private fun backToList() {
        onBackPressed()
    }

}
