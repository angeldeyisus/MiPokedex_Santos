package santos.angel.minipokedex_santosangel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cargarPokemones()
        val btnRegistrar: Button = findViewById(R.id.btnAgregarPokemon)
        btnRegistrar.setOnClickListener{
            val intent: Intent = Intent(this, AddPokemonActivity::class.java)
            startActivity(intent)
        }
    }

    fun cargarPokemones() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("pokemones")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaPokemones = mutableListOf<Pokemon>()
                for (pokemonSnap in snapshot.children) {
                    val pokemon = pokemonSnap.getValue(Pokemon::class.java)
                    pokemon?.let { listaPokemones.add(it) }
                }
                actualizarListView(listaPokemones)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun actualizarListView(lista: List<Pokemon>) {
        val adapter = PokemonAdapter(this, lista)
        val listView = findViewById<ListView>(R.id.listPokemon)
        listView.adapter = adapter
    }

}

