package com.reactiveandroid.sample.mvp.views;

import com.reactiveandroid.sample.mvp.models.Folder;

import java.util.List;

public interface AddToFoldersView {

    void showFoldersList(List<Folder> allFolders, List<Folder> noteFolders);
    void updateFoldersList(List<Folder> noteFolders);

}
