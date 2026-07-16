package com.spt.bas.web.ws;

import com.alibaba.fastjson.JSON;
import com.spt.bas.web.ws.po.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author moonLight
 */
@Component
@EnableScheduling
@ServerEndpoint("/indexWebSocket/{userId}")
public class IndexWebSocketServer extends TextWebSocketHandler {
    protected Logger logger = LoggerFactory.getLogger(IndexWebSocketServer.class);
    private static final AtomicInteger ONLINE_NUM = new AtomicInteger();
    private static final ConcurrentHashMap<String, Session> SESSION_POOLS = new ConcurrentHashMap<>();

    /**
     * 发送消息
     *
     * @param session
     * @param message
     */
    public void sendMessage(Session session, String message) {
        if (session != null) {
            synchronized (session) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("Failed to send message, closing session: {}", e.getMessage());
                    try {
                        session.close();
                    } catch (IOException closeException) {
                        logger.error("Error while closing session:  {}", closeException.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 给指定用户发送信息
     *
     * @param userId
     * @param message
     */
    public void sendInfo(String userId, String message) {
        Session session = SESSION_POOLS.get(userId);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            logger.error("sendInfo error", e);
        }
    }

    /**
     * 群发广播消息
     *
     * @param message
     */
    public void broadcast(String message) {
        SESSION_POOLS.forEach((key, session) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                logger.error("Failed to send message to session {}: {}", key, e.getMessage());
                SESSION_POOLS.remove(key);
                try {
                    session.close();
                } catch (Exception closeException) {
                    logger.error("Error while closing session: {}", closeException.getMessage());
                }
            }
        });
    }

    /**
     * 建立连接成功调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        SESSION_POOLS.put(userId, session);
        addOnlineCount();
        System.out.println(userId + "加入webSocket！当前连接数为" + ONLINE_NUM);
        // 广播上线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("0");
        msg.setText(userId);
        sendInfo(userId, JSON.toJSONString(msg, true));
//        broadcast(JSON.toJSONString(msg,true));
    }

    /**
     * 关闭连接时调用
     *
     * @param userId
     */
    @OnClose
    public void onClose(@PathParam(value = "userId") String userId) {
        SESSION_POOLS.remove(userId);
        subOnlineCount();
        System.out.println(userId + "断开webSocket连接！当前连接数为" + ONLINE_NUM);
        // 广播下线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("-2");
        msg.setText(userId);
        broadcast(JSON.toJSONString(msg, true));
    }

    /**
     * 收到客户端信息后，根据接收人的username把消息推下去或者群发
     * to=-1群发消息
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        if (StringUtils.equals("HeartBeat", message)) {
            broadcast(message);
            return;
        }
        Message msg = JSON.parseObject(message, Message.class);
        msg.setDate(new Date());
        if (msg.getTo().equals("-1")) {
            broadcast(JSON.toJSONString(msg, true));
        } else {
            sendInfo(msg.getTo(), JSON.toJSONString(msg, true));
        }
    }

    /**
     * 错误时调用
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    public static void addOnlineCount() {
        ONLINE_NUM.incrementAndGet();
    }

    public static void subOnlineCount() {
        ONLINE_NUM.decrementAndGet();
    }

    public static AtomicInteger getOnlineNumber() {
        return ONLINE_NUM;
    }

    public static ConcurrentHashMap<String, Session> getSessionPools() {
        return SESSION_POOLS;
    }
}
