package cn.com.listwebview.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by LiuZhen on 2017/3/16.
 */
public class Adapter extends ArrayAdapter<String> {

    private List<String> list;
    private int resource;
    private Context context;
    private LayoutInflater inflater;

    public Adapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final String val = getItem(position);
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.tv_val = (TextView) convertView.findViewById(R.id.tv_val);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_val.setText(val);
        return convertView;
    }

    static class ViewHolder{
        TextView tv_val;
    }

}
