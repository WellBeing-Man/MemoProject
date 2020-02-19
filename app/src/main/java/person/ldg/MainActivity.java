package person.ldg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import person.ldg.util.Authority;

public class MainActivity extends AppCompatActivity {

    static final String TAG="MainActivity";

    private Button toNewMemo;
    private RecyclerView memoRecycler;
    private MemoAdapter memoAdapter;
    private  String[] requiredPermissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int reqeustcode=200;
    private static final long serialVersionUID=321L;

    private ArrayList<Memo> memos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Authority authority= new Authority(requiredPermissions,this,this, reqeustcode);
        memos=new ArrayList<Memo>();


        memoRecycler=findViewById(R.id.memoRecycler);
        memoAdapter=new MemoAdapter(memos);
        memoRecycler.setAdapter(memoAdapter);
        memoRecycler.setLayoutManager(new LinearLayoutManager(this));
        memoAdapter.notifyDataSetChanged();


        if(authority.checker()==0){
            authority.excute();
        }else{
            getMemos();
            viewMemos();
        }



            Log.d(TAG, "onCreate: "+ memos.size());




        toNewMemo=findViewById(R.id.newMemo);
        toNewMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(),MemoHandle.class);
                startActivity(intent);
            }
        });
    }


    public void getMemos() {


        String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WellBeing/";
        File file=new File(dirPath);
        String[] filelist=file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith("wbm");
            }
        });

        for(String filename:filelist){
            try {
                 FileInputStream fileInputStream = new FileInputStream(new File(dirPath+filename));
                 ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
                 Memo readedMemo= new Memo();
                 readedMemo=(Memo)objectInputStream.readObject();
                memos.add(readedMemo);
                 readedMemo=null;
            }catch (IOException e){
                Log.e(TAG, "getMemos: "+"IO" );
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    private void viewMemos() {
            /*어댑터에 넣어서 리사이클러 뷰 뿌리기*/
    }
}

