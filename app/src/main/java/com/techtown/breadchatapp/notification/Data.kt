package com.techtown.breadchatapp.notification

class Data(
    var user : String,
    var icon : Int,
    var body : String,
    var title : String,
    var sendted : String
) {
    constructor() : this("", 0, "", "", "")
}