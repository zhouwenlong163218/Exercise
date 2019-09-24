package com.example.exercise.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.exercise.Adapter.MyCursorAdapter;
import com.example.exercise.R;
import com.example.exercise.entity.Student;
import com.example.exercise.Adapter.StudentAdapter;
import com.example.exercise.Dao.StudentDaoImpl;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ADD=108;
    private static final int UPDATE=109;
//    private StudentAdapter studentAdapter;
    private MyCursorAdapter studentAdapter;
    private StudentDaoImpl studentDaoImpl;
    private Student selectedStudent;
    private int studentPos;
    private ListView listView;
    private List<Student> students;
    private Button btnADD,btnUpdate,btnDelete,btnAddp;
    private List <String> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_studentlist);
        btnADD = findViewById(R.id.btn_add);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnAddp=findViewById(R.id.btn_addp);

        btnADD.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnAddp.setOnClickListener(this);
        initData();

        studentAdapter = new MyCursorAdapter(this,studentDaoImpl.select());
        listView.setAdapter(studentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                studentAdapter.setSelectItem(i);
                studentPos = i;

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                selectedStudent = new Student();
                selectedStudent.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                selectedStudent.setName(cursor.getString(cursor.getColumnIndex("student_name")));
                selectedStudent.setClassmage(cursor.getString(cursor.getColumnIndex("student_classmate")));
                selectedStudent.setAge(cursor.getInt(cursor.getColumnIndex("student_age")));
//                selectedStudent = (Student) adapterView.getItemAtPosition(i);
            }
        });
    }

    private void initData() {

        studentDaoImpl = new StudentDaoImpl(this);
        students=studentDaoImpl.selectAllStudent();

        if (students == null){
            students = new ArrayList<>();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivityForResult(intent,ADD);
                break;
            case R.id.btn_update:
                if (selectedStudent != null){
                    Intent intent1 = new Intent(MainActivity.this,Main3Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("updata2",selectedStudent);
                    intent1.putExtras(bundle);
                    startActivityForResult(intent1,UPDATE);
                }
                break;
            case R.id.btn_delete:
                if (selectedStudent != null){
                    studentDaoImpl.delete(selectedStudent.getName());
//                    students.remove(studentPos);
                    studentAdapter.changeCursor( studentDaoImpl.select());
                    studentAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_addp:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,},1);
                }else{
                    readContacts();//申请权限
                                    //读取联系人
                }
                break;
        }
    }
    //读取联系人的cursor，组装联系人放入listview中
    private void readContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        contacts=new ArrayList<>();
        if (cursor !=null&& cursor.moveToFirst()){
            do{
                String name =cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone =cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(name+": "+phone);
            }while (cursor.moveToNext());
            cursor.close();
        }
        //2.设置Adapter
        if(contacts.isEmpty()){
            Toast.makeText(MainActivity.this,"木有联系人",Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,contacts);
        listView.setAdapter(arrayAdapter);
        //3.设置ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode==1&& grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            return;
        }

//        if (data != null) {
//            Bundle bundle = data.getExtras();
//            if (bundle == null) {
//                return;
//            }
//            selectedStudent = (Student) bundle.get("22");
//            if (requestCode == UPDATE) {
//                students.set(studentPos, selectedStudent);
//            } else if (requestCode == ADD) {
//                students.add(selectedStudent);
//            }
//            studentAdapter.notifyDataSetChanged();
//        }
        studentAdapter.changeCursor(studentDaoImpl.select());

    }
}
