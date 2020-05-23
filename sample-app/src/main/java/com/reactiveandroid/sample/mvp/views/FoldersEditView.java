package com.reactiveandroid.sample.mvp.views;

import com.reactiveandroid.sample.mvp.models.Folder;

import java.util.List;

public interface FoldersEditView {

    void updateFoldersList(List<Folder> folders);
    void closeScreen();

}
