package com.spt.bas.web.ws;

import com.alibaba.fastjson.JSON;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
// Phase 4 stub: uncomment when IApproveWaitDealClient impl is ported
// import com.spt.bas.client.entity.ApproveWaitDeal;
// import com.spt.bas.client.remote.IApproveWaitDealClient;
// import com.spt.bas.client.vo.ApproveWaitSearchVo;
import com.spt.bas.web.ws.po.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
// Phase 4 stub: uncomment when IApproveWaitDealClient impl is ported
// import java.util.List;
// import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/webSocket/{username}")
@Component
@EnableScheduling
public class WebSocketServer {

    // Phase 4 stub: uncomment when IApproveWaitDealClient impl is ported
    // private  static IApproveWaitDealClient iApproveWaitDealClient ;

    private static IAuthOpenFacade authOpenFacade;

    // Phase 4 stub: uncomment when IApproveWaitDealClient impl is ported
    // @Autowired
    // public void setChatService(IApproveWaitDealClient iApproveWaitDealClient) {
    //     WebSocketServer.iApproveWaitDealClient = iApproveWaitDealClient;
    // }
    @Autowired
    public void setAdminOpenFacade(IAuthOpenFacade authOpenFacade){
        WebSocketServer.authOpenFacade = authOpenFacade;
    }
	 //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }
    //给指定用户发送信息
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 群发消息
    public void broadcast(String message){
    	for (Session session: sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch(Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }

//    @Scheduled(cron = "0/2 * * * * ?")
//    private void autoSendInfo() {
//
//        for (Map.Entry<String, Session> sessionEntry : sessionPools.entrySet()) {
//            Session session = sessionEntry.getValue();
//            try {
//                    sendMessage(session, String.valueOf(2));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "username") String userName){
        sessionPools.put(userName, session);
        addOnlineCount();
        // System.out.println(userName + "加入webSocket！当前连接数为" + onlineNum);
        // 广播上线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("0");
        msg.setText(userName);
        // Phase 4 stub: uncomment when IApproveWaitDealClient impl is ported
        // ApproveWaitSearchVo searchVo =new ApproveWaitSearchVo();
        // searchVo.setRelaUserId(userName);
        // SysDeptSdk sysDeptSdk = authOpenFacade.findDeptByUserId(Long.valueOf(userName));
        // if (Objects.nonNull(sysDeptSdk)){
        //     searchVo.setRelaDeptId(sysDeptSdk.getDeptId());
        // }
        // List<ApproveWaitDeal> list = iApproveWaitDealClient.findPageWaitDealCount(searchVo);
        // long countRedFlg = list.stream().filter(string -> "0".equals(string.getReadFlg())).count();
        // long countCompleteFlg =list.stream().filter(string->"0".equals(string.getCompleteFlg())).count();
        // msg.setCountReadFlg(countRedFlg);
        // msg.setCountCompleteFlg(countCompleteFlg);

        sendInfo(userName,JSON.toJSONString(msg,true));
//        broadcast(JSON.toJSONString(msg,true));
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "username") String userName){
        sessionPools.remove(userName);
        subOnlineCount();
        // System.out.println(userName + "断开webSocket连接！当前连接数为" + onlineNum);
        // 广播下线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("-2");
        msg.setText(userName);
        broadcast(JSON.toJSONString(msg,true));
    }

    //收到客户端信息后，根据接收人的username把消息推下去或者群发
    // to=-1群发消息
    @OnMessage
    public void onMessage(String message) throws IOException{
        if (StringUtils.equals("HeartBeat",message)){
            broadcast(message);
            return;
        }
        Message msg=JSON.parseObject(message, Message.class);
		msg.setDate(new Date());
		if (msg.getTo().equals("-1")) {
			broadcast(JSON.toJSONString(msg,true));
		} else {
			sendInfo(msg.getTo(), JSON.toJSONString(msg,true));
		}
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        throwable.printStackTrace();
    }

    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    public static AtomicInteger getOnlineNumber() {
        return onlineNum;
    }

    public static ConcurrentHashMap<String, Session> getSessionPools() {
        return sessionPools;
    }
}
