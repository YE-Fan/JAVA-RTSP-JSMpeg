package top.mihile.cableMonitorMedia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mihile.cableMonitorMedia.service.WebsockerService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint
 * 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *
 * @author Mihile azuretodd@foxmail.com
 * @date 0122020/4/12
 */
@ServerEndpoint(value="/stream/live/{playChannel}")
@Component
public class WebSocketController {
    @Autowired
    WebsockerService websockerService;

    @OnOpen
    public void onOpen(@PathParam("playChannel") String playChannel, Session session){
        // 加入对应的playchannel
        Map<String, Session> wsGroup = WebsockerService.map.get(playChannel);
        if(wsGroup==null){
            wsGroup = new ConcurrentHashMap<String, Session>();
        }
        wsGroup.put(session.getId(),session);
        WebsockerService.map.put(playChannel,wsGroup);

        System.out.println(session.getId() + " 加入连接  组为："+playChannel);
    }

    @OnClose
    public void onClose(Session session){ System.out.println(session.getId() + " 关闭连接");  }

    @OnError
    public void onError(Session session, Throwable error){  
       System.out.println(session.getId() + " 发生错误");
       error.printStackTrace(); 
    }

} 
