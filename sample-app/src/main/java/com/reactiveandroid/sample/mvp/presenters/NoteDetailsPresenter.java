package com.reactiveandroid.sample.mvp.presenters;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.models.NoteFolderRelation;
import com.reactiveandroid.sample.mvp.views.NoteDetailsView;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NoteDetailsPresenter {

    public static final long NEW_NOTE_ID = -1L;
    private NoteDetailsView view;
    private Long noteId;
    private Note note;

    public NoteDetailsPresenter(NoteDetailsView view, Long noteId) {
        this.view = view;
        this.noteId = noteId;
        onFirstViewAttach();
    }

    protected void onFirstViewAttach() {
        if (noteId != NEW_NOTE_ID) {
            loadNote();
        }
    }

    public void onSaveNoteClicked(String title, String text) {
        if (note == null) {
            note = new Note(title, text, ColorGenerator.MATERIAL.getRandomColor());
        }

        note.saveAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(noteId -> view.showNoteSavedMessage());
    }

    public void onDeleteNoteClicked() {
        if (note == null) {
            view.closeScreen();
            return;
        }

        note.deleteAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.showNoteDeletedMessage();
                    view.closeScreen();
                });
    }

    public void onShowNoteInfoClicked() {
        view.showNoteInfo(note);
    }

    public void onOpenNoteFoldersScreenClicked() {
        if (note != null) {
            view.openNoteFoldersScreen(note.getId());
        } else {
            view.showFirstSaveNoteMessage();
        }
    }

    private void loadNote() {
        Single<Note> noteSingle = Select.from(Note.class)
                .where("id = ?", noteId)
                .fetchSingleAsync();

        Select.from(NoteFolderRelation.class)
                .where("note = ?", noteId)
                .fetchAsync()
                .flatMapObservable(Observable::fromIterable)
                .map(NoteFolderRelation::getFolder)
                .toList()
                .zipWith(noteSingle, (folders, note) -> {
                    note.setFolders(folders);
                    return note;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNotesLoaded);
    }

    private void onNotesLoaded(Note note) {
        this.note = note;
        view.showNoteDetails(note);
    }

}
