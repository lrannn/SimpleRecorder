package com.mass.audio.library.model;

import java.nio.ShortBuffer;

/**
 * Created by lrannn on 2017/6/1.
 */

public interface OnShortBufferDataChangeListener extends IBufferDataChangeInterface<ShortBuffer> {

    @Override
    void onDataChange(int position, ShortBuffer shortBuffer);

}
