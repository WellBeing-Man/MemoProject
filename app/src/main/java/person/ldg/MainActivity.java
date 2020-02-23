package person.ldg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String TAG="MainActivity";

    private Button toNewMemo;
    private RecyclerView memoRecycler;
    private MemoAdapter memoAdapter;            //뷰 관련 오브젝트 선언

    private  String[] requiredPermissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};  //권한 객체
    private int reqeustcode=200;
    static final int UNAUTHORIZED=0;
    static final long serialVersionUID=321L;

    private ArrayList<Memo> memos;          //각 메모 객체 배열

    @Override
    protected void onResume() {
        super.onResume();
        memos.clear();                  //memos 삭제 후 다시 채우기
        getMemos();
        memoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
      memos.clear();
        getMemos();
        memoAdapter.notifyDataSetChanged();
         }

    @Override
    protected void onRestart() {
        super.onRestart();
        memos.clear();
        getMemos();
        memoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        memos.clear();
        getMemos();
        memoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        memos.clear();
        getMemos();
        memoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memos.clear();
        getMemos();
        memoAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Authority authority= new Authority(requiredPermissions,this,this, reqeustcode);
        memos=new ArrayList<Memo>();    //memos 객체 초기화

        toNewMemo=findViewById(R.id.newMemo);           //버튼

        memoRecycler=findViewById(R.id.memoRecycler);
        memoAdapter=new MemoAdapter(memos,getApplicationContext());         //현 액티비티 컨텍스트 전달
        memoRecycler.setAdapter(memoAdapter);
        memoRecycler.setLayoutManager(new LinearLayoutManager(this));
        memoAdapter.notifyDataSetChanged();                                     //Recycler뷰 관련 선언 및 초기화



        if(authority.checker()==UNAUTHORIZED){                         //checker가 0이면 인증 진행
            authority.excute();
        }else{
            getMemos();                                             //메모 객체 갱신
        }



        toNewMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(),MemoHandle.class);  //새로운 메모 작성 버튼
                startActivity(intent);
            }
        });
    }


    public void getMemos() {                                //메모 파일 객체로 집어 넣기

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

}

