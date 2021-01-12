package com.example.watermeterapp.data

data class Usages (
        val UsageID:Int,
        val UsagePropertyID:Int,
        val PeriodStart:String,
        val PeriodEnd:String,
        val UsageData:Int
        )