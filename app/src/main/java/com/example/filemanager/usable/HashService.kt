package com.example.filemanager.usable

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.os.IBinder
import java.io.File
import java.security.MessageDigest

class HashService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    lateinit var db: DBHelper
    override fun onCreate() {
        super.onCreate()
        db = DBHelper(this)

        val broadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(p0: Context, p1: Intent) {
                val path = p1.extras!!.get("path")
                val hash = p1.extras!!.get("hash")
                val database = db.readableDatabase
                val c = database.rawQuery("SELECT "+ DBHelper.HASH_COL + " FROM " + DBHelper.OLD_TABLE_NAME + " WHERE " + DBHelper.FILE_COl + " = "+ path, null)
                if(c.moveToFirst()){
                    val hashold = c.getString(c.columnNames.lastIndex)
                    if (hash == hashold)
                    {
                        var intent = Intent("answer")
                            .putExtra("res", true)
                        sendBroadcast(intent)
                        return
                    }
                }
                var intent = Intent("answer")
                    .putExtra("res", false)
                sendBroadcast(intent)
                return
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("check_update"))
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        progress(File(intent.getStringExtra("root")))
        return super.onStartCommand(intent, flags, startId)
    }
    fun progress(root: File){
        val database = db.writableDatabase
        var content: ContentValues

        fun CheckIn(list: Array<File>){
            list.forEach {
                if(it.isDirectory)
                    CheckIn(it.listFiles())
                else{
                    content = ContentValues()
                    content.put(DBHelper.FILE_COl, it.absolutePath)
                    content.put(DBHelper.HASH_COL, HashUtils.getCheckSumFromFile(
                        MessageDigest.getInstance(MessageDigestAlgorithm.SHA_256),
                        it
                    ))
                    database.insertWithOnConflict(DBHelper.MAIN_TABLE_NAME, null, content, SQLiteDatabase.CONFLICT_IGNORE)
                }
            }
        }
        CheckIn(root.listFiles())
    }

    override fun onDestroy() {
        val database = db.writableDatabase
        database.execSQL("delete from "+ DBHelper.OLD_TABLE_NAME);
        database.execSQL("INSERT INTO "+ DBHelper.OLD_TABLE_NAME + "SELECT * FROM "+ DBHelper.MAIN_TABLE_NAME)
        database.execSQL("delete from "+ DBHelper.MAIN_TABLE_NAME);
        super.onDestroy()
    }
}