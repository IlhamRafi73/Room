package com.example.room.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.room.R
import com.example.room.database.Note

class NoteAdapter(context: Context, private val notes: List<Note>) :
    ArrayAdapter<Note>(context, 0, notes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context)
                .inflate(R.layout.item_container, parent, false)
        }

        val currentNote = getItem(position)

        listItemView?.findViewById<TextView>(R.id.txtItemName)?.text = currentNote?.title
        listItemView?.findViewById<TextView>(R.id.txtItemDesc)?.text = currentNote?.description

        return listItemView ?: View(context)
    }
}

