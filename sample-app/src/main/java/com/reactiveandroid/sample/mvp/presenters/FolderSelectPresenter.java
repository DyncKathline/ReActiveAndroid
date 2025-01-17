package com.reactiveandroid.sample.mvp.presenters;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.mvp.models.Folder;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.models.NoteFolderRelation;
import com.reactiveandroid.sample.mvp.views.AddToFoldersView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FolderSelectPresenter {

    private AddToFoldersView view;
    private Long noteId;
    private Note note;
    private List<Folder> allFolders = new ArrayList<>();
    private List<Folder> noteFolders = new ArrayList<>();

    public FolderSelectPresenter(AddToFoldersView view, Long noteId) {
        this.view = view;
        this.noteId = noteId;
        onFirstViewAttach();
    }

    protected void onFirstViewAttach() {
        loadNote();
    }


    public void onFolderSelected(int position, boolean isChecked) {
        Folder selectedFolder = allFolders.get(position);
        if (isChecked) {
            NoteFolderRelation noteFolderRelation = new NoteFolderRelation(note, selectedFolder);
            noteFolders.add(selectedFolder);
            noteFolderRelation.saveAsync()
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        } else {
            noteFolders.remove(selectedFolder);
            Delete.from(NoteFolderRelation.class)
                    .where("note=? AND folder=?", noteId, selectedFolder.getId())
                    .executeAsync()
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        updateFoldersList();
    }

    private void loadNote() {
        Select.from(Note.class)
                .where("id=?", noteId)
                .fetchSingleAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(note -> {
                    this.note = note;
                    loadNoteFolders();
                });
    }

    private void loadNoteFolders() {
        Select.from(Folder.class)
                .fetchAsync()
                .map(folders -> this.allFolders = folders)
                .flatMapObservable(Observable::fromIterable)
                .filter(this::isNoteInFolder)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(folders -> {
                    this.noteFolders = folders;
                    showFoldersList();
                });
    }

    private boolean isNoteInFolder(Folder folder) {
        return Select.from(NoteFolderRelation.class)
                .where("note=? AND folder=?", noteId, folder.getId())
                .count() > 0;
    }

    private void showFoldersList() {
        view.showFoldersList(allFolders, noteFolders);
    }

    private void updateFoldersList() {
        view.updateFoldersList(noteFolders);
    }

}
