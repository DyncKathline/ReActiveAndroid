package com.reactiveandroid.sample.mvp.views;

import com.reactiveandroid.sample.mvp.models.Note;

import java.util.List;

public interface NotesListView {

    void updateNotesList(List<Note> notes);
    void showNotesList();
    void hideNotesList();
    void showNotesNotFoundMessage();
    void hideNotesNotFoundMessage();
    void openNoteDetailsScreen(long noteId);
    void openFoldersEditScreen();

}
