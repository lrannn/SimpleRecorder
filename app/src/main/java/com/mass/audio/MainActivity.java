package com.mass.audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mass.audio.library.model.OnByteDataChangeListener;
import com.mass.audio.library.YmAudioRecorder;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements OnByteDataChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YmAudioRecorder record = new YmAudioRecorder(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                MediaRecorder.AudioSource.MIC, 512, null, this);

        record.startRecording();


    }

    @Override
    public void onDataChange(int position, ByteBuffer byteBuffer) {

    }
}
