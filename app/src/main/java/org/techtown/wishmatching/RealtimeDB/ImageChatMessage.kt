package org.techtown.wishmatching.RealtimeDB

class ImageChatMessage(val id: String, val imageUrl: String, val fromId:String, val toId:String, val timestamp: Long,val nickname:String,val flag:Int){
    constructor(): this("","","","",-1,"",0)
}