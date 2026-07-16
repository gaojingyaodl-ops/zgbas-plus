var WsRes = {};
WsRes.socket = null;
WsRes.connect = (function (host) {
    if ("WebSocket" in window) {
        WsRes.socket = new WebSocket(host);
    } else if ("MozWebSocket" in window) {
        WsRes.socket = new MozWebSocket(host);
    } else {
        console.log("Error: WebSocket is not supported by this browser.");
        return;
    }
    WsRes.socket.onopen = function () {
        console.log("Info: websocket已启动.");
        // 心跳检测重置
        heartCheck.reset().start(WsRes.socket);
    };
    WsRes.socket.onclose = function () {
        console.log("Info: websocket已关闭.");
    };
    WsRes.socket.onmessage = function (message) {
        heartCheck.reset().start(WsRes.socket);
        if (message.data == null || message.data == '' || "HeartBeat" == message.data) {
            //心跳消息
            console.log("-- HeartBeat --");
        } else {
            // 收到 Websocket消息，执行对应业务逻辑
            try {
                var messageData = eval('(' + message.data + ')');
                console.log(messageData);
                if (messageData && messageData.messageType == "F") {
                    // 资金方余额更新消息通知
                    WsRes.updateFundAmount(messageData);
                } else if (messageData && messageData.messageType == "W") {
                    // 待办事项数量更新消息通知
                    WsRes.updateBadge(messageData);
                }
            } catch (err) {
                console.log("onMessage with business error");
            }
        }
    };
});
WsRes.initialize = function () {
    WsRes.currFundCompanyId = $("#currFundCompanyId").val();
    WsRes.currUserId = $("#currUserId").val();
    if (WsRes.currUserId) {
        if (window.location.protocol == "http:") {
            WsRes.connect("ws://" + window.location.host + "/indexWebSocket/" + WsRes.currUserId);
        } else {
            WsRes.connect("wss://" + window.location.host + "/indexWebSocket/" + WsRes.currUserId);
        }
    }
};
WsRes.sendMessage = (function () {
    var message = document.getElementById("WsPring").value;
    if (message != "") {
        WsRes.socket.send(message);
        document.getElementById("WsPring").value = "";
    }
});
WsRes.updateFundAmount = (function (messageData) {
    if (WsRes.currFundCompanyId == messageData.fundCompanyId) {
        let fundAmountElements = document.querySelectorAll('a[href="#"][onclick="openFundFlow(\'all\');"]');
        if (fundAmountElements.length > 0) {
            fundAmountElements.forEach((element) => {
                element.innerHTML = '<i class="fa fa-cny"></i>' + messageData.fundAmount;
            });
        }
        let fundAmountQgElements = document.querySelectorAll('a[href="#"][onclick="openFundFlow(\'qg\');"]');
        if (fundAmountQgElements.length > 0) {
            fundAmountQgElements.forEach((element) => {
                element.innerHTML = '<i class="fa fa-cny"></i>' + messageData.fundAmountQg;
            });
        }
        let fundAmountWsElements = document.querySelectorAll('a[href="#"][onclick="openFundFlow(\'ws\');"]');
        if (fundAmountWsElements.length > 0) {
            fundAmountWsElements.forEach((element) => {
                element.innerHTML = '<i class="fa fa-cny"></i>' + messageData.fundAmountWs;
            });
        }
    }
});
WsRes.updateBadge = (function (messageData) {
    if (WsRes.currUserId == messageData.targetUserId){
        const badgeElement = document.querySelector('.wait-done-badge');
        // const lockClsElement = document.querySelector('.lockCls');
        let waitDealNum = messageData.waitDealNum;
        if (badgeElement){
            if (waitDealNum && waitDealNum > 0) {
                waitDealNum = waitDealNum > 99 ? "99+" : waitDealNum;
                badgeElement.textContent = waitDealNum;
                badgeElement.style.display = 'inline-block';
                // if (lockClsElement){
                //     lockClsElement.style.paddingLeft = '12px';
                // }
            } else {
                badgeElement.style.display = 'none';
                // if (lockClsElement){
                //     lockClsElement.style.paddingLeft = '0px';
                // }
            }
        }
    }
});
WsRes.initialize();

//心跳检测
var heartCheck = {
    timeout: 5000,// 5秒
    timeoutObj: null,
    serverTimeoutObj: null,
    reset: function () {
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        return this;
    },
    start: function (ws) {
        var self = this;
        this.timeoutObj = setTimeout(function () {
            // 这里发送一个心跳，后端收到后，返回一个心跳消息，
            // onmessage拿到返回的心跳就说明连接正常
//			console.log('start heartCheck');
            ws.send("HeartBeat");
            self.serverTimeoutObj = setTimeout(function () {// 如果超过一定时间还没重置，说明后端主动断开了
                ws.close();// 如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect
                // 会触发onclose导致重连两次
            }, self.timeout)
        }, this.timeout);
    }
}
