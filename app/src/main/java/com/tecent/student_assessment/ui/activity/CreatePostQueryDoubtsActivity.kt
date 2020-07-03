package com.tecent.student_assessment.ui.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONObject

import java.io.File
import java.util.HashMap

import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.tecent.student_assessment.R.color
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.UploadHelper
import com.tecent.student_assessment.utils.ExtraFunctions

class CreatePostQueryDoubtsActivity : AppCompatActivity() {
    lateinit var iv_cancel_post: ImageView
    lateinit var iv_profile_image: ImageView
    lateinit var iv_set_image: ImageView
    lateinit var tv_username: TextView
    lateinit var tv_btn_post: TextView
    lateinit var path_image: TextView
    lateinit var et_post_text: EditText
    private val READ_REQUEST_CODE = 42
    private lateinit var filename: String
    private var filefullpath = ""
    lateinit var fileName: String
    lateinit var postText: String
    internal lateinit var uploadHelper: UploadHelper
    lateinit var sharedPreferences: SharedPreferences
    lateinit var dialog: ACProgressFlower
    lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_create_post_query
        )
        iv_cancel_post = findViewById(
            id.iv_cancel_post
        )
        iv_profile_image = findViewById(
            id.iv_profile_image
        )
        tv_username = findViewById(
            id.tv_username
        )
        et_post_text = findViewById(
            id.et_post_text
        )
        tv_btn_post = findViewById(
            id.tv_btn_post
        )
        path_image = findViewById(id.path_image)
        iv_set_image = findViewById(
            id.iv_set_image
        )
        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = this.getSharedPreferences(
            ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE)
        val userdp = sharedPreferences.getString("userdp", "")
        requestQueue.add(
            ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl + "userdp/" + userdp, iv_profile_image))
        val name = sharedPreferences.getString("name", "")
        val userid = sharedPreferences.getString("userid", "")
        postText = ""
        fileName = ""
        tv_username.text = name
        //progress dialog
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Uploading....")
                .fadeColor(Color.BLACK).build()
        dialog.setCancelable(false)

        iv_cancel_post.setOnClickListener { finish() }
        //post button
        tv_btn_post.setOnClickListener {
            postText = et_post_text.text.toString().replace("'", "\\'")
            if (postText.trim().length < 5) {
                toast("please write at least 5 characters")
            } else if (!path_image.text.equals("")) {
                if (ExtraFunctions.isNetworkStatusAvailable(this)) {
                    uploadFileStatus()
                } else {
                    dialog.dismiss()
                    Toast.makeText(this@CreatePostQueryDoubtsActivity, "No internet connection!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (ExtraFunctions.isNetworkStatusAvailable(this)) {
                    dialog.show()
                    sendDataHomeToServer(
                            sharedPreferences.getString("userid", ""),
                            postText, fileName)
                } else {
                    Toast.makeText(this@CreatePostQueryDoubtsActivity, "No internet connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        et_post_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (et_post_text.text.toString().trim { it <= ' ' }.length >= 5) {
                    tv_btn_post.setBackgroundResource(
                        color.green
                    )
                } else if (et_post_text.text.toString().trim { it <= ' ' }.length < 5) {
                    tv_btn_post.setBackgroundResource(
                        color.smalldarkgrey
                    )
                }
                //                Toast.makeText(CreatePostQueryDoubts.this, String.valueOf(et_post_text.getText().toString().trim().length()), Toast.LENGTH_SHORT).show();
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    //image picker
    fun addAttachment(view: View) {
        val mimeTypes = arrayOf("image/*")

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""

            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }

            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        try {
            if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                // The document selected by the user won't be returned in the intent.
                // Instead, a URI to that document will be contained in the return intent
                // provided to this method as a parameter.
                // Pull that URI using resultData.getData().
                var uri: Uri?
                if (resultData != null) {
                    uri = resultData.data
                    val path = getPath(this, uri)
                    val file = File(path!!)
                    if (file.length() < 10 * 1024 * 1024) {
                        filefullpath = path
//                        setpostbuttonstatus()
                        fileName = filefullpath.substring(filefullpath.lastIndexOf("/") + 1)
                        path_image.text = fileName
                        if (!fileName.endsWith(".pdf") && !fileName.endsWith(".PDF")) {
//                            filefullpath = createthumbnailbig(path, filename)
                        }
                    } else {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "File too you can upload upto 1MB",
                                Snackbar.LENGTH_INDEFINITE)
                        snackbar.setAction("OK") { snackbar.dismiss() }
                        snackbar.show()
                    }

                }
            }

        } catch (e: Exception) {
            //Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            try {
                if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    // The document selected by the user won't be returned in the intent.
                    // Instead, a URI to that document will be contained in the return intent
                    // provided to this method as a parameter.
                    // Pull that URI using resultData.getData().
                    val file = File(resultData!!.data!!.toString())
                    if (file.length() < 10 * 1024 * 1024) {
                        val uri = Uri.fromFile(file)
                        val temp = getPath(this, uri)
                        if (temp!!.endsWith(".jpg") || temp.endsWith(".png") || temp.endsWith(".jpeg") || temp.endsWith(".JPG") || temp.endsWith(".PNG") || temp.endsWith(".JPEG")) {
                            //Toast.makeText(this, temp,Toast.LENGTH_LONG).show();
                            var path = "/storage/" + temp.substring(temp.lastIndexOf("/") + 1)
                            path = path.replace("%3A", "/")
                            path = path.replace("%2F", "/")
                            path = path.replace("%20", " ")
                            filefullpath = path
                            filename = filefullpath.substring(filefullpath.lastIndexOf("/") + 1)
                            path_image.setText(filename)
                            if (!filename.endsWith(".pdf") && !filename.endsWith(".PDF")) {
//                                filefullpath = createthumbnailbig(path, filename)
                            }
                        } else {
                            Toast.makeText(this, "Oops...unable to read file", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "File size too! you can upload file upto 1 MB",
                                Snackbar.LENGTH_INDEFINITE)
                        snackbar.setAction("OK") { snackbar.dismiss() }
                        snackbar.show()
                    }
                }
            } catch (f: Exception) {
                Toast.makeText(this, "Oops...unable to read file", Toast.LENGTH_LONG).show()
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri?): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri!!)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri!!.scheme!!, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun Context.toast(message: String) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    override fun onDestroy() {
        super.onDestroy()
        try {
            uploadHelper.cancel(true)
        } catch (e: Exception) {
        }
    }

    fun uploadFileStatus() {
        uploadHelper = @SuppressLint("StaticFieldLeak")
        object : UploadHelper(
            ExtraFunctions.serverurl + "uploadFileDoubts.php") {
            override fun onPostExecute(response: String?) {
                val emp = JSONObject(response)
                val result = emp.getString("result")
                if (result == "successful") {
                    dialog.dismiss()
                    fileName = emp.getString("filename")
                    sendDataHomeToServer(
                            sharedPreferences.getString("userid", ""),
                            postText, fileName)
                } else {
                    dialog.dismiss()
                    toast("An error has occurred. Please try again.")
                }

                super.onPostExecute(response)
            }

            override fun onPreExecute() {
                dialog.show()
                super.onPreExecute()
            }
        }
        if (filefullpath.startsWith("/storage/primary/"))
            filefullpath = filefullpath.replace("/storage/primary/", ExtraFunctions.ROOTMAIN)
//        toast(filefullpath)
        uploadHelper.execute(filefullpath)
    }

    fun sendDataHomeToServer(userid: String, postText: String, fileName: String) {
        val url = ExtraFunctions.serverurl + "uploadFileDoubtsData.php"
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response -> jsonParser(response) }, Response.ErrorListener {
            dialog.dismiss()
            toast("Error! Please try again later...")
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["userid"] = userid
                MyData["posttext"] = postText
                MyData["filename"] = fileName
                return MyData
            }
        }
        requestQueue.add(stringRequest)
    }

    fun jsonParser(jsontext: String) {
        try {
            val emp = JSONObject(jsontext)
            val result = emp.getString("result")
            if (result == "successful") {
                dialog.dismiss()
                toast("doubt uploaded successfully")
                val sharedPreferencesEditPosts = sharedPreferences.edit()
                sharedPreferencesEditPosts.putString("doubts", (Integer.parseInt(sharedPreferences.getString("doubts", "")!!) + 1).toString())
                sharedPreferencesEditPosts.putBoolean("newDoubtPost",true)
                sharedPreferencesEditPosts.apply()
                finish()
            } else if (result == "error") {
                dialog.dismiss()
                toast("An error has occured. Please try again")
            }
        } catch (exception: Exception) {
            dialog.dismiss()
            exception.printStackTrace()
        }

    }
}