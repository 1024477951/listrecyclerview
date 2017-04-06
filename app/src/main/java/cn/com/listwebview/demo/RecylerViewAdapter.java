package cn.com.listwebview.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LiuZhen on 2017/4/6.
 */
public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private List<String> list;
    private LayoutInflater inflate;

    public RecylerViewAdapter(Context context,int resource,List<String> list){
        this.context = context;
        this.resource = resource;
        this.list = list;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public RecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(resource, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecylerViewAdapter.ViewHolder holder, int position) {
        holder.tv.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv;

        public ViewHolder(View view)
        {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_val);
        }
    }

}
