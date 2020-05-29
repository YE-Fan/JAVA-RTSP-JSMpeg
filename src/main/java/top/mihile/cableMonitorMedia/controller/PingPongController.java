package top.mihile.cableMonitorMedia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mihile.cableMonitorMedia.model.Result;

/**
 * @author Mihile azuretodd@foxmail.com
 * @date 0282020/5/28
 */
@RestController
public class PingPongController {
    @GetMapping("/ping")
    public Result pingPong(){
        return  Result.builder().code(0).msg("Pong").build();
    }
}
