package com.example.exercise.entity;
import java.io.Serializable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//实体类，与数据库表字段一一对应
@DatabaseTable(tableName = "tb_Student")
public class OrmStudent implements Serializable {
    @DatabaseField(generatedId =true)
    private int _id;
    @DatabaseField(index =true,columnName = "name",dataType = DataType.STRING)
    private String Name;
    @DatabaseField
    private String Classmate;
    @DatabaseField(columnName = "age",dataType = DataType.INTEGER,canBeNull = true)
    private int Age;
    @DatabaseField(columnName = "school_id",foreign = true,foreignAutoRefresh = true)
    private OrmStudent ormStudent  ;
}
