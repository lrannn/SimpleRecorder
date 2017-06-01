package com.mass.audio.library.model;

import java.nio.Buffer;

/**
 * Created by lrannn on 2017/6/1.
 */
public interface IBufferDataChangeInterface<T extends Buffer> {
    void onDataChange(int position, T t);
}
