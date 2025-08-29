package com.example.simplevoicereminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {
    private final List<Recording> recordings;
    private final OnRecordingClickListener listener;

    public RecordingAdapter(List<Recording> recordings, OnRecordingClickListener listener) {
        this.recordings = recordings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recording, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recording recording = recordings.get(position);
        String fileName = new File(recording.getFileName()).getName();
        holder.nameTextView.setText(fileName);
        holder.dateTextView.setText(recording.getDate());
        holder.itemView.setOnClickListener(v -> listener.onRecordingClick(recording.getFileName()));
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView dateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.recording_name);
            dateTextView = itemView.findViewById(R.id.recording_date);
        }
    }

    interface OnRecordingClickListener {
        void onRecordingClick(String fileName);
    }
}