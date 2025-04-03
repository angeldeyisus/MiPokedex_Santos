package santos.angel.minipokedex_santosangel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PokemonAdapter: BaseAdapter {
    var pokemones = ArrayList<Pokemon>()
    var context: Context

    constructor(context: Context, pokemones: List<Pokemon>) {
        this.context = context
        this.pokemones = pokemones as ArrayList<Pokemon>
    }

    override fun getCount(): Int {
        return pokemones.size
    }

    override fun getItem(position: Int): Any {
        return pokemones[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)
        val nombre = view.findViewById<TextView>(R.id.tvNombre)
        val numero = view.findViewById<TextView>(R.id.tvNumero)
        val imagen = view.findViewById<ImageView>(R.id.ivPokemon)

        val pokemon = pokemones[position]
        nombre.text = pokemon.nombre
        numero.text = "#${pokemon.numero}"
        Glide.with(context).load(pokemon.imagenUrl).into(imagen)
        return view
    }
}