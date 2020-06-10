package top.mihile.cableMonitorMedia.controller;

import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.mihile.cableMonitorMedia.model.Result;
import top.mihile.cableMonitorMedia.service.RtspTransService;
import top.mihile.cableMonitorMedia.service.WebsockerService;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author Mihile azuretodd@foxmail.com
 * @date 0122020/4/12
 */
@RestController
@RequestMapping("/stream")
public class MediaConrtroller {
    @Autowired
    RtspTransService rtspTransService;
    @Autowired
    WebsockerService websockerService;
    @Value("${mihile.interval}")
    Integer interval;

    @PostMapping("/play")
    public Result play(@RequestBody Map<String,String> data){
        String url = data.get("url");
        // rtsp://admin:mihile123@192.168.1.141:554/Streaming/Channels/102
        String simpleString = StringUtils.replace(url,"//","/");
        // rtsp:/admin:mihile123@192.168.1.141:554/Streaming/Channels/102
        String[] splitList = StringUtils.split(simpleString,"/");
        if(ArrayUtils.isEmpty(splitList) || !StringUtils.equalsIgnoreCase("rtsp:",splitList[0]) || splitList.length<2){
            return Result.builder()
                    .code(400)
                    .msg("不是有效的rtsp地址格式")
                    .build();
        }

        UUID playChannel = UUID.nameUUIDFromBytes(url.getBytes());

        // 如果没有这个 地址的拉流或者拉流结束了，就启动拉流
        // todo 拉流失败
        Thread ffmpegThread = RtspTransService.ffmpegProcessMap.get(playChannel);
        if(ffmpegThread==null){
            rtspTransService.doStartTrans(url,playChannel);
        }else if(ffmpegThread.getState()==Thread.State.TERMINATED){
            rtspTransService.doStartTrans(url,playChannel);
        }

        // interval(60s) 时间内必须再次请求，否则关闭拉流
        Timer timer = RtspTransService.ffmpegProcessTimerMap.get(playChannel);
        if(timer==null){
            timer = new Timer();
            RtspTransService.ffmpegProcessTimerMap.put(playChannel,timer);
        }else {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            RtspTransService.ffmpegProcessTimerMap.put(playChannel,timer);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(RtspTransService.ffmpegProcessMap.get(playChannel)!=null){
                    RtspTransService.ffmpegProcessMap.get(playChannel).interrupt();
                }
            }
        },interval);

        Map<String,String> map = new HashMap<>();
        map.put("path","/stream/live/"+playChannel);

        return Result.builder()
                .code(0)
                .msg("success")
                .data(map)
                .build();
    }

    @RequestMapping("/upload/{playChannel}")
    public void upload(@PathVariable("playChannel") String playChannel, HttpServletRequest request){
        // read data from ffmpeg upload stream
        ServletInputStream inputStream = null;
        try{
            inputStream = request.getInputStream();
        }catch (IOException e){
            System.out.println("open ffmpeg inputStream fail");
            e.printStackTrace();
            return;
        }

            byte[] buffer = new byte[10*1024];
            int len = 0;
        try{
            while ((len = inputStream.readLine(buffer,0,buffer.length))!=-1){
              // 广播数据
              Map<String, Session> wsGroup = WebsockerService.map.get(playChannel);
              if(wsGroup!=null && wsGroup.size()>0){
                  Set<String> sessionIdSet = wsGroup.keySet();
                  for (String sessionId : sessionIdSet) {
                      Session session = wsGroup.get(sessionId);
                      try{
                          if(session.isOpen()){
                              // 这个不能放在外面，跟踪源码发现sendBinary底层会调用clear清空ByteBuffer
                              ByteBuffer bf = ByteBuffer.wrap(buffer,0,len);
                              session.getBasicRemote().sendBinary(bf);
                          }else {
                              wsGroup.remove(sessionId);
                          }
                      }catch (Exception e){
                          e.printStackTrace();
                          System.out.println("session"+session.getId()+"has bean closed");
                      }
                  }
              }
            }
        }catch (IOException e) {
            // ffmpeg was closed,so inputStream was closed
        }
        System.out.println("ffmpeg upload end");
    }


}
