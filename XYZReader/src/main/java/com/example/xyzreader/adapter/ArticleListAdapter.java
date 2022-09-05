package com.example.xyzreader.adapter;


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
import com.example.xyzreader.ui.ArticleListActivity;
import com.example.xyzreader.ui.DynamicHeightNetworkImageView;
import com.example.xyzreader.ui.ImageLoaderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {

    public static final String TAG = ArticleListAdapter.class.getSimpleName();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    private SimpleDateFormat mOutputFormatYear = new SimpleDateFormat("yyyy");

    private Cursor mCursor;
    private Context mContext;

    public ArticleListAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
        this.setHasStableIds(true);
    }




    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));


        Date publishedDate = parsePublishedDate();
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

        holder.yearView.setText(mOutputFormatYear.format(publishedDate));

        holder.authorView.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));

        holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance( (ArticleListActivity) mContext).getImageLoader()
        );
        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

        holder.thumbnailView.setTransitionName(mCursor.getString(ArticleLoader.Query.TITLE));

        holder.itemView.setOnClickListener(view1 -> {
            if (mContext instanceof ArticleListActivity) {
                mContext.startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                ItemsContract.Items.buildItemUri(getItemId(position))
                        ),
                        ActivityOptions
                                .makeSceneTransitionAnimation(
                                        (ArticleListActivity)mContext,
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
        return mCursor.getCount();
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



