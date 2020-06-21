package com.techtown.breadchatapp.model

class Chat(
    var sender: String,
    var receiver: String,
    var message: String,
    var isSeen: Unit
) {
    constructor() : this("", "", "")



}