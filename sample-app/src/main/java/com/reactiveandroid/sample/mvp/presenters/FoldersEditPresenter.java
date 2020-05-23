package com.reactiveandroid.sample.mvp.presenters;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.mvp.models.Folder;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.views.FoldersEditView;
import com.reactiveandroid.sample.ui.activities.FoldersEditActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FoldersEditPresenter {

    private FoldersEditView view;
    private List<Folder> folders = new ArrayList<>();

    public FoldersEditPresenter(FoldersEditView view) {
        this.view = view;
        onFirstViewAttach();
    }

    protected void onFirstViewAttach() {
        loadFolders();
    }

    public void onFolderCreate(String folderName) {
        if (folders == null) return;

        Folder newFolder = new Folder(folderName);
        folders.add(newFolder);
        view.updateFoldersList(folders);
        newFolder.saveAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void onFolderUpdate(String folderName, int position) {
        if (folders == null) return;

        Folder updatedFolder = folders.get(position);
        updatedFolder.setName(folderName);
        view.updateFoldersList(folders);
        updatedFolder.saveAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void onFolderDelete(int position) {
        if (folders == null) return;

        Folder deletedFolder = folders.remove(position);
        view.updateFoldersList(folders);
        deletedFolder.deleteAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void loadFolders() {
        Select.from(Folder.class)
                .fetchAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(folders -> {
                    this.folders = folders;
                    view.updateFoldersList(folders);
                });
    }

    public void onDeleteAllFoldersClicked() {
        folders.clear();
        view.closeScreen();
        Delete.from(Note.class).executeAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
