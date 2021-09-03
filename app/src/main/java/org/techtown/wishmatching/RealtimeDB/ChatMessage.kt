package org.techtown.wishmatching.RealtimeDB

class ChatMessage(val id: String, val text: String, val fromId:String, val toId:String, val timestamp: Long,val nickname:String,val flag:Int){
    constructor(): this("","","","",-1,"",0)
}