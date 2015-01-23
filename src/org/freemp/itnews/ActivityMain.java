package org.freemp.itnews;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;
import org.xml.sax.SAXException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityMain extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private AQuery aq;
    private Activity activity;
    private RecyclerView gridView;
    private StaggeredGridLayoutManager mLayoutManager;
    private AdapterMain adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ClassItem> items = new ArrayList<ClassItem>();

    private final String[] FEEDS = new String[]{"http://roem.ru/rss/","http://siliconrus.com/feed/","http://habrahabr.ru/rss/","http://megamozg.ru/rss/","http://geektimes.ru/rss/"};
    private DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zz", Locale.ENGLISH);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        aq = new AQuery(activity);
        AQUtility.setDebug(true);

        swipeRefreshLayout = new SwipeRefreshLayout(activity);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        gridView = new RecyclerView(activity);
        gridView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        gridView.setLayoutManager(mLayoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout.addView(gridView);
        getWindow().setContentView(swipeRefreshLayout);

        adapter = new AdapterMain(activity,items);
        gridView.setAdapter(adapter);

        getFeeds();
    }

    public void getFeeds() {
        items.clear();
        for(String feed:FEEDS){
            request(feed);
        }
    }

    public void request(String url) {
        aq.ajax(url, XmlDom.class,this,"onRequest");
        swipeRefreshLayout.setRefreshing(true);
    }

    public void onRequest(String url,XmlDom xml, AjaxStatus status) {
        if (status.getCode()==200) {
            String logo = "";
            try {
                logo = xml.tags("url").get(0).text();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            List<XmlDom> xmlItems = xml.tags("item");

            for(XmlDom xmlItem: xmlItems){
                ClassItem item = new ClassItem();
                String description = xmlItem.tag("description").text();
                item.setLogo(logo);
                try {
                    item.setAuthor(xmlItem.tag("author").text());
                }
                catch (Exception e) {}
                item.setTitle(xmlItem.tag("title").text());
                item.setDescription(description);
                item.setLink(xmlItem.tag("link").text());
                String pubDate = xmlItem.tag("pubDate").text();
                Date date = new Date();
                try {
                    date = formatter.parse(pubDate);
                }
                catch (Exception e) {
                    AQUtility.debug("errorParsingDate",e.toString());
                }
                item.setDate(date);
                String src = "";
                try {
                    description = description.replace(".png\">",".png\"/>").replace(".jpg\">",".jpg\"/>");
                    src = new XmlDom("<xml>"+description+"</xml>").tag("img").attr("src");
                    if (src.startsWith("//") ) {
                        src = "http:"+src;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                item.setImg(src);
                items.add(item);
            }
            Collections.sort(items, new Comparator<ClassItem>() {
                public int compare(ClassItem o1, ClassItem o2) {
                    if (o1.getDate() == null || o2.getDate() == null)
                        return 0;
                    return o2.getDate().compareTo(o1.getDate());
                }
            });

        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getFeeds();
    }
}
