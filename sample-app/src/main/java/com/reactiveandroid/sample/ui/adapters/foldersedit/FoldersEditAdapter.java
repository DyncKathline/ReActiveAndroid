package com.reactiveandroid.sample.ui.adapters.foldersedit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Folder;

import java.util.ArrayList;
import java.util.List;

public class FoldersEditAdapter extends RecyclerView.Adapter<NewFolderViewHolder> {

    private OnFolderChangedListener onFolderChangedListener;
    private OpenCloseable lastOpenedItem;
    private LayoutInflater layoutInflater;
    private List<Folder> folders = new ArrayList<>();

    public FoldersEditAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnFolderChangedListener(OnFolderChangedListener listener) {
        this.onFolderChangedListener = listener;
    }

    @NonNull
    @Override
    public NewFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_new_folder, parent, false);
        return new NewFolderViewHolder(this, itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewFolderViewHolder holder, int position) {
        if(position > 0) {
            holder.folderName.setText(folders.get(position - 1).getName());
        }
    }

    @Override
    public int getItemCount() {
        return folders.size() + 1;
    }

    OpenCloseable getLastOpened() {
        return lastOpenedItem;
    }

    void setLastOpened(OpenCloseable lastOpenedItem) {
        this.lastOpenedItem = lastOpenedItem;
    }

    void onFolderCreate(String folderName) {
        if (onFolderChangedListener != null) {
            onFolderChangedListener.onFolderCreate(folderName);
        }
    }

    void onFolderUpdate(String folderName, int position) {
        if (onFolderChangedListener != null) {
            onFolderChangedListener.onFolderUpdate(folderName, position);
        }
    }

    void onFolderDelete(int position) {
        if (onFolderChangedListener != null) {
            onFolderChangedListener.onFolderDelete(position);
        }
    }

    public void setItems(List<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }
}