package com.example.imagefromgallery

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    val REQUEST_GET_SINGLE_FILE: Int = 1;

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK);
            intent.type = "image/*";
            startActivityForResult(intent, REQUEST_GET_SINGLE_FILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.setupPermissions()
        try {
            if(resultCode == RESULT_OK && requestCode == REQUEST_GET_SINGLE_FILE) {
                val selectedImageUri = data?.data
                val uriPathHelper = URIPathHelper()
                if (selectedImageUri != null) {
                    val path = uriPathHelper.getPath(requireActivity().applicationContext, selectedImageUri)
                    if (path != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                        this.saveImage(bitmap)
                    }
                }
            }
        }
        catch(e: Exception) {
            Log.e("File Selector Activity", e.toString());
            Toast.makeText(requireActivity(), e.toString() , Toast.LENGTH_LONG).show()
        }
    }

    private fun saveImage(finalBitmap: Bitmap) {
        val root: String = requireActivity().getExternalFilesDir(null).toString()
        val myDir = File("$root/saved_images")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-example.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}