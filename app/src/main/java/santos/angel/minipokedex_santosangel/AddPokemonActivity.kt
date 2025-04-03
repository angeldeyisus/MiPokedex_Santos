package santos.angel.minipokedex_santosangel

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.logging.ErrorManager

class AddPokemonActivity : AppCompatActivity() {

    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    val CLOUD_NAME = "dw8yxze4m"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-preset"
    var imageUri: Uri? = null

    val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Obtener la URI de la imagen seleccionada
                result.data?.data?.let { uri ->
                    imageUri = uri
                    changeImage(uri)
                }
            }
        }


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
            subirImagen()
        }

        guardar.setOnClickListener {
            guardarPokemon()
        }
    }

    fun guardarPokemonEnFirebase(nombre: String, numero: String, imagenUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("pokemones")

        val pokemonId = ref.push().key  // Genera una clave única
        val pokemon = hashMapOf(
            "id" to pokemonId,
            "nombre" to nombre,
            "numero" to numero,
            "imagenUrl" to imagenUrl
        )

        pokemonId?.let {
            ref.child(it).setValue(pokemon)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pokémon guardado correctamente", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun subirImagen() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryLauncher.launch(galleryIntent)
    }

    fun changeImage(uri: Uri) {
        val thumbnail: ImageView = findViewById(R.id.iv_pokemon)
        try {
            thumbnail.setImageURI(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initCloudnary() {
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }

    fun guardarPokemon() {
        val nombre = findViewById<EditText>(R.id.et_NombrePokemon).text.toString()
        val numero = findViewById<EditText>(R.id.et_NumPokemon).text.toString()

        if (imageUri != null) {
            MediaManager.get().upload(imageUri).unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        TODO("Not yet implemented")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as String
                        guardarPokemonEnFirebase(nombre, numero, url)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Toast.makeText(
                            this@AddPokemonActivity,
                            "Error al subir imagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        TODO("Not yet implemented")
                    }
                }).dispatch()
        } else {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para verificar y solicitar permisos
//    private fun checkAndRequestStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
//                    READ_EXTERNAL_STORAGE_REQUEST_CODE
//                )
//            }
//        } else {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    READ_EXTERNAL_STORAGE_REQUEST_CODE
//                )
//            }
//        }
//    }

    // Manejo de la respuesta del usuario
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede acceder a la imagen.", Toast.LENGTH_LONG).show()
            }
        }
    }
}


