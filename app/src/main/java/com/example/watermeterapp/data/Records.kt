package com.example.watermeterapp.data

data class Records (
    val RecordID:Int,
    val RecordTime:String,
    val UserName: String,
    val RecordPropertyID: Int,
    val UnitBlock: String,
    val UnitFloor: String,
    val UnitName: String,
    val RecordReading: Int,
    val RecordStatus: String
        )