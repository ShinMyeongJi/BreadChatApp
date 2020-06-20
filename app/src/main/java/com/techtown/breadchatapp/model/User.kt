package com.techtown.breadchatapp.model

class User(
    var id : String,
    var username : String,
    var imageURL : String,
    var status : String
) {
    constructor() : this("", "", "", "")
}