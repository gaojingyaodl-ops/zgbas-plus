var WsRes = {};
WsRes.socket = null;
WsRes.connect = (function(host) {
    if ("WebSocket" in window) {
        WsRes.socket = new WebSocket(host);
    } else if ("MozWebSocket" in window) {
        WsRes.socket = new MozWebSocket(host);
    } else {
        console.log("Error: WebSocket is not supported by this browser.");
        return;
    }
    WsRes.socket.onopen = function() {
        console.log("Info: websocket已启动.");
        // 心跳检测重置
        heartCheck.reset().start(WsRes.socket);
    };
    WsRes.socket.onclose = function() {
        console.log("Info: websocket已关闭.");
    };
    WsRes.socket.onmessage = function(message) {
        //console.log("Info: 心跳消息");
        heartCheck.reset().start(WsRes.socket);
        if (message.data == null || message.data === '' || "HeartBeat" === message.data){
            //心跳消息
			// console.log("Info: 心跳消息2--->");
            // console.log(message.data)
            return;
        }
        // console.log(message);
        try {
            var data =  eval('('+message.data+')')
            // console.log("data"+data);
            if (data.countReadFlg>0){
                 $("#redPoint").addClass('redPoint')
            }else if ( data.countReadFlg==0){
                $("#redPoint").removeClass('redPoint');
            }
            if (data.countCompleteFlg>0){
                $("#redPoint2").addClass('redPoint2');
            }else if (data.countCompleteFlg==0){
                $("#redPoint2").removeClass('redPoint2');
            }
        }catch (err){

        }
        //---start 收到 Websocket消息，执行业务操作，如去掉指定的网批记录

    };
});
WsRes.initialize = function() {
    // ${@shiroUtil.getCurrentUserName()}
    // ${@shiroUtil.getCurrentUserId()}
    WsRes.userCd = $('#currentUserId').val();
    if (window.location.protocol == "http:") {
        WsRes.connect("ws://" + window.location.host + "/webSocket/"+WsRes.userCd);
    } else {
        WsRes.connect("wss://" + window.location.host + "/webSocket/"+WsRes.userCd);
    }
};
WsRes.sendMessage = (function() {

});
WsRes.initialize();


//心跳检测
var heartCheck = {
    timeout : 1000,// 60秒
    timeoutObj : null,
    serverTimeoutObj : null,
    reset : function() {
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        return this;
    },
    start : function(ws) {
        var self = this;
        this.timeoutObj = setTimeout(function() {
            // 这里发送一个心跳，后端收到后，返回一个心跳消息，
            // onmessage拿到返回的心跳就说明连接正常
//			console.log('start heartCheck');
            ws.send("HeartBeat");
            self.serverTimeoutObj = setTimeout(function() {// 如果超过一定时间还没重置，说明后端主动断开了
                ws.close();// 如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect
                // 会触发onclose导致重连两次
            }, self.timeout)
        }, this.timeout);
    }
}


