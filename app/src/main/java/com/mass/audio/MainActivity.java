package com.mass.audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mass.audio.library.Recorder;
import com.mass.audio.library.model.OnShortBufferDataChangeListener;

import java.nio.ShortBuffer;

public class MainActivity extends AppCompatActivity implements OnShortBufferDataChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Recorder record = new Recorder(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                MediaRecorder.AudioSource.MIC, 512, null, this);

        record.startRecording();


    }

    @Override
    public void onDataChange(int position, ShortBuffer byteBuffer) {

    }
}
