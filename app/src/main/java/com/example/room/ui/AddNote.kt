package com.example.room.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.room.database.Note
import com.example.room.database.NoteDao
import com.example.room.database.NoteRoomDatabase
import com.example.room.databinding.ActivityAddNoteBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddNote : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var noteId: Long = -1 // Declare noteId as a member variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!

        // Retrieve noteId from the intent
        noteId = intent.getLongExtra("noteId", -1)

        if (noteId != -1L) {
            // If noteId is not -1, it means we are editing an existing note
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteDesc = intent.getStringExtra("noteDesc")
            val noteDate = intent.getStringExtra("noteDate")

            // Pre-fill the fields with existing note information
            binding.txtTitle.setText(noteTitle)
            binding.txtDesc.setText(noteDesc)
            binding.txtDate.setText(noteDate)

            binding.btnSave.setOnClickListener {
                updateNote(
                    Note(
                        id = noteId,
                        title = binding.txtTitle.text.toString(),
                        description = binding.txtDesc.text.toString(),
                        date = binding.txtDate.text.toString()
                    )
                )
                finish()
            }
        } else {
            // If noteId is -1, it means we are adding a new note
            binding.btnSave.setOnClickListener {
                insertNote(
                    Note(
                        title = binding.txtTitle.text.toString(),
                        description = binding.txtDesc.text.toString(),
                        date = binding.txtDate.text.toString()
                    )
                )
                finish()
            }
        }
    }

    private fun updateNote(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }
    private fun insertNote(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }
}