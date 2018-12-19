package my.xubaipei.downloader;


import java.util.ArrayList;
import java.util.List;

import static my.xubaipei.downloader.LogUtil.log;


/**
 * Created by xubaipei on 2018/12/17.
 */

public class MessageQueen extends Thread{
    List<Message> messageQueue;

    public MessageQueen() {
        this.messageQueue = new ArrayList<Message>();
    }

    public void sendMessage(Message message){
        messageQueue.add(message);
        synchronized (this){
            this.notify();
        }
        log("add message queue size:"+messageQueue.size()+"pollTime:"+pollTime);
    }
    public boolean handleMessage(Message message){
        return true;
    }

    public void loop(){
        start();
    }

    long pollTime = 0;

    @Override
    public void run() {
        while (true){
            if (messageQueue.size() == 0){
                synchronized (this){
                    try {
                        this.wait();
                        log("being notify-----");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }
//            Message message = messageQueue.poll();
            Message message = messageQueue.get(0);
            messageQueue.remove(message);
            pollTime++;
            if (message != null){
                try {
                    handleMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
