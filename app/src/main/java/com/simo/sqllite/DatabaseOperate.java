package com.simo.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.model.DataOfDay;
import com.model.GameRecord;
import com.neckguardian.sign.State;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的操作类
 * Created by 孤月悬空 on 2016/1/29.
 */
public class DatabaseOperate {

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    private static final String TAG = "DatabaseOperate";

    public DatabaseOperate(Context context) {
        helper = new DatabaseHelper(context);
    }

    /**
     * 查找最后一天的数据
     * @return  最后一天的数据的封装类
     */
    public DataOfDay queryLastDay() {
        db = helper.getReadableDatabase();

        DataOfDay dataOfDay = new DataOfDay();

        String columns[] = new String[]{
                State.CURRENT_ENERGY,
                State.TARGET_ENERGY,
                State.DATE_NUM,
                State.EXERCISE_ANGLE,
                State.EXERCISE_AMOUNT,
                State.ENERGY_CONSUMPTION,
                State.LOWER_TIME,
                State.LEFT_TIME,
                State.RIGHT_TIME,
        };

        Cursor cursor = db.query(State.TABLE_NAME, columns, null, null, null, null, null);

        cursor.moveToLast();
        dataOfDay.setCurrentEnergy(cursor.getInt(0));
        dataOfDay.setTargetEnergy(cursor.getInt(1));
        dataOfDay.setDateNum(cursor.getString(2));
        dataOfDay.setExerciseAngle(cursor.getInt(3));
        dataOfDay.setExerciseAmount(cursor.getInt(4));
        dataOfDay.setEnergyConsumption(cursor.getInt(5));
        dataOfDay.setLowerTime(cursor.getInt(6));
        dataOfDay.setLeftTime(cursor.getInt(7));
        dataOfDay.setRightTime(cursor.getInt(8));

        db.close();
        cursor.close();

        Log.i(TAG, "最后一天的数据是：" + dataOfDay.toString());

        return dataOfDay;
    }

    /**
     * 查找所有历史记录的方法
     *
     * @return 所有的历史记录
     */
    public List<DataOfDay> queryHistory() {
        db = helper.getReadableDatabase();

        List<DataOfDay> dataOfDays = new ArrayList<>();

        String columns[] = new String[]{
                State.CURRENT_ENERGY,
                State.TARGET_ENERGY,
                State.DATE_NUM,
                State.EXERCISE_ANGLE,
                State.EXERCISE_AMOUNT,
                State.ENERGY_CONSUMPTION,
                State.LOWER_TIME,
                State.LEFT_TIME,
                State.RIGHT_TIME,
        };

        Cursor cursor = db.query(State.TABLE_NAME, columns, null, null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DataOfDay dataOfDay = new DataOfDay();
            dataOfDay.setCurrentEnergy(cursor.getInt(0));
            dataOfDay.setTargetEnergy(cursor.getInt(1));
            dataOfDay.setDateNum(cursor.getString(2));
            dataOfDay.setExerciseAngle(cursor.getInt(3));
            dataOfDay.setExerciseAmount(cursor.getInt(4));
            dataOfDay.setEnergyConsumption(cursor.getInt(5));
            dataOfDay.setLowerTime(cursor.getInt(6));
            dataOfDay.setLeftTime(cursor.getInt(7));
            dataOfDay.setRightTime(cursor.getInt(8));
            dataOfDays.add(dataOfDay);
        }

        db.close();
        cursor.close();

        Log.i(TAG, "一共查询到了" + dataOfDays.size() + "个数据");

        return dataOfDays;
    }

    /**
     * 查找所有游戏记录的方法
     * @return  所有的游戏记录的list
     */
    public List<GameRecord> queryRecord() {
        db = helper.getReadableDatabase();

        List<GameRecord> gameRecords = new ArrayList<>();

        String columns[] = new String[]{
                State.SHOW_TIME,
                State.GAME_TIME,
                State.GAME_NAME,
                State.GAME_USE_TIME,
                State.COMPLETENESS
        };

        Cursor cursor = db.query(State.TABLE_GAME, columns, null, null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            GameRecord record = new GameRecord();
            record.setShow_time(cursor.getString(0));
            record.setGame_time(cursor.getString(1));
            record.setGame_name(cursor.getString(2));
            record.setGame_use_time(cursor.getInt(3));
            record.setCompleteness(cursor.getInt(4));
            gameRecords.add(record);
        }

        db.close();
        cursor.close();

        Log.e(TAG, "一共查询到了" + gameRecords.size() + "个数据");

        return gameRecords;
    }

    /**
     * 插入一条数据
     *
     * @param dataOfDay //需要存入数据库的数据
     */
    public void insertDataOfDay(DataOfDay dataOfDay) {
        db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(State.CURRENT_ENERGY, dataOfDay.getCurrentEnergy());
        values.put(State.TARGET_ENERGY, dataOfDay.getTargetEnergy());
        values.put(State.DATE_NUM, dataOfDay.getDateNum());
        values.put(State.EXERCISE_ANGLE, dataOfDay.getExerciseAngle());
        values.put(State.EXERCISE_AMOUNT, dataOfDay.getExerciseAmount());
        values.put(State.ENERGY_CONSUMPTION, dataOfDay.getEnergyConsumption());
        values.put(State.LOWER_TIME, dataOfDay.getLowerTime());
        values.put(State.LEFT_TIME, dataOfDay.getLeftTime());
        values.put(State.RIGHT_TIME, dataOfDay.getRightTime());

        db.insert(State.TABLE_NAME, null, values);
        db.close();

        Log.i(TAG, "成功插入了今天的数据");
    }

    /**
     * 插入一条数据
     *
     * @param gameRecord //需要存入数据库的数据
     */
    public void insertGameRecord(GameRecord gameRecord) {
        db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(State.SHOW_TIME, gameRecord.getShow_time());
        values.put(State.GAME_TIME, gameRecord.getGame_time());
        values.put(State.GAME_NAME, gameRecord.getGame_name());
        values.put(State.GAME_USE_TIME, gameRecord.getGame_use_time());
        values.put(State.COMPLETENESS, gameRecord.getCompleteness());

        db.insert(State.TABLE_GAME, null, values);
        db.close();

        Log.e(TAG, "成功插入了本次游戏数据");
    }

    /**
     * 更新一条数据
     *
     * @param dataOfDay 需要更新的数据
     */
    public void updateDataOfDay(DataOfDay dataOfDay) {
        db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(State.CURRENT_ENERGY, dataOfDay.getCurrentEnergy());
        values.put(State.TARGET_ENERGY, dataOfDay.getTargetEnergy());
        values.put(State.DATE_NUM, dataOfDay.getDateNum());
        values.put(State.EXERCISE_ANGLE, dataOfDay.getExerciseAngle());
        values.put(State.EXERCISE_AMOUNT, dataOfDay.getExerciseAmount());
        values.put(State.ENERGY_CONSUMPTION, dataOfDay.getEnergyConsumption());
        values.put(State.LOWER_TIME, dataOfDay.getLowerTime());
        values.put(State.LEFT_TIME, dataOfDay.getLeftTime());
        values.put(State.RIGHT_TIME, dataOfDay.getRightTime());

        String whereClause = State.DATE_NUM + " = ? ";
        String whereArgs[] = new String[]{dataOfDay.getDateNum()};

        db.update(State.TABLE_NAME, values, whereClause, whereArgs);
        db.close();

        Log.i(TAG, "成功更新了一条数据");
    }

    /**
     * 删除数据库中所有的数据，但是数据库和表还在
     */
    public void deleteAllData() {
        db = helper.getReadableDatabase();

        db.delete(State.TABLE_NAME, null, null);
        db.close();

        Log.i(TAG, "已经删除数据库中所有的数据");
    }

    /**
     * 删除数据库中所有的数据，但是数据库和表还在
     */
    public void deleteAllRecord() {
        db = helper.getReadableDatabase();

        db.delete(State.TABLE_GAME, null, null);
        db.close();

        Log.i(TAG, "已经删除数据库中所有的数据");
    }
}
