package org.techtown.wishmatching.Database

data class PostDTO(
    var documentId: String,
    var imageUrl :String?=null,
    var imageUrl2 :String?=null,
    var imageUrl3 :String?=null,
    var imageUrl4 :String?=null,
    var imageUrl5 :String?=null,
    var uid :String? =null,
    var title: String?=null,
    var content : String? = null,
    var category : String? = null,
    var dealsituation : String? = null,
    var date : String? = null)