package org.techtown.wishmatching.Database

data class ContentDTO(var Nickname :String?=null,     //닉네임 변수
                      var imageUrl :String? =null,   //이미지 주소를 관리하는 변수
                      var uid: String?=null,         //어느 유저가 올렸는지 관리하는 변수
                      var userId : String? = null,   // 올린 유저의 이미지를 관리
                      var timestamp: Long?=null,      //몇시몇분에 컨텐츠를 올렸는지
                      var favoriteCount:Int=0,       //좋아요를 몇 개 눌렀는지 관리하는
                      var favorites : Map<String,Boolean> = HashMap()){     // 좋아요를 누른 유저를 관리(중복 좋아요를 방지하기위함)
    //댓글을 관리해주는 comment클래스
    data class Comment(var uid: String? = null,   //
                       var userId: String?=null,   //이메일 관리
                       var comment:String?=null,   //comment관리
                       var timestamp: Long?=null)   //몇시몇분에 댓글을 달았는지
}