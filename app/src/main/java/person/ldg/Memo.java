package person.ldg;


import android.graphics.Bitmap;
import android.os.Environment;


import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


class Memo implements Serializable {
    private String name;
    private String contents;
    private byte[] imageBytes;
    private static final long serialVersionUID=321L;

    public Memo(String name, String contents, byte[] imageBytes) {
        this.name = name;
        this.contents = contents;
        this.imageBytes = imageBytes;
    }

    public Memo() {

    }


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

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }


    public void storeFile(){

        byte[] serializedMemo;
        String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WellBeing/";
        File dirFile=new File(dirPath);
        FileOutputStream saveFile=null;
        if(!dirFile.exists()){
            dirFile.mkdir();
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



}
