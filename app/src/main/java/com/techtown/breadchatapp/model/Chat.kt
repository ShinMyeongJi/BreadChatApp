package com.techtown.breadchatapp.model

class Chat(
    var sender : String,
    var receiver : String,
    var message : String
) {
    constructor() : this("", "", "")
}