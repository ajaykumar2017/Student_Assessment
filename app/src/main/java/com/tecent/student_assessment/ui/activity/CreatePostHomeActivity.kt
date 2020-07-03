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
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.design.chip.Chip
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.ui.fragments.HomeFragment
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.UploadHelper
import com.tecent.student_assessment.utils.ExtraFunctions
import kotlinx.android.synthetic.main.activity_create_post_home.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONObject
import java.io.File
import java.util.HashMap

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CreatePostHomeActivity : AppCompatActivity() {

    lateinit var selectedSubject:String
    lateinit var userid:String
    lateinit var postText:String
    private val READ_REQUEST_CODE = 42
    private lateinit var filename: String
    private var filefullpath = ""
    lateinit var fileName: String
    internal lateinit var uploadHelper: UploadHelper
    lateinit var sharedPreferences:SharedPreferences
    lateinit var dialog:ACProgressFlower
    internal lateinit var requestQueue: RequestQueue
    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_create_post_home
        )
        setSupportActionBar(findViewById(
            id.toolbar_main
        ))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(
            drawable.ic_001_back
        )
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.title = "Create Post"
        toolbar_main.setNavigationOnClickListener {
            finish()
        }
        postText=""
        selectedSubject=""
        fileName=""
        sharedPreferences = this.getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        requestQueue = Volley.newRequestQueue(this)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Uploading....")
                .fadeColor(Color.BLACK).build()
        chipGroup.setOnCheckedChangeListener{group,checkedId:Int ->
            // Get the checked chip instance from chip group
            val chip:Chip? = findViewById(checkedId)

            chip?.let {
                selectedSubject="${it.text}"
            }
        }

        btn_post.setOnClickListener {
            postText = et_text_post.text.toString().replace("'", "\\'")
            if (postText.trim().length<5){
                toast("please write at least 5 characters")
            }else if (selectedSubject==""){
                toast("Please select a subject")
            } else if(!image_path.text.equals("")) {
//                Toast.makeText(this, postText + " " + selectedSubject, Toast.LENGTH_SHORT).show()
                uploadFileStatus()
            }
            else{
                dialog.show()
                sendDataHomeToServer(
                        sharedPreferences.getString("userid", ""),
                        postText, selectedSubject, fileName)
            }
        }

    }

    fun addAttachment(view: View) {
        val mimeTypes = arrayOf("image/*", "application/pdf")

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
                        filename = filefullpath.substring(filefullpath.lastIndexOf("/") + 1)
                        image_path.setText(filename)
                        if (!filename.endsWith(".pdf") && !filename.endsWith(".PDF")) {
//                            filefullpath = createthumbnailbig(path, filename)
                        }
                    } else {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "File too large." + " Maximum size to upload is 1 MB",
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
                        if (temp!!.endsWith(".pdf") || temp.endsWith(".jpg") || temp.endsWith(".png") || temp.endsWith(".jpeg") ||
                                temp.endsWith(".PDF") || temp.endsWith(".JPG") || temp.endsWith(".PNG") || temp.endsWith(".JPEG")) {
                            //Toast.makeText(this, temp,Toast.LENGTH_LONG).show();
                            var path = "/storage/" + temp.substring(temp.lastIndexOf("/") + 1)
                            path = path.replace("%3A", "/")
                            path = path.replace("%2F", "/")
                            path = path.replace("%20", " ")
                            filefullpath = path
                            filename = filefullpath.substring(filefullpath.lastIndexOf("/") + 1)
                            image_path.setText(filename)
                            if (!filename.endsWith(".pdf") && !filename.endsWith(".PDF")) {
//                                filefullpath = createthumbnailbig(path, filename)
                            }
                        } else {
                            Toast.makeText(this, "Oops...unable to read file", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "File size too large. If you want to upload large study material, upload it through" + " Contribute menu in Dashboard.",
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

    fun Context.toast(message:String)=
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()


    override fun onDestroy() {
        super.onDestroy()
        try {
            uploadHelper.cancel(true)
        }catch (e:Exception){}
    }
    fun uploadFileStatus(){
        uploadHelper= @SuppressLint("StaticFieldLeak")
        object : UploadHelper(
            ExtraFunctions.serverurl + "uploadFileHome.php")
        {
            override fun onPostExecute(response: String?) {
                val emp = JSONObject(response)
                val result = emp.getString("result")
                if (result == "successful"){
                    dialog.dismiss()
                    fileName = emp.getString("filename")
                    sendDataHomeToServer(
                            sharedPreferences.getString("userid", ""),
                            postText, selectedSubject, fileName)
                }else{
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
            filefullpath=filefullpath.replace("/storage/primary/",
                ExtraFunctions.ROOTMAIN)
        Log.d("filepath",filefullpath)
//        toast(filefullpath)
        uploadHelper.execute(filefullpath)
    }
    fun sendDataHomeToServer(userid:String, postText:String, selectedSubject:String, fileName:String){
        val url = ExtraFunctions.serverurl + "uploadFileHomeData.php"
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response -> jsonParser(response) }, Response.ErrorListener {
            dialog.dismiss()
            toast("Error! Please try again later...")
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["userid"] = userid
                MyData["posttext"] = postText
                MyData["subject"] = selectedSubject
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
                toast("post uploaded successfully")
                val sharedPreferencesEditPosts = sharedPreferences.edit()
                sharedPreferencesEditPosts.putString("posts",(Integer.parseInt(sharedPreferences.getString("posts", "")!!) + 1).toString())
                sharedPreferencesEditPosts.putBoolean("newPost",true)
                sharedPreferencesEditPosts.apply()
                finish()
                homeFragment.volleyPostDataRequest()
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
