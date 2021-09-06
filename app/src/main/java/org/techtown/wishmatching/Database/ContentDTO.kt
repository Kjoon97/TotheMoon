package org.techtown.wishmatching.Database

data class ContentDTO(var nickname :String?=null,
                      var imageUrl :String? =null,
                      var uid: String?=null,
                      var area : String? = null,
                      var userCategory1 : String? = null,
                      var userCategory2: String? = null,
                      var userCategory3: String? = null){
}