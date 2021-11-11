package com.pgi.convergencemeetings.search.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.utils.CommonUtils;
import com.pgi.convergencemeetings.R;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.utils.JoinMeetingsListener;
import com.pgi.logging.Logger;
import com.pgi.logging.enums.LogEvent;
import com.pgi.network.models.ImeetingRoomInfo;
import com.pgi.network.models.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SearchResult} and makes a call to the
 * specified {@link JoinMeetingsListener}.
 */
public class JoinMeetingsAdapter extends RecyclerView.Adapter<JoinMeetingsAdapter.ViewHolder> {

    private List<ImeetingRoomInfo> imeetingRoomInfos;
    private final JoinMeetingsListener mListener;
    private Logger mlogger = ConvergenceApplication.mLogger;

    public JoinMeetingsAdapter(List<ImeetingRoomInfo> searchResults, JoinMeetingsListener listener) {
        imeetingRoomInfos = searchResults;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_meetings_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = imeetingRoomInfos.get(position);
        holder.mMeetingUrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommonUtils.copyRoomURL(CommonUtils.getCtx(), imeetingRoomInfos.get(position).getFurl(), AppConstants.COPY_URL_UNIVERSAL);
                return true;
            }
        });
        String furl = holder.mItem.getMeetingRoomFurl();
        String nameInitials = holder.mItem.getMeetingNameInitials();
        String profilePic = holder.mItem.getProfileImage();
        TextView profileInitialsView = holder.mProfileNameInitials;
        TextView meetingUrlView = holder.mMeetingUrl;
        TextView meetingNameView = holder.mMeetingName;
        String meetingHostName = holder.mItem.getMeetingHostName();
        CircleImageView profileImage = holder.mProfileImage;
        ImageView noProfile = holder.mNoProfileImage;
        boolean isLocalJapan = CommonUtils.isUsersLocaleJapan();

        if (meetingHostName != null) {
            if (isLocalJapan) {
                meetingNameView.setText(CommonUtils.formatJapaneseName(meetingHostName));
            } else {
                meetingNameView.setText(CommonUtils.formatCamelCase(meetingHostName));
            }
            meetingNameView.setVisibility(View.VISIBLE);
        } else {
            meetingNameView.setVisibility(View.GONE);
        }
        if (furl != null) {
            meetingUrlView.setVisibility(View.VISIBLE);
            meetingUrlView.setText(furl);
        }
        if (profilePic != null && !profilePic.isEmpty()) {
            Picasso.get().load(profilePic).fit().into(profileImage);
            profileInitialsView.setVisibility(View.INVISIBLE);
            noProfile.setVisibility(View.INVISIBLE);
            profileImage.setVisibility(View.VISIBLE);
        } else if (nameInitials != null && !nameInitials.isEmpty()) {
            profileImage.setVisibility(View.INVISIBLE);
            noProfile.setVisibility(View.INVISIBLE);
            profileInitialsView.setVisibility(View.VISIBLE);
            if (isLocalJapan) {
                profileInitialsView.setText(CommonUtils.formatJapaneseInitials(nameInitials));
            } else {
                profileInitialsView.setText(nameInitials.toUpperCase());
            }
        } else {
            profileInitialsView.setVisibility(View.INVISIBLE);
            profileImage.setVisibility(View.INVISIBLE);
            noProfile.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the DialInActivityContract, if the
                    // DialInFragmentContract is attached to one) that an item has been selected.
                    mlogger.record(LogEvent.FEATURE_JOINFROMSEARCH);
                    mListener.onRecentMeetingClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imeetingRoomInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.tv_profile_name)
        TextView mProfileNameInitials;
        @BindView(R.id.tv_meeting_name)
        TextView mMeetingName;
        @BindView(R.id.tv_meeting_url)
        TextView mMeetingUrl;
        @BindView(R.id.profile_image)
        CircleImageView mProfileImage;
        @BindView(R.id.iv_no_profile)
        ImageView mNoProfileImage;
        public ImeetingRoomInfo mItem;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
