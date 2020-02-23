package person.ldg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MemoHandle extends AppCompatActivity {

    private static final String TAG = "MemoHandle";
    private static final int GALLERYREQUESTCODE = 1;
    private static  final int CAMERAREQUESTCODE = 3;

    private EditText titleText;                 //뷰 객체들
    private EditText contentsText;
    private Button editCompelete;
    private Button gallery;
    private Button camera;
    private Button delete;
    private Button url;
    private RecyclerView imageRecyler;
    private ImageAdapter imageAdapter;


    private ArrayList<Bitmap> images;               //저장에 사용되는 데이터 객체들
    private ArrayList<Integer> offsetArray;
    private Memo memo;
    private String contentsTextWord;
    private String titleTextWord;

    private Authority authorityCam, authorityStorage,authorityInternet;         //인증객체들
    private String[] cameraPermissions={Manifest.permission.CAMERA};
    private String[] storagePermissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] internetPermssions={Manifest.permission.INTERNET};
    static final int requestCode=200;


    final private int dstWidth=300;                 //이미지 크기
    final private int dstHeight=300;
    private boolean isCorrection=true;


    @Override
    protected void onStart() {
        super.onStart();

        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_handle);

        titleText=findViewById(R.id.titleText);                     //뷰 객체들 초기화
        contentsText=findViewById(R.id.contentText);
        editCompelete=findViewById(R.id.editComplete);
        gallery=findViewById(R.id.gallery);
        camera=findViewById(R.id.camera);
        delete=findViewById(R.id.delete);
        url=findViewById(R.id.urlButton);

        images=new ArrayList<Bitmap>();                 //데이터 객체들 초기화
        offsetArray=new ArrayList<Integer>();


        Intent getFromdetail=getIntent();                   //메모 수정하러 들어왔을 시
        try {                                               //제목 수정 비활성
            setFromIntent(getFromdetail);
            decodeByte();       //images객체에 Byte코드 디코드
            contentsTextWord=memo.getContents();
            titleText.setText(titleTextWord);
            titleText.setInputType(InputType.TYPE_NULL);
            contentsText.setText(contentsTextWord);
            editCompelete.setText("수정완료");
            isCorrection=false;
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        imageRecyler=findViewById(R.id.image_recycler);                 //리사이클러 뷰 관련 초기화
        imageAdapter=new ImageAdapter(images);
        imageRecyler.setAdapter(imageAdapter);
        LinearLayoutManager horizonlinearLayoutManager = new LinearLayoutManager(this);     //이미지 수평방향으로 추가
        horizonlinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        imageRecyler.setLayoutManager(horizonlinearLayoutManager);
        imageAdapter.setDifaultPostions();



            delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position=imageAdapter.getImagePosition();               //클릭으로 선택된 이미지를 삭제
                if (images.isEmpty() || position==-1){
                    Toast.makeText(getApplicationContext(),"삭제할 이미지를 선택해 주세요.",Toast.LENGTH_SHORT).show();
            } else{
                    images.remove(position);
                    imageAdapter.notifyDataSetChanged();
                    imageAdapter.setDifaultPostions();
                }
            }
        });


        authorityStorage= new Authority(storagePermissions,this,this,requestCode);      //갤러리 접근시 저장소 관련 퍼미션
        if(authorityStorage.checker()==Authority.UNAUTHORIZED){
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authorityStorage.excute();

                }
            });
        } else {
        gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERYREQUESTCODE);
                }
            }
        });}


        authorityCam= new Authority(cameraPermissions,this,this,requestCode);           //카메라 권한 없으면 버튼 클릭시 다이얼로그 생성
        if(authorityCam.checker()==Authority.UNAUTHORIZED){
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authorityCam.excute();

                }
            });
        }
        else{
             camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, 3);
                        }
                    }
                }
            });
        }

        authorityInternet=new Authority(internetPermssions,getApplicationContext(),this,requestCode);       //인터넷 권한 없을시 다이얼로그 생성
        if(authorityInternet.checker()==Authority.UNAUTHORIZED){
            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authorityInternet.excute();

                }
            });

        }else {

            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MemoHandle.this);           //다이얼 로그로 입력 받아오기
                    builder.setTitle("Url 입력");
                    builder.setMessage("이미지를 가져올 url을 입력하세요");

                    final EditText input = new EditText(MemoHandle.this);
                    builder.setView(input);

                    builder.setPositiveButton("가져오기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {

                            Thread connection=new Thread(){                                 //쓰레드 오버라이드
                                @Override
                                public void run() {
                                    Bitmap bitmap;
                                    try {
                                        URL url=new URL(input.getText().toString());
                                        HttpURLConnection con=(HttpURLConnection) url.openConnection();
                                        con.setDoInput(true);
                                        con.connect();

                                        InputStream inputStream=con.getInputStream();
                                        bitmap=BitmapFactory.decodeStream(inputStream);
                                        bitmap=Bitmap.createScaledBitmap(bitmap,dstWidth,dstHeight,true);
                                        images.add(bitmap);
                                        inputStream.close();
                                    }catch (MalformedURLException e){
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(),"잘못된 URL입니다.",Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "run: "+"No URL" );

                                    } catch (IOException e) {
                                        Toast.makeText(getApplicationContext(),"인터넷 연결에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "run: "+"IOConnection Failed");
                                        e.printStackTrace();
                                    }
                                }
                            };
                            connection.start();
                            try {
                                connection.join();
                                imageAdapter.notifyDataSetChanged();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    builder.setNegativeButton("그만두기", null);

                    builder.create().show();
                }
                }
            });
        }



        editCompelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                titleTextWord=titleText.getText().toString();
                contentsTextWord=contentsText.getText().toString();

                String dirPath= getFilesDir().getAbsolutePath()+"/WellBeing/";
                File file=new File(dirPath);                                            //디렉토리 검사
                if(!file.exists()){
                    file.mkdir();
                }
                String[] filelist=file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        return s.equals(titleText.getText().toString()+".wbm");
                    }
                });                                                                 //파일 목록 받아와서 제목 검사

                try{
                    if(titleText.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }else if(filelist.length==1 && isCorrection){         //인텐트로 전달 받은게 있으면 같은이름 파일 검사 안함
                        Toast.makeText(getApplicationContext(),"같은 이름의 파일이 존재합니다. 다른 이름으로 지어주세요.",Toast.LENGTH_SHORT).show();
                    }else {

                        int byteArraySize=(images.size()/3)+1;
                        byte[][] imageByte=new byte[byteArraySize][];
                        imageByte=compressor(byteArraySize);

                        int[] array = new int[offsetArray.size()];      //ArrayList객체를 int[]로 바꿔줌

                        for (int i = 0; i < offsetArray.size(); i++) {
                            array[i] = offsetArray.get(i).intValue();
                        }

                        memo = new Memo(titleTextWord, contentsTextWord,imageByte, array);      //데이터로 새로운 memo객체 생성 및 수정된 내용 저장
                        memo.storeFile(dirPath);
                        finish();
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });



    }

    private void setFromIntent(Intent getFromdetail) {                          //DetailActivity로부터 인텐트 전달 받아 전역 변수에 넣어줌
        titleTextWord=getFromdetail.getStringExtra("MemoName");
        getMemo(titleTextWord);


    }


    private byte[][] compressor(int byteArraySize) {                //2차원 배열 비트맵을 PNG로 인코딩... 동시에 오프셋 데이터도 저장
       byte[][] buffer=new byte[byteArraySize][];

        for(int i=0;i<byteArraySize;i++) {
            ByteArrayOutputStream imageStream=new ByteArrayOutputStream();
            Log.d(TAG, "compressor: i:"+i);
            for(int j=i*3;j<images.size();j++){
                images.get(j).compress(Bitmap.CompressFormat.PNG,100,imageStream);
                Log.d(TAG, "compressor: j:"+j);
                offsetArray.add((imageStream.toByteArray().length));
                if(j==(i*3+2)){
                    break;
                }
            }
            buffer[i]=imageStream.toByteArray();
        }
        return buffer;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {           //갤러리와 카메라로 받은 이미지 파일 images배열에 넣기
        if(requestCode==GALLERYREQUESTCODE && resultCode==RESULT_OK){
                try{
                    InputStream inputStream=getContentResolver().openInputStream(data.getData());
                    Bitmap img = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream)
                            ,dstWidth,dstHeight,true);//사진이 너무 크기 때문에 크기 조절
                    images.add(img);
                    imageAdapter.notifyDataSetChanged();
                    imageAdapter.setDifaultPostions();
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
        else if(requestCode==CAMERAREQUESTCODE && resultCode==RESULT_OK){
            Bundle extra=data.getExtras();
            Bitmap bitmap=Bitmap.createScaledBitmap((Bitmap) extra.get("data")
                    ,dstWidth,dstHeight,true);
            images.add(bitmap);
            imageAdapter.notifyDataSetChanged();
            imageAdapter.setDifaultPostions();
        }

    }

    private void decodeByte(){                                      //인텐트로 전달받은 byte배열을 bitmap이미지로 바꿈.. offset필요

        byte[][] bytesDecode=memo.getImageBytes();
        int[] offsets=memo.getOffset();
        int offset=0;
        Bitmap bitmap;
        Log.d(TAG, "decodeByte: offset:"+offsets.length);
        for(int i=0;i<bytesDecode.length;i++){
            for(int j=i*3;j<offsets.length;j++){
                Log.d(TAG, "decodeByte: i:"+i+" j: "+j+" offset : "+offsets[j]);
                Log.d(TAG, "decodeByte: "+bytesDecode[i].length);
                bitmap=BitmapFactory.decodeByteArray(bytesDecode[i],offset,offsets[j]-offset);
                offset=offsets[j];
                images.add(bitmap);
                if(j==(i*3+2)){
                    break;
                }
            }
            offset=0;
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
