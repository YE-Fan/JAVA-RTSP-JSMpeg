package top.mihile.cableMonitorMedia.service;

import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mihile azuretodd@foxmail.com
 * @date 0122020/4/12
 */
@Service
public class WebsockerService {
    /**
     * playChannel SessionId Session
      */
    public static Map<String, Map<String,Session>> map = new ConcurrentHashMap<>();

}
