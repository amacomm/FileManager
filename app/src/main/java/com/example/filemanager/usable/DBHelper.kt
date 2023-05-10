package com.example.filemanager.usable

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = "FILESDB"
        private val DATABASE_VERSION = 1
        val MAIN_TABLE_NAME = "files"
        val OLD_TABLE_NAME = "oldfiles"
        val ID_COL = "_id"
        val FILE_COl = "file"
        val HASH_COL = "hash"
    }
    override fun onCreate(db: SQLiteDatabase) {
        var query = ("CREATE TABLE " + MAIN_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                FILE_COl + " TEXT," +
                HASH_COL + " TEXT" + ")")
        db.execSQL(query)
        query = ("CREATE TABLE " + OLD_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                FILE_COl + " TEXT," +
                HASH_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + MAIN_TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + OLD_TABLE_NAME)
        onCreate(db)
    }
}