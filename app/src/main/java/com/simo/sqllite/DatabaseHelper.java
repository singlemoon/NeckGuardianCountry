package com.simo.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.neckguardian.sign.State;

/**
 * Created by 孤月悬空 on 2016/1/29.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static int VERSION = 6;
    private static final String TAG = "DatabaseHelper";


    public DatabaseHelper(Context context) {
        super(context, State.DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1
                = "CREATE TABLE "
                + State.TABLE_NAME
                + "("
                + State.CURRENT_ENERGY       + " INT            DEFAULT 0,"
                + State.TARGET_ENERGY        + " INT            DEFAULT 30000,"
                + State.DATE_NUM             + " VARCHAR(10)    DEFAULT '2-4',"
                + State.EXERCISE_ANGLE       + " INT            DEFAULT 0,"
                + State.EXERCISE_AMOUNT      + " INT            DEFAULT 0,"
                + State.ENERGY_CONSUMPTION   + " INT            DEFAULT 0,"
                + State.LOWER_TIME           + " INT            DEFAULT 0,"
                + State.LEFT_TIME            + " INT            DEFAULT 0,"
                + State.RIGHT_TIME           + " INT            DEFAULT 0"
                + ")";
        db.execSQL(sql1);

        String sql2
                = "CREATE TABLE "
                + State.TABLE_GAME
                + "("
                + State.SHOW_TIME           + " VARCHAR(10)    DEFAULT '3-7',"
                + State.GAME_TIME           + " VARCHAR(10)    DEFAULT '上午9:09',"
                + State.GAME_NAME           + " VARCHAR(10)    DEFAULT '平衡大师',"
                + State.GAME_USE_TIME       + " INT            DEFAULT 0,"
                + State.COMPLETENESS        + " INT            DEFAULT 0"
                + ")";
        db.execSQL(sql2);

        Log.e(TAG, "成功创建了一个数据库：" + State.DATABASE_NAME);
        Log.e(TAG, "成功创建了一个数据表：" + State.TABLE_NAME);
        Log.e(TAG, "成功创建了一个数据表：" + State.TABLE_GAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(newVersion);
        //删除表
        String sql1 = "DROP TABLE " + State.TABLE_NAME;
        String sql2 = "DROP TABLE " + State.TABLE_GAME;
        db.execSQL(sql1);
        db.execSQL(sql2);
        Log.i(TAG, "成功删除了一个数据表" + State.TABLE_NAME);
        onCreate(db);
    }
}
