package com.example.socket_server.websocket;


import com.example.socket_server.webSocketUtil.WebSocketUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * 首先你需要先熟悉mvc三层架构的控制层书写，然后再理解这里就好理解了
 * @ServerEndpoint(value="/webSocket/{userid}") 跟Controller层里RequsetMapping里定义的接口一个道理
 * 只是它是固定的ws://127.0.0.1:9999/webSocket/{userid}（怎么改端口还没研究），这个连接是前端调用时用的，
 * 如果你不研究前端那不需要关心。
 */
@Component
@ServerEndpoint(value="/webSocket/{userid}") //{userid}可以是任何id，只是这条连接的唯一标识
public class WebSocketServer {

    /**
     * 连接事件，加入注解
     * @param userId
     * @param session
     * 首先这个注解的功能就是在加入动作时将连接和id以key-value的形式加入到一个“容器”，前端调用请求加入时会调用
     * 至于加入哪里，WebSocketUtil.addSession (userId, session );从这里面进去看
     */
    @OnOpen
    public void onOpen( @PathParam  ( value = "userid" ) String userId, Session session ) {
        String message ="[" + userId + "]加入聊天室！！";

        // 添加到session的映射关系中
        WebSocketUtil.addSession (userId, session );
        // 广播通知，某用户上线了
        WebSocketUtil.sendMessageForAll ( message );
    }



    /**
     * 用户断开链接
     * @param userId
     * @param session
     *  断开连接就是从“容器”中以id删除目标连接
     */
    @OnClose
    public void onClose(@PathParam  ( value = "userid" ) String userId, Session session ) {
        String message ="[" + userId + "]退出了聊天室...";
        // 删除映射关系
        WebSocketUtil.removeSession ( userId );
        // 广播通知，用户下线了
        WebSocketUtil.sendMessageForAll ( message );
    }



    /**
     * 当接收到用户上传的消息
     * @param userId
     * @param session
     */
    @OnMessage
    public void onMessage(@PathParam  ( value = "userid" ) String userId, Session session ,String message) {
        String msg ="[" + userId + "]:"+message;
        // 直接广播
        //这个广播底部就是循环同步信息到前端，从此处点进去看就能理解了
        WebSocketUtil.sendMessageForAll ( msg );
    }


    /**
     * 处理用户活连接异常
     * @param session
     * @param throwable
     * 这个没研究，都异常就闭连接呗。
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }

}


