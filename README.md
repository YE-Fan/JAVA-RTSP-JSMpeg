# JAVA-RTSP-JSMpeg
使用JSMpeg播放摄像头RTSP流     
using JSMpeg to play IP Camera RTSP stream

是参考[gin-rtsp](https://github.com/wanghaoxi3000/gin-rtsp)项目构建的，修复了一些边缘情况的bug，目前只在windows下面测试过

使用JAVA调用ffmpeg拉取摄像头rtsp流，通过websocket转发，前端通过JSMpeg来接收websocket数据，播放视频

## demo
```java -jar .\mihile-cableMonitor-media-1.0-SNAPSHOT.jar```
然后打开浏览器，输入*localhost:3000*，输入正确的rtsp地址，点击play播放，注意，rtsp地址必须正确，否则如果摄像头密码错误之类的，可能造成摄像头因为多次密码错误而被锁定设备，需要一段时间摄像头自己解锁。

画面会在60s后自动停止，要持续播放需要在60s内再次点击play

![demo.png](demo.png)
# 环境要求
## ffmpeg
在命令行输入ffmpeg -v，能够出现版本信息

# API
```
GET /ping

Response
{
    "code": 0,
    "msg": "pong",
    "data": null
}
```

```
POST /stream/play
{
    "url":"rtsp://admin:mihile123@192.168.1.141:554/Streaming/Channels/102"
}

Response
{
    "code": 0,
    "msg": "success",
    "data": {
        "path": "/stream/live/72b8cf14-f74b-3368-ab63-e5e1bba821b2"
    }
}
```

websocket播放地址就是 ws://127.0.0.1:3000/stream/live/72b8cf14-f74b-3368-ab63-e5e1bba821b2

每60s需要请求一次*POST /stream/play*否则会结束拉流

# 常见问题 FAQ
1. 画面闪烁  
可能是输入了错误的rtsp地址播放，然后又输入了正确的地址播放，就会造成闪烁。  
解决方法：刷新页面，输入正确的rtsp地址播放  

2. 画面大小  
在top.mihile.cableMonitorMedia.service.RtspTransService这个类中修改ffmpeg参数，默认是640*480  

3. 画面卡顿、延迟  
在top.mihile.cableMonitorMedia.service.RtspTransService这个类中修改ffmpeg参数，不同的转码参数造成的画面质量等会不同  

4. 没有声音  
这个方法不适合播放音频，ffmpeg转码加入音频参数会大大降低效率，可能造成画面延迟、卡顿，如有需要自行研究  

