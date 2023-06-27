package com.cibertec.yaranime.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.yaranime.R
import com.cibertec.yaranime.models.Event
import com.google.android.material.button.MaterialButton


class EventAdapter(private val items: List<Event>) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tituloEvento: TextView
        var direccionEvento: TextView
        var botonEvento: MaterialButton
        var fechaEvento: TextView
        var ciudadEvento: TextView
        init {
            with(itemView){
                tituloEvento = findViewById(R.id.titulo_evento)
                direccionEvento = findViewById(R.id.direccion_evento)
                botonEvento = findViewById(R.id.boton_evento)
                fechaEvento = findViewById(R.id.fecha_evento)
                ciudadEvento = findViewById(R.id.ciudad_evento)


            }
        }

        fun bindData(data: Event) {
            tituloEvento.text = data.title
            direccionEvento.text = data.address
            fechaEvento.text = data.date
            ciudadEvento.text = data.city

            botonEvento.setOnClickListener {
                val urlIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(data.link)
                )
                startActivity(botonEvento.context, urlIntent, null)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindData(item)
    }

}