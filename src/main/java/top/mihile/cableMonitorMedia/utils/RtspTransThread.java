package top.mihile.cableMonitorMedia.utils;

import top.mihile.cableMonitorMedia.service.RtspTransService;

import java.io.InputStream;
import java.util.List;

/**
 * @author Mihile azuretodd@foxmail.com
 * @date 0272020/5/27
 */
public class RtspTransThread extends Thread {
    private List<String> commands;
    public RtspTransThread(final List<String> commands){
        this.commands = commands;
    }

    @Override
    public void run() {
        try{
            while (true){
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process process = pb.start();

                InputStream inputStream = process.getInputStream();
                InputStream errorStream = process.getErrorStream();
                ThreadedStreamHandler inputStreamHandler = new ThreadedStreamHandler(inputStream);
                ThreadedStreamHandler errorStreamHandler = new ThreadedStreamHandler(errorStream);
                inputStreamHandler.start();
                errorStreamHandler.start();

                while (true){
                    if(!process.isAlive()){
                        break;
                    }
                    if(Thread.currentThread().isInterrupted()){
                        process.destroy();
                        break;
                    }
                }
                int exitValue = process.waitFor();

                if(Thread.currentThread().isInterrupted()){
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("RtspTransThread End");
    }
}
