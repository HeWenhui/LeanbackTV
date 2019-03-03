
    /** 
     * 重写window.parent.postMessage
     */
    window.parent={}
    /**
     * 获取课件发送的消息
     * @param {Object} type 课件广播发送的消息message
     * @param {String} data 其值可以是字符串 “*”，或者是一个URI
     */
    window.parent.postMessage=function(type,data){
    console.log('postmessage1',type, data)
        var data = {
            type: type,
            data: data
        }
        xesApp && xesApp.postMessage(JSON.stringify(data));
    }
    /**
     * 给课件发送消息
     * @param {Object} type 表示该message的类型
     * @param {String} data 其值可以是字符串 “*”，或者是一个URI
     */
    function sendToCourseware(type,data){
     console.log('sendToCourseware',type, data)
        window.postMessage(type, data)
    }

