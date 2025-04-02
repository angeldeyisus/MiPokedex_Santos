package santos.angel.minipokedex_santosangel

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.logging.ErrorManager

class AddPokemonActivity : AppCompatActivity() {

    val CLOUD_NAME = "dw8yxze4m"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-preset"
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val nombre: EditText = findViewById(R.id.et_NombrePokemon)
        val numero: EditText = findViewById(R.id.et_NumPokemon)
        val subir: Button = findViewById(R.id.btnSubirImagen)
        val guardar: Button = findViewById(R.id.btnGuardarPokemon)



        initCloudnary()

        subir.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
        guardar.setOnClickListener{
            guardarPokemon()
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun startActivityForResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data

            if (fullPhotoUri != null)
                changeImage(fullPhotoUri)
        }
    }
    fun changeImage(uri: Uri){
        val thumbnail: ImageView = findViewById(R.id.iv_pokemon)
        try {
            thumbnail.setImageURI(uri)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun initCloudnary(){
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }

    fun guardarPokemon(): String{
        var url: String = ""
        if(imageUri != null){
            MediaManager.get().upload(imageUri).unsigned(UPLOAD_PRESET).callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("Cloudinary Quickstart", "Upload start")
                }
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long){
                    Log.d("Cloudinary Quickstart", "Upload start")
                }
                override fun onSuccess(requestId: String, resultData: Map<*, *>){
                    Log.d("Cloudinary Quickstart", "Upload success")
                    url = resultData["secure_url"] as String? ?: ""
                    Log.d("URL}", url)
                }
                override fun onError(requestId: String, error: ErrorManager){
                    Log.d("Cloudinary Quickstart", "Upload failed")
                }
                override fun onReschedule(requestId: String, error: ErrorManager){
                }
            }).dispatch()
        }
        return url
    }

}