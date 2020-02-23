package person.ldg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*Memo 배열을 받아주는 어댑터*/
public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private static final String TAG = "MemoAdapter";

    private ArrayList<Memo> memos;

    private Context parentConext;               //생성자로 받아오기
    private Intent toDetail;

    public MemoAdapter(ArrayList<Memo> memos, Context parentConext) {
        this.parentConext=parentConext;
        this.memos = memos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView preImageView;
        TextView preTitleView;
        TextView preContentView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            preImageView=itemView.findViewById(R.id.memoImageItem);
            preTitleView=itemView.findViewById(R.id.memoTitleItem);
            preContentView=itemView.findViewById(R.id.memoContentsItem);

            itemView.setOnClickListener(new View.OnClickListener() {            //아이템 클릭시 상세보기로 넘어가기
                @Override
                public void onClick(View view) {                                //해당 아이템 클릭시 상세보기 액티비티로 인텐트 전달하면서 넘어감
                        Memo memotoSend=memos.get(getAdapterPosition());

                        toDetail=new Intent(parentConext,DetailActivity.class);     //Serialize되어있어서 Parcelable이 안됨... 수동으로 보내기
                        toDetail.putExtra("MemoName",memotoSend.getName());
                        toDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        parentConext.startActivity(toDetail);
                }
            });
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {         //뷰홀더 생성
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.record_memo_item,parent,false);
        MemoAdapter.ViewHolder viewHolder=new MemoAdapter.ViewHolder(view);
        return viewHolder;
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {        //뷰 홀더에 데이터 바인딩

        Memo bindMemo=memos.get(position);
        try {
            holder.preTitleView.setText(bindMemo.getName());
            holder.preContentView.setText(bindMemo.getContents());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

                                      //메모에 이미지가 없으면 이미지뷰 없기
          try {
              Bitmap bitmap = BitmapFactory.decodeByteArray(bindMemo.getImageBytes()[0], 0, bindMemo.getOffset()[0]);
              holder.preImageView.setImageBitmap(bitmap);

          }catch (IndexOutOfBoundsException e){
              e.printStackTrace();
              holder.preImageView.setVisibility(View.INVISIBLE);
          }


    }

    @Override
    public int getItemCount() {
        return memos.size();
    }


}
