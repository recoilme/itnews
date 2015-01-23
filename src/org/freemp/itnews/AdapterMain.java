package org.freemp.itnews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by recoilme on 23/01/15.
 */
public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {

    private ArrayList<ClassItem> data;
    private AQuery aq;
    private Activity activity;
    private DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());

    public AdapterMain(Activity activity,ArrayList<ClassItem> data) {
        this.activity = activity;
        this.data = data;
        aq = new AQuery(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView stgvImageView;
        private ImageView userAva;
        private TextView siteurl;
        private TextView userFullname;
        private TextView articleTitle;
        public ViewHolder(View holderView) {
            super(holderView);
            stgvImageView = (ImageView) holderView.findViewById(R.id.stgvImageView);
            siteurl = (TextView) holderView.findViewById(R.id.siteurl);
            userAva = (ImageView) holderView.findViewById(R.id.userAva);
            userFullname = (TextView) holderView.findViewById(R.id.userFullname);
            articleTitle = (TextView) holderView.findViewById(R.id.articleTitle);
            cardView = (CardView) holderView.findViewById(R.id.card_view);
        }
    }

    @Override
    public AdapterMain.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ClassItem item = data.get(i);
        aq.id(viewHolder.articleTitle).text(item.getTitle());
        aq.id(viewHolder.siteurl).text(item.getLink());
        aq.id(viewHolder.userAva).image(item.getLogo());
        String author = ""+item.getAuthor();
        if (author.equals("null")) author="";
        aq.id(viewHolder.userFullname).text(author + " " + formatter.format(item.getDate()));
        aq.id(viewHolder.stgvImageView).clear();
        if (TextUtils.equals(item.getImg(),""))
            aq.id(viewHolder.stgvImageView).gone();
        else {
            aq.id(viewHolder.stgvImageView).visible().image(item.getImg(), true, false, 640, 0, null, AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
        }
        final String url = item.getLink();
        aq.id(viewHolder.cardView).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    activity.startActivity(i);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
