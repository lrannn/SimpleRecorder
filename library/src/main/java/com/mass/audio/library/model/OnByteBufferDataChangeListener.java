package com.mass.audio.library.model;

import java.nio.ByteBuffer;

/**
 * Created by lrannn on 2017/6/1.
 */

public interface OnByteBufferDataChangeListener extends IBufferDataChangeInterface<ByteBuffer> {

    @Override
    void onDataChange(int position, ByteBuffer byteBuffer);
}
