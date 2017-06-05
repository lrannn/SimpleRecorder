# SimpleRecorder
这是用AudioRecord来封装的录音代码，如果对音频数据源有需求的话，推荐使用。如果仅仅使用录音文件的话，最好使用MediaRecord相关


### 用法

```
 
 mRecorder = new Recorder(44100/*采样率*/,
                AudioFormat.CHANNEL_IN_MONO/*单双声道*/,
                AudioFormat.ENCODING_PCM_16BIT/*格式*/,
                MediaRecorder.AudioSource.MIC/*AudioSource*/,
                512/*每次多少个采样*/,
                this/*接受数据的监听，如果不需要可以穿null*/);
 
 mRecorder.startRecording();                 
                
```

### License
```
Copyright (C) 2016 lrannn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```




