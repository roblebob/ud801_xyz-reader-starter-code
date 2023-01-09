package com.example.xyzreader.ui.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.repository.model.Item;
import com.example.xyzreader.ui.ArticleListActivity;
import com.example.xyzreader.ui.helper.DynamicHeightNetworkImageView;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ArticleListAdapterNew extends RecyclerView.Adapter<ArticleListAdapterNew.ViewHolder> {

    public static final String TAG = ArticleListAdapterNew.class.getSimpleName();

    private Context mContext;

    public ArticleListAdapterNew( Context context) {
        mContext = context;
        this.setHasStableIds(true);
    }

    private ArrayList<Item> mItemList = new ArrayList<>();
    public void submit(List<Item> itemList) {
        mItemList = new ArrayList<>( itemList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.titleView.setText( mItemList.get( position).getTitle());


//        Date publishedDate = parsePublishedDate();
//            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
//
//                holder.subtitleView.setText(
//                        Html.fromHtml(
//                        DateUtils.getRelativeTimeSpanString(
//                                publishedDate.getTime(),
//                                System.currentTimeMillis(),
//                                DateUtils.HOUR_IN_MILLIS,
//                                DateUtils.FORMAT_ABBREV_ALL
//                            ).toString()
//                            + "<br/>" + " by "
//                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
//            } else {
//                holder.subtitleView.setText(
//                        Html.fromHtml(
//                            outputFormat.format(publishedDate)
//                            + "<br/>" + " by "
//                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
//                        )
//                );
//            }

        holder.yearView.setText( String.format( Locale.getDefault(),"%d",
                LocalDateTime.ofInstant( Instant.parse( mItemList.get( position).getPublishedDate() + "Z"), ZoneId.systemDefault()) .getYear()
        ));

        holder.authorView.setText( mItemList.get( position).getAuthor());

        holder.thumbnailView.setImageUrl(
                mItemList.get( position).getThumb(),
                ImageLoaderHelper.getInstance( (ArticleListActivity) mContext).getImageLoader()
        );

        holder.thumbnailView.setAspectRatio( (float) mItemList.get( position).getAspectRatio());


        holder.thumbnailView.setTransitionName( mItemList.get( position).getTitle());

        holder.itemView.setOnClickListener(view1 -> {
            if (mContext instanceof ArticleListActivity) {
                mContext.startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                ItemsContract.Items.buildItemUri( mItemList.get( position).getId())
                        ),
                        ActivityOptions
                                .makeSceneTransitionAnimation(
                                        (ArticleListActivity) mContext,
                                        holder.thumbnailView,
                                        holder.thumbnailView.getTransitionName()
                                )
                                .toBundle()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }





    static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView authorView;
        public TextView yearView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.list_item_article__title_tv);
            authorView = (TextView) view.findViewById(R.id.list_item_article__author_tv);
            yearView = view.findViewById(R.id.list_item_article__year_tv);
        }
    }
}
