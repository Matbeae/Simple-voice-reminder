package com.example.simplevoicereminder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String currentFileName;
    private final ArrayList<Recording> recordings = new ArrayList<>();
    private RecordingAdapter recordingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRecordings();
        recordingAdapter = new RecordingAdapter(recordings, this::playRecording);
        recyclerView.setAdapter(recordingAdapter);
    }
    private void loadRecordings() {
        File recordingsDir = getExternalFilesDir(null);
        if (recordingsDir != null && recordingsDir.exists()) {
            File[] files = recordingsDir.listFiles();
            if (files != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".3gpp")) {
                        String date = dateFormat.format(new Date(file.lastModified()));
                        recordings.add(new Recording(file.getAbsolutePath(), date));
                    }
                }
                Collections.sort(recordings, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
            }
        }
    }

    public void startRecording(View v) {
        try {
            releaseRecorder();
            currentFileName = generateFileName();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(currentFileName);
            mediaRecorder.setMaxDuration(20000);

            mediaRecorder.setOnInfoListener((mr, what, extra) -> {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording(v); // Останавливаем запись автоматически, если достигнута максимальная длительность
                    Toast.makeText(this, "Recording stopped: 20-second limit reached", Toast.LENGTH_SHORT).show();
                }
            });
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(View v) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

            // Создаем новый объект Recording и добавляем его в список
            recordings.add(0, new Recording(currentFileName, date));
            recordingAdapter.notifyItemInserted(0);
            recordingAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
        }
        releaseRecorder();
    }

    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return getExternalFilesDir(null).getAbsolutePath() + "/record_" + timeStamp + ".3gpp";
    }

    public void playRecording(String fileName) {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Playing recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }
}