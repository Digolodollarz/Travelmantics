package tech.diggle.apps.travelmantics

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import android.util.StatsLog.logEvent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import java.util.*
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_admin.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var analytics: FirebaseAnalytics
    val RC_SIGN_IN = 323

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        analytics = FirebaseAnalytics.getInstance(this)
        analytics.logEvent("start_app", null)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    AuthUI.IdpConfig.EmailBuilder().build(),
                                    AuthUI.IdpConfig.GoogleBuilder().build()
                            ))
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN)
        }

        btnAdmin.setOnClickListener { startActivity(Intent(this, AdminActivity::class.java)) }
        btnUser.setOnClickListener { startActivity(Intent(this, UserActivity::class.java)) }
        btnLogout.setOnClickListener {
            auth.signOut()
            recreate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode != Activity.RESULT_OK) {
                when {
                    response == null -> Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show()
                    response.error!!.errorCode == ErrorCodes.NO_NETWORK -> Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show()
                    else -> {
                        Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
                        Log.e("Main Activity", "Sign-in error: ", response.error)
                    }
                }
            }

            recreate()
        }
    }
}
