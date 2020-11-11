package com.example.socket_server.webSocketUtil;


import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketUtil {
    /**
     * 记录当前在线的Session，所谓的“容器”
     */
    private static final Map<String, Session> ONLINE_SESSION = new ConcurrentHashMap<>();

    /**
     * 添加session
     * @param userId
     * @param session
     */
    public static void addSession(String userId, Session session){
        // 将新连接存储起来
        ONLINE_SESSION.put( userId, session );
    }

    /**
     * 关闭session
     * @param userId
     */
    public static void removeSession(String userId){
        ONLINE_SESSION.remove( userId );
    }


    /**
     * 给单个用户推送消息
     * @param session
     * @param message
     */
    public static void sendMessage(Session session, String message){
        if(session == null){
            return;
        }
        //这个会把消息推送给前端
        // getAsyncRemote() 异步发送消息方法
        // getBasicRemote() 同步发送消息方法
        RemoteEndpoint.Async async  = session.getAsyncRemote ();
        async.sendText ( message );
    }




    /**
     * 向所有在线人发送消息
     * @param message
     */
    public static void sendMessageForAll(String message) {
        //jdk8 新方法  循环了，你的明白？
        ONLINE_SESSION.forEach((sessionId, session) -> sendMessage(session, message));
    }


}
