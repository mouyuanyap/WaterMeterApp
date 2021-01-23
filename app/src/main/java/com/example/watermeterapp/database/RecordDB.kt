package com.example.watermeterapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecordDB (
    @PrimaryKey(autoGenerate = true)val RecordID:Int,
    @ColumnInfo val RecordPropertyID:Int,
    @ColumnInfo val RecordPropertyName:String,
    @ColumnInfo val RecordReading:Int


        )