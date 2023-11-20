package com.example.room.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.room.R
import com.example.room.database.Note
import com.example.room.database.NoteDao
import com.example.room.database.NoteRoomDatabase
import com.example.room.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        with(binding) {

            btnAdd.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddNote::class.java))
            }

            listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { adapterView, _, i, _ ->
                    val item = adapterView.adapter.getItem(i) as Note
                    delete(item)
                    true
                }
            listView.onItemClickListener = this@MainActivity
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedNote = binding.listView.adapter.getItem(position) as Note

        // Pass the selected note's information to the AddNote activity for editing
        val intent = Intent(this, AddNote::class.java)
        intent.putExtra("noteId", selectedNote.id) // Pass the ID of the selected note
        intent.putExtra("noteTitle", selectedNote.title)
        intent.putExtra("noteDesc", selectedNote.description)
        intent.putExtra("noteDate", selectedNote.date)
        startActivity(intent)
    }

    private fun getAllNotes() {
        mNotesDao.allNotes.observe(this) { notes ->
            val adapter = NoteAdapter(this, notes)
            binding.listView.adapter = adapter
        }
    }


    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }
}