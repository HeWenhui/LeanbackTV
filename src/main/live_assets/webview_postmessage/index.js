'use strict'
/** 
 * 重写window.parent.postMessage
 */
window.parent = {}
/**
 * 获取课件发送的消息
 * @param {Object} message 课件广播发送的消息message
 * @param {String} origin 其值可以是字符串 “*”，或者是一个URI
 */

window.parent.postMessage = function (message, origin) {
    var data = {
        where: 'postMessage',
        message: message,
        origin: origin
    }
    console.log(data)
    window.xesApp && xesApp.postMessage(JSON.stringify(data));
}
/**
 * 监听e.source.postMessage
 */
window.addEventListener('message', function (e) {
    var data = {
        where: 'addEventListener',
        message: e.data,
        origin: e.origin
    }
    console.log(data)
    window.xesApp && xesApp.postMessage(JSON.stringify(data));
})

/**
 * 给课件发送消息
 * @param {Object} message 表示该message的类型
 * @param {String} origin 其值可以是字符串 “*”，或者是一个URI
 */

function sendToCourseware(message, origin) {
    window.postMessage(message, origin)
     window.xesApp && xesApp.onReceive(JSON.stringify(message));
}

function testCourseware(message, origin) {
     window.xesApp && xesApp.onReceive(JSON.stringify(message));
}