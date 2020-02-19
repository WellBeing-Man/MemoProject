package person.ldg;

import android.app.ListActivity;
import android.content.Context;
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

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private static final String TAG = "MemoAdapter";
    private ArrayList<Memo> memos;

    public MemoAdapter(ArrayList<Memo> memos) {
        this.memos = memos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.memoImageItem);
            textView=itemView.findViewById(R.id.memoTextItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.record_memo_item,parent,false);
        MemoAdapter.ViewHolder viewHolder=new MemoAdapter.ViewHolder(view);
        return viewHolder;
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap= BitmapFactory.decodeByteArray(memos.get(position).getImageBytes(),0,memos.get(position).getImageBytes().length);
        holder.textView.setText(memos.get(position).getName().substring(0,3));
        holder.imageView.setImageBitmap(bitmap);
        Log.d(TAG, "onBindViewHolder: "+memos.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

}
