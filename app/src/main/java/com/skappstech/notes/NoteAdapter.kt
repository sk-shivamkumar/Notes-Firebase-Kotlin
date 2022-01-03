package com.skappstech.notes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private var datalist : ArrayList<Data>, val listener:MyOnClickListener): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_design, parent,false)
        return NoteViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val currentitem = datalist[position]

        holder.title.text = currentitem.title
        holder.note.text = currentitem.note

    }

    override fun getItemCount(): Int {
        return datalist.size
    }




    inner class NoteViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){

        val title : TextView = itemView.findViewById(R.id.rv_tv_title)
        val note : TextView = itemView.findViewById(R.id.rv_tv_note)

        init {

            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onClick(position)
            }

        }
    }

    interface MyOnClickListener{
        fun onClick(position: Int)
    }
}
