package com.example.watermeterapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {
    @Insert
    fun insertRecord(vararg record:RecordDB)

    @Query("SELECT * FROM RecordDB")
    fun getAll(): List<RecordDB>

    @Query("SELECT * FROM RecordDB WHERE RecordPropertyID = :propertyID")
    fun getSpecific(propertyID:Int): List<RecordDB>

    @Query("DELETE FROM RecordDB")
    fun deleteAll()

    @Query("DELETE FROM RecordDB WHERE RecordID = :recordID")
    fun delete(recordID:Int)
}