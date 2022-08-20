package com.maouni92.messengerapp.model

data class User(var id:String ?= null,
                var name:String ?= null,
                var email:String ?= null,
                var imageUrl:String ?= null,
                var isAvailable:Boolean ?= true
                )
