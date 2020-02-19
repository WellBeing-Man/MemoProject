package person.ldg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import person.ldg.util.Authority;

public class MemoHandle extends AppCompatActivity {

    private static final String TAG = "MemoHandle";
    private EditText titleText;
    private EditText contentsText;
    private Button editCompelete;
    private Button gallery;
    private Button camera;
    private Button delete;

    private ArrayList<Bitmap> images;
    private Memo memo;

    private RecyclerView imageRecyler;
    private ImageAdapter imageAdapter;
    private String[] requiredPermissions={Manifest.permission.CAMERA};
    static final int requestCode=200;

    private Authority authority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_handle);

        titleText=findViewById(R.id.titleText);
        contentsText=findViewById(R.id.contentText);
        editCompelete=findViewById(R.id.editComplete);
        gallery=findViewById(R.id.gallery);
        camera=findViewById(R.id.camera);
        delete=findViewById(R.id.delete);



        authority= new Authority(requiredPermissions,this,this,requestCode);

        if(authority.checker()==0){
            authority.excute();
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    authority.excute();
                }
            });
        }else{
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(cameraIntent.resolveActivity(getPackageManager())!=null){
                        startActivityForResult(cameraIntent,3);
                    }
                }
            });
        }



        images=new ArrayList<Bitmap>();
        imageRecyler=findViewById(R.id.image_recycler);
        imageAdapter=new ImageAdapter(images);
        imageRecyler.setAdapter(imageAdapter);
        LinearLayoutManager horizonlinearLayoutManager = new LinearLayoutManager(this);
        horizonlinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        imageRecyler.setLayoutManager(horizonlinearLayoutManager);




        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);

            }
        });


        editCompelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memo=new Memo(titleText.getText().toString(), contentsText.getText().toString(),compressor());
                memo.storeFile();
                finish();

            }
        });
    }

    private byte[] compressor() {
        ByteArrayOutputStream tempBuffer=new ByteArrayOutputStream();
        for(int i=0;i<images.size();i++){
                images.get(i).compress(Bitmap.CompressFormat.PNG,50,tempBuffer);
        }

        return tempBuffer.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                try{
                    InputStream inputStream=getContentResolver().openInputStream(data.getData());
                    Bitmap img = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream)
                            ,250,300,true);
                    images.add(img);
                    imageAdapter.notifyDataSetChanged();
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode==3 && resultCode==RESULT_OK){
            Bundle extra=data.getExtras();
            Bitmap bitmap=Bitmap.createScaledBitmap((Bitmap) extra.get("data")
                    ,250,300,true);

            images.add(bitmap);
            imageAdapter.notifyDataSetChanged();
        }
    }

public void deleteImage(int i){
            if(images.get(i)!=null) {
                imageAdapter.notifyDataSetChanged();
            }else{
                Log.e(TAG,"Image index "+i+" Empty");
            }
}


}
