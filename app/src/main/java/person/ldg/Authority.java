package person.ldg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//권한 설정을 위한 클래스
public class Authority extends AppCompatActivity {
    static final String TAG = "Authority";


    public static String CONTENT="앱을 사용하기 위해서는 저장소, 카메라, 인터넷 접근 권한이 필요합니다.";

    private String[] requiredPermissions;
    private Context context;
    private Activity activity;
    private int reqeustcode;
    static final int AUTHORIZED=1, UNAUTHORIZED=0;

    public Authority(String[] requiredPermissions, Context context, Activity activity, int reqeustcode) {
        this.requiredPermissions = requiredPermissions;
        this.context = context;
        this.activity = activity;
        this.reqeustcode=reqeustcode;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(String[] requiredPermissions) {          /*Getter Setter*/
        this.requiredPermissions = requiredPermissions;
    }

    public static void setCONTENT(String CONTENT) {
        Authority.CONTENT = CONTENT;
    }

    public void excute(){                                       //권한 설정묻는 다이얼로그 및 설정
        for(int i=0;i<requiredPermissions.length;i++){
            String[] perms={requiredPermissions[i]};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, requiredPermissions[i]) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        requiredPermissions[i])) {
                    ActivityCompat.requestPermissions(activity,perms,reqeustcode);
                } else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle(CONTENT);
                    builder.setMessage("설정 페이지로 이동하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent appSetting= new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                            appSetting.addCategory(Intent.CATEGORY_DEFAULT);
                            appSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(appSetting);
                        }
                    });
                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.create().show();
                    };

                }

            } else {
                Log.d(TAG,requiredPermissions+"권한이 있습니다.");
            }
        }
        }


    public int checker(){                   //권한이 있는지 검사
        int returncode=UNAUTHORIZED;
        for(int i=0;i<requiredPermissions.length;i++) {
            if (ContextCompat.checkSelfPermission(context, requiredPermissions[i]) !=
                    PackageManager.PERMISSION_GRANTED){
                    returncode =UNAUTHORIZED; }
            else{
                returncode=AUTHORIZED;
            }
        }
        return returncode;
    }


}
