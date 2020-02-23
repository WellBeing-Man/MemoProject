package person.ldg;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


class Memo implements Serializable{
    private static final String TAG = "MEMO";
    private static long serialVersionUID=321L;

    private String name;
    private String contents;
    private byte[][] imageBytes;
    private int[] offset;                  //바이트디코딩을 위한 오프셋
    private String latestModifieddate;

    public Memo(String name, String contents, byte[][] imageBytes, int[] offset) {
        this.name = name;
        this.contents = contents;
        this.imageBytes = imageBytes;
        this.offset = offset;
    }

    public Memo() {

    }



    /*Getter 와 Setter*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public byte[][] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[][] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public int[] getOffset() {
        return offset;
    }

    public void setOffset(int[] offset) {
        this.offset = offset;
    }

    public String getLatestModifieddate() {
        return latestModifieddate;
    }

    public void storeFile(String fileDir){            //시리얼라이즈 해서 파일로 저장

        SimpleDateFormat nowTime=new SimpleDateFormat("yyyy-MM-DD HH:mm");
        latestModifieddate=nowTime.format(new Date(System.currentTimeMillis()));

        byte[] serializedMemo;
        String dirPath= fileDir;
        Log.d(TAG, "storeFile: "+dirPath);
        File dirFile=new File(dirPath);
        FileOutputStream saveFile=null;
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File a=new File(dirPath+name+".wbm");
        if(a.exists()){
            a.delete();
        }

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            try(ObjectOutputStream outputStream=new ObjectOutputStream(byteArrayOutputStream)) {
                outputStream.writeObject(this);
                serializedMemo=byteArrayOutputStream.toByteArray();
                saveFile=new FileOutputStream(dirPath+name+".wbm");
                saveFile.write(serializedMemo);
                saveFile.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public boolean delete(String filePath){

        String dirPath= filePath+"/WellBeing/";
        File dirFile=new File(dirPath);
        File a=new File(dirPath+name+".wbm");
        if(a.exists()){
            a.delete();
            return true;
        }else
        {
            return false;
        }
        }



}
