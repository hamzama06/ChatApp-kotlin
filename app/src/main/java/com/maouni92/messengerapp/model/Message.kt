package com.maouni92.messengerapp.model

data class Message (var messageId: String ? = null, var message: String ?= null, var senderId:String ?= null, var userId:String ?= null, var friendId:String ?= null,var friendName:String ?= null, var friendImageUrl:String ?= null, var type:String ?= null, var date:String ?= null, var time:Long ?=null)