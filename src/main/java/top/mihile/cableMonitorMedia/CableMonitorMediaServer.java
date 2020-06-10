package top.mihile.cableMonitorMedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.mihile.cableMonitorMedia.service.RtspTransService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mihile azuretodd@foxmail.com
 * @date 0122020/4/12
 */
@SpringBootApplication
public class CableMonitorMediaServer {
    public static boolean audio = false;

    public static void main(String[] args) {
        for (String arg : args) {
            if ("-audio".equals(arg)) {
                audio = true;
                break;
            }
        }
        SpringApplication.run(CableMonitorMediaServer.class, args);
    }
}
