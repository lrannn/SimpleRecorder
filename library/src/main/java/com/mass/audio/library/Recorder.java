/*
 * Copyright (C) lrannn
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mass.audio.library;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.mass.audio.library.model.IBufferDataChangeInterface;
import com.mass.audio.library.model.OnByteBufferDataChangeListener;
import com.mass.audio.library.model.OnShortBufferDataChangeListener;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Recorder是个进行了简单封装的AudioRecor使用类，传入对应的参数和回调接口即可快速进行录音
 */
public class Recorder {

    private AudioRecord mAudioRecord;
    private ByteBuffer byteBuffer;
    private ShortBuffer shortBuffer;


    private OnPeriodInFramesChangeListener l;

    private Recorder() {
    }

    /**
     * 构造方法传入采样率和回调接口，如果你是8Bit的数据，那么必须要用{@link OnByteBufferDataChangeListener}
     * 来进行回调。如果你是16Bit的数据，可以使用{@link OnByteBufferDataChangeListener}或者{@link OnShortBufferDataChangeListener}
     * 来进行回调，只不过一个传出去的是byteBuffer，一个是shortBuffer,请注意，当你使用ByteBuffer的时候，
     * byteBuffer的大小是period的两倍，取数据的时候请注意大小
     *
     * @param samplerate  采样率，常用为44100，48000
     * @param channel     声道属性，参考:{@link AudioFormat}
     * @param format      位深度，参考{@link AudioFormat}
     * @param audioSource 输入源 {@link android.media.MediaRecorder.AudioSource}
     * @param period      处理sample数量
     * @param listener    读取完数据的回调
     */
    public Recorder(int samplerate,
                    int channel,
                    int format,
                    int audioSource,
                    int period,
                    final IBufferDataChangeInterface listener) {
        int minBufferSize = AudioRecord.getMinBufferSize(samplerate, channel, format);
        mAudioRecord = new AudioRecord(audioSource, samplerate, channel, format, minBufferSize);
        mAudioRecord.setPositionNotificationPeriod(period);
        if (isEncodingPCM16Bit()) {
            if (listener instanceof OnByteBufferDataChangeListener) {
                byteBuffer = ByteBuffer.allocate(period * 2);
            } else {
                shortBuffer = ShortBuffer.allocate(period);
            }
        } else {
            if (listener instanceof OnShortBufferDataChangeListener) {
                throw new IllegalArgumentException("Audio format is pcm 8 bit, so you only use OnByteBufferDataChangeListener!");
            }
            byteBuffer = ByteBuffer.allocate(period);
        }
        mAudioRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioRecord recorder) {

            }

            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                if (l != null)
                    l.onFrames(recorder);

                if (listener == null) {
                    return;
                }

                if (listener instanceof OnShortBufferDataChangeListener) {
                    int position = read(shortBuffer.array());
                    ((OnShortBufferDataChangeListener) listener).onDataChange(position, shortBuffer);
                } else if (listener instanceof OnByteBufferDataChangeListener) {
                    int position = read(byteBuffer.array());
                    ((OnByteBufferDataChangeListener) listener).onDataChange(position, byteBuffer);
                }

            }
        });
    }

    public void startRecording() {
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
            return;
        mAudioRecord.startRecording();
    }

    public void stop() {
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED)
            return;
        mAudioRecord.stop();
    }

    public boolean isRecording() {
        return mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }

    public void setOnPeriodInFramesChangeListener(OnPeriodInFramesChangeListener listener) {
        l = listener;
    }


    private int read(byte[] data) {
        int read = mAudioRecord.read(data, 0, data.length);
        return read;
    }

    private int read(short[] data) {
        int read = mAudioRecord.read(data, 0, data.length);
        return read;
    }

    /**
     * 返回当前AudioRecord的AudioFormat信息，判断是否为16bit来进行读取byte[]或者short[]
     *
     * @return 当前音源是否为PCM16比特
     */
    private boolean isEncodingPCM16Bit() {
        return mAudioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT;
    }

    /**
     * 当走完设定的period*frame的时候调用
     */
    public interface OnPeriodInFramesChangeListener {
        void onFrames(AudioRecord record);
    }

}
