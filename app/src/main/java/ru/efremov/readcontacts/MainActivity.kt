package ru.efremov.readcontacts

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.core.app.ActivityCompat
import java.security.Permission
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            requestContacts()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                requestContacts()
            } else {
                Log.d("MainActivity", "Permission Denied")
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestContacts() {
        thread {
            val cursor = contentResolver.query(
                Contacts.CONTENT_URI,
                null,
                null,
                null
            )

            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Contacts._ID)
                )
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME)
                )
                if (name != null) {
                    val contact = Contact(
                        id,
                        name
                    )
                    Log.d("MainActivity", contact.toString())
                }
            }

            cursor?.close()
        }
    }

    companion object {

        const val READ_CONTACTS_RC = 100
    }

}