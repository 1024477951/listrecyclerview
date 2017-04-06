package cn.com.listwebview.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Adapter adapter = null;
    private List<String> list = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for(int i = 0;i < 20; i++){
            list.add(""+i);
        }
        list();
        RecylerView();
    }

    private void list(){
        listView = (ListView) findViewById(R.id.listview);
        adapter = new Adapter(MainActivity.this,R.layout.item_line,list);
//        View view = LayoutInflater.from(this).inflate(R.layout.item_head,null);
//        WebView webView = (WebView)view.findViewById(R.id.webView);
//
//        webView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP)
//                    listView.requestDisallowInterceptTouchEvent(false);
//                else
//                    listView.requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
//        webView.loadUrl("http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0519/2892.html");
        listView.setAdapter(adapter);
//        listView.addHeaderView(view,null,true);

    }

    private void RecylerView(){
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new RecylerViewAdapter(MainActivity.this,R.layout.item_line,list));
    }


}
