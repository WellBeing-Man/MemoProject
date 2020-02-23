package person.ldg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private static final String TAG = "ImageAdapter";

    private final int scaledWidth=1000;
    private final int scaledHieght=1000;
    private final int originHeight=300;
    private final int originWidth=300;                                      //이미지 터치시 커졌다 작아졌다

    private ArrayList<Bitmap> data=null;
    private int imagePosition;

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageitem);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imagePosition=getAdapterPosition();
                }
            });

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {            //터치해서 누르면 사진 확대, 떼면 축소
                    ViewGroup.LayoutParams params=(ViewGroup.LayoutParams) view.getLayoutParams();
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN :
                            params.height=scaledHieght;
                            params.width=scaledWidth;
                            view.setLayoutParams(params);
                            return true;
                        case MotionEvent.ACTION_UP:
                            params.width=originWidth;
                            params.height=originHeight;
                            view.setLayoutParams(params);
                            imagePosition=getAdapterPosition();
                            return true;
                    }
                    return false;
                }
            });

        }
        }


    public ImageAdapter(ArrayList<Bitmap> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {     //뷰홀더 생성
        Context context=parent.getContext();
        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.record_bitmap_item,parent,false);
        ImageAdapter.ViewHolder viewHolder=new ImageAdapter.ViewHolder(view);

        return viewHolder;
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {        //뷰홀더 바인딩
        Bitmap bitmap=data.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getImagePosition(){
        return this.imagePosition;
    }       //최근에 클릭한 이미지 포지션

    public void setDifaultPostions(){
        this.imagePosition=-1;
    }           //이미지 삭제시 포지션 -1로 초기화
}
