package com.mass.audio.library;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.mass.audio.library.model.IBufferDataChangeInterface;
import com.mass.audio.library.model.OnByteDataChangeListener;
import com.mass.audio.library.model.OnShortBufferDataChangeListener;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * YmAudioRecorder是个进行了简单封装的AudioRecor使用类，传入对应的参数和回调接口即可快速进行录音
 */
public class YmAudioRecorder {

    private AudioRecord mAudioRecord;
    private ByteBuffer byteBuffer;
    private ShortBuffer shortBuffer;

    private YmAudioRecorder() {
    }

    /**
     * @param samplerate  采样率
     * @param channel     声道属性，参考:{@link AudioFormat}
     * @param format      位深度，参考{@link AudioFormat}
     * @param audioSource 输入源 {@link android.media.MediaRecorder.AudioSource}
     * @param period      每次处理多少sample
     * @param l           处理完sample的回调
     * @param listener    读取完数据的回调
     */
    public YmAudioRecorder(int samplerate,
                           int channel,
                           int format,
                           int audioSource,
                           int period,
                           final OnPeriodInFramesChangeListener l,
                           final IBufferDataChangeInterface listener) {
        int minBufferSize = AudioRecord.getMinBufferSize(samplerate, channel, format);
        mAudioRecord = new AudioRecord(audioSource, samplerate, channel, format, minBufferSize);
        mAudioRecord.setPositionNotificationPeriod(period);
        if (isEncodingPCM16Bit()) {
            if (listener instanceof OnByteDataChangeListener) {
                byteBuffer = ByteBuffer.allocate(period * 2);
            } else {
                shortBuffer = ShortBuffer.allocate(period);
            }
        } else {
            if (listener instanceof OnShortBufferDataChangeListener) {
                throw new IllegalArgumentException("Audio format is pcm 16 bit, so you only use OnByteBufferDataChangeListener!");
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
                } else {
                    int position = read(byteBuffer.array());
                    listener.onDataChange(position, byteBuffer);
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
    interface OnPeriodInFramesChangeListener {
        void onFrames(AudioRecord record);
    }

}
