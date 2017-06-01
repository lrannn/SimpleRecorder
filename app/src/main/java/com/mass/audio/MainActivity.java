package com.mass.audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mass.audio.library.Recorder;
import com.mass.audio.library.model.OnByteBufferDataChangeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements OnByteBufferDataChangeListener {

    public static final String TEST_FILE_PATH = Environment.getExternalStorageDirectory() + "/test.pcm";

    private ImageButton imageBtn;

    private Recorder mRecorder;
    private FileOutputStream fileOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageBtn = (ImageButton) findViewById(R.id.action_image);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder == null) {
                    return;
                }
                boolean recording = mRecorder.isRecording();
                if (recording) {
                    ((ImageButton) v).setImageResource(R.drawable.record);
                    mRecorder.stop();
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.pause);
                    mRecorder.startRecording();
                }
            }
        });

        boolean b = testFile();
        if (!b) {
            Toast.makeText(this, "创建文件失败~", Toast.LENGTH_SHORT).show();
        }

        mRecorder = new Recorder(44100,
                AudioFormat.CHANNEL_IN_MONO/*单双声道*/,
                AudioFormat.ENCODING_PCM_16BIT/*格式*/,
                MediaRecorder.AudioSource.MIC/*AudioSource*/,
                512/*period*/,
                null/**/,
                this/*onDataChangeListener*/);


    }

    private boolean testFile() {
        try {
            fileOutputStream = new FileOutputStream(new File(TEST_FILE_PATH));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDataChange(int position, ByteBuffer byteBuffer) {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.write(byteBuffer.array());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
