package com.reactiveandroid.sample.mvp.views;

import com.reactiveandroid.sample.mvp.models.Note;

public interface NoteDetailsView {

    void showNoteDetails(Note note);
    void showFirstSaveNoteMessage();
    void showNoteSavedMessage(Long noteId);
    void showNoteDeletedMessage();
    void showNoteInfo(Note note);
    void openNoteFoldersScreen(long noteId);
    void closeScreen();

}
