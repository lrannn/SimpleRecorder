package com.mass.audio;

import android.Manifest;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
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
    private static final int REQUEST_CODE = 0x01;
    private static final int NUM_SAMPLES = 512;
    private static final String[] perms = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ImageButton mImageButton;

    private Recorder mRecorder;
    private FileOutputStream mStream;

    private byte[] output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, REQUEST_CODE);
        }

        mImageButton = (ImageButton) findViewById(R.id.action_image);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder == null || !mRecorder.isInitialized()) {
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

        boolean result = createOutputFile();
        if (!result) {
            Toast.makeText(this, "创建文件失败~", Toast.LENGTH_SHORT).show();
        }

        mRecorder = new Recorder(44100,
                AudioFormat.CHANNEL_IN_MONO/*单双声道*/,
                AudioFormat.ENCODING_PCM_16BIT/*格式*/,
                MediaRecorder.AudioSource.MIC/*AudioSource*/,
                NUM_SAMPLES/*period*/,
                this/*onDataChangeListener*/);

        output = new byte[NUM_SAMPLES * 2];

    }

    private boolean createOutputFile() {
        try {
            mStream = new FileOutputStream(new File(TEST_FILE_PATH));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        mRecorder.release();
        super.onDestroy();
    }

    @Override
    public void onDataChange(int position, ByteBuffer buffer) {
        try {
            if (mStream != null) {
                mStream.write(buffer.array());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 适用于小端在前的short转换成byte
     *
     * @param value 16位short值
     * @return byte[]数组
     */
    private byte[] short2byte(short value) {
        byte[] data = new byte[2];
        data[0] = (byte) (value & 0xFF);
        data[1] = (byte) ((value >> 8) & 0xFF);
        return data;
    }
}
