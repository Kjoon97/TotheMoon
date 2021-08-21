package org.techtown.wishmatching.Database

data class PostDTO(
    var documentId: String,
    var imageUrl :String?=null,
    var uid :String? =null,
    var title: String?=null,
    var content : String? = null,
    var category : String? = null,
    var dealsituation : String? = null)