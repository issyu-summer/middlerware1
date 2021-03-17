
//当需要进行聊天时
//首先需要建立和服务端的server连接

//let作用域比var更加严格
let socket;
let chatClose=false;
let json="";
function getUserName() {
    //获取username
}

function getIdentityName() {
    //和queueName或者topicName
}

function initChat(initJson){
    //初始化聊天窗口
}

function modifyChatStatus(){
    //如果关闭了chat
    //则chatClose=true;
}

function realTimeDisplay(json){
    //实时显示json中的数据
}

function webSocket() {
    //检测浏览器是否支持webSocket,一般都支持
    if(typeof(WebSocket) == "undefined") {
        console.log("您的浏览器不支持WebSocket");
    }else {
        console.log("您的浏览器支持WebSocket");
    }
    //建立webSocket连接,
    //ws://localhost:8080/websocket/{username}/{identityName}
    //此处需要获取出username,和queueName或者identityName
    let username;
    let identityName;
    let socketUrl = "ws://localhost:9000/websocket/"+username+"/"+identityName;
    // socketUrl=
    //     socketUrl.replace("https","ws").replace("http","ws");
    socket = new WebSocket(socketUrl);
    //至此,服务端和客户端建立webSocket连接成功
    if(socket==null){
        //几乎不会失败
        console.log("建立连接失败")
    }else {
        console.log("建立连接成功,socketUrl=" + socketUrl);
    }
    //打开连接,仅仅打开连接并不能输出任何信息。
    socket.onopen = function() {
        console.log("websocket连接🔗已启用");
    }
    //获得消息事件,才能真正的输出服务器传到前端的信息
    //从WebSocketServer.onOpen()/onMsg()/onClose()处传入的消息都要通过socket.onmessage来接收

    //按照逻辑,首先初始化一次,获取上次还未消费到的消息。
    let initJson="";
    socket.onmessage=function (msg){
        initJson=msg.data;
    }
    //如果初始化消息不为空的话,就要初始化聊天窗口
    if(initJson!==""){
        initChat(initJson);
    }

    //初始化之后,需要循环监听,直到连接关闭,即关闭聊天窗口
    //还是需要客户端向服务端传递一个讯号？
    let str="";
    //消息的数量
    let size=0;
    //当chat关闭的时候,会跳出循环,自然进入到了关闭连接
    //有问题,chat不断轮询,资源必然会耗尽
    while (!chatClose){
        socket.send(str);
        socket.onmessage=function (msg){
            json=msg.data;
        }
        //获取到json时,实时显示json
        if(json!=null) {
            realTimeDisplay(json);
        }
    }
    //关闭连接
    socket.onclose = function() {
        console.log("websocket已关闭");
    }
    //发生了错误
    socket.onerror = function(msg) {
        console.log("websocket发生了错误:"+msg.data);
    }
}
