package com.example.nimish.udacitytracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nimish.udacitytracker.data.Course;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;


/**
 * Created by nimishsanghi on 30/07/16.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseAdapterViewHolder> {

    final private Context mContext;
    private Cursor mCursor;
    private boolean mTwoPane;
    private FirebaseAnalytics mFirebaseAnalytics;


    //public class CourseAdapterViewHolder extends RecyclerView.ViewHolder implements View
    // .OnClickListener {
    public class CourseAdapterViewHolder extends RecyclerView.ViewHolder {


        public View mView;
        public ImageView mIconView;
        public TextView mTitleView;
        public TextView mLevelView;
        public TextView mNewReleaseView;
        public TextView mShortSummaryView;

        public CourseAdapterViewHolder(View view) {

            super(view);
            mView = view;
            mIconView = (ImageView) view.findViewById(R.id.course_image);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mLevelView = (TextView) view.findViewById(R.id.level);
            mNewReleaseView = (TextView) view.findViewById(R.id.new_release);
            mShortSummaryView = (TextView) view.findViewById(R.id.short_summary);
        }

    }


    //public CourseAdapter(Context context, CourseAdapterOnClickHandler dh, View emptyView, int
    // choiceMode) {
    public CourseAdapter(Context context, boolean twoPane) {
        mContext = context;
        mTwoPane = twoPane;

        // FOr Google Analytics - initialize
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);


    }

    @Override
    public CourseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_content,
                parent, false);
        view.setFocusable(true);
        return new CourseAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mTitleView.setText(mCursor.getString(MainActivityFragment.COL_COURSE_TITLE));
        holder.mShortSummaryView.setText(mCursor.getString(MainActivityFragment
                .COL_COURSE_SHORT_SUMMARY));
        String level = mCursor.getString(MainActivityFragment.COL_COURSE_LEVEL);
        if (level.length() > 0)
            level = level.substring(0, 1).toUpperCase() + level.substring(1);
        holder.mLevelView.setText(level);
        if (mCursor.getString(MainActivityFragment.COL_COURSE_NEW_RELEASE).equalsIgnoreCase
                ("true")) {
            holder.mNewReleaseView.setVisibility(View.VISIBLE);
        } else {
            holder.mNewReleaseView.setVisibility(View.INVISIBLE);
        }

        String imageUri = mCursor.getString(MainActivityFragment.COL_COURSE_IMAGE);
        if (!imageUri.trim().isEmpty())
            Picasso.with(mContext).load(imageUri).into(holder.mIconView);

        final Course course = new Course(
                mCursor.getLong(MainActivityFragment.COL_COURSE_ID),
                mCursor.getString(MainActivityFragment.COL_COURSE_CODE),
                mCursor.getString(MainActivityFragment.COL_COURSE_TITLE),
                mCursor.getString(MainActivityFragment.COL_COURSE_HOMEPAGE),
                mCursor.getString(MainActivityFragment.COL_COURSE_SUBTITLE),
                mCursor.getString(MainActivityFragment.COL_COURSE_LEVEL),
                mCursor.getString(MainActivityFragment.COL_COURSE_IMAGE),
                mCursor.getString(MainActivityFragment.COL_COURSE_BANNER_IMAGE),
                mCursor.getString(MainActivityFragment.COL_COURSE_TEASER_VIDEO),
                mCursor.getString(MainActivityFragment.COL_COURSE_SUMMARY),
                mCursor.getString(MainActivityFragment.COL_COURSE_SHORT_SUMMARY),
                mCursor.getString(MainActivityFragment.COL_COURSE_REQUIRED_KNOWLEDGE),
                mCursor.getString(MainActivityFragment.COL_COURSE_EXPECTED_LEARING),
                mCursor.getString(MainActivityFragment.COL_COURSE_EXPECTED_DURATION),
                mCursor.getString(MainActivityFragment.COL_COURSE_EXPECTED_DURATION_UNIT),
                mCursor.getString(MainActivityFragment.COL_COURSE_NEW_RELEASE),
                mCursor.getInt(MainActivityFragment.COL_COURSE_FAVORITE)
        );


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, course.getCourseCode());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, course.getTitle());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "course");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);


                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(CourseDetailFragment.ARG_COURSE, course);
                    CourseDetailFragment fragment = new CourseDetailFragment();
                    fragment.setArguments(arguments);
                    FragmentManager fragmentManager = ((AppCompatActivity) mContext)
                            .getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.course_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CourseDetailActivity.class);
                    intent.putExtra(CourseDetailFragment.ARG_COURSE, course);
                    //context.startActivity(intent);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(
                                    (AppCompatActivity) mContext,
                                    new Pair<View, String>(
                                            v.findViewById(R.id.course_image),
                                            context.getString(R.string.transition_name_image)));
                    ActivityCompat.startActivity((AppCompatActivity) mContext, intent, options
                            .toBundle());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

}
