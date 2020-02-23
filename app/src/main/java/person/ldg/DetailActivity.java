package person.ldg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private TextView titleText;
    private TextView contentText;
    private Button correctionButton;
    private Button deleteButton;

    private Memo memo;
    private ArrayList<Bitmap> images;

    private RecyclerView imageRecyler;
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent receiveIntent=getIntent();
        String Name=receiveIntent.getStringExtra("MemoName");
        getMemo(Name);



        titleText=findViewById(R.id.detail_ttitle_text);
        contentText=findViewById(R.id.content_on_detail);
        titleText.setText(memo.getName());
        contentText.setText(memo.getContents());
        correctionButton=findViewById(R.id.correctionButton);
        deleteButton=findViewById(R.id.memoDelete);


        images=new ArrayList<Bitmap>();
         decodeByte();

        imageRecyler=findViewById(R.id.recycler_on_detail);
        imageAdapter=new ImageAdapter(images);
        imageRecyler.setAdapter(imageAdapter);
        LinearLayoutManager horizonlinearLayoutManager = new LinearLayoutManager(this);
        horizonlinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        imageRecyler.setLayoutManager(horizonlinearLayoutManager);


        correctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent correctionIntent=new Intent(getApplicationContext(),MemoHandle.class);
                correctionIntent.putExtra("MemoName",memo.getName());

                correctionIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(correctionIntent);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(memo.delete(getFilesDir().getAbsolutePath())){
                    Toast.makeText(getApplicationContext(),"삭제 성공",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"삭제 실패",Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

    private void decodeByte() {
        byte[][] bytesDecode = memo.getImageBytes();
        int[] offsets = memo.getOffset();
        int offset = 0;
        Bitmap bitmap;
        Log.d(TAG, "decodeByte: HERE");
        try {
        for (int i = 0; i < bytesDecode.length; i++) {
            for (int j = i * 3; j < offsets.length; j++) {
                bitmap = BitmapFactory.decodeByteArray(bytesDecode[i], offset, offsets[j] - offset);

                offset = offsets[j];
                images.add(bitmap);
                if (j == (i * 3 + 2)) {
                    break;
                }
            }
            offset = 0;
        }
    }catch (SecurityException e){
            e.printStackTrace();
        }
        }

    public void getMemo(String fileName) {                                //메모 파일 객체로 집어 넣기

        String dirPath= getFilesDir().getAbsolutePath()+"/WellBeing/";

            try {
                FileInputStream fileInputStream = new FileInputStream(new File(dirPath+fileName+".wbm"));
                ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
                Memo readedMemo= new Memo();
                readedMemo=(Memo)objectInputStream.readObject();
                memo=readedMemo;
                readedMemo=null;

            }catch (IOException e){
                Log.e(TAG, "getMemos: "+"IO" );
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

