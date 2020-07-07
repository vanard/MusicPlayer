package com.vanard.learnmusicplayer.model

//data class MusicFile(
//    var name: String?="",
//    var desc: String?="",
//    var photo: String?="",
//    var price: Int=0,
//    var quantity: Int=0
//): Serializable {
//    fun addQuantity(){
//        this.quantity = this.quantity+1
//    }
//    fun minQuantity(){
//        if (this.quantity > 0){
//            this.quantity = this.quantity-1
//        }
//    }
//
//    fun getSubtotal() : Int{
//        return this.quantity*price
//    }
//}

data class MusicFile(
    var path: String? = "",
    var title: String? = "",
    var artist: String? = "",
    var album: String? = "",
    var duration: String? = "",
    var id: String? = ""
)