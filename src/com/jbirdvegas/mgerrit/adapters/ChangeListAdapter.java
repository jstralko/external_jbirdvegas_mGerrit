package com.jbirdvegas.mgerrit.adapters;

/*
 * Copyright (C) 2013 Android Open Kang Project (AOKP)
 *  Author: Evan Conway (P4R4N01D), 2013
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.jbirdvegas.mgerrit.PatchSetViewerFragment;
import com.jbirdvegas.mgerrit.Prefs;
import com.jbirdvegas.mgerrit.R;
import com.jbirdvegas.mgerrit.cards.CommitCardBinder;
import com.jbirdvegas.mgerrit.database.UserChanges;

public class ChangeListAdapter extends SimpleCursorAdapter {

    Context mContext;

    // Cursor indices
    private Integer changeid_index;
    private Integer changenum_index;
    private Integer status_index;

    public ChangeListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (view.getTag() == null) {
            viewHolder = new ViewHolder(view);
        }

        // These indices will not change regardless of the view
        if (changeid_index == null) {
            changeid_index = cursor.getColumnIndex(UserChanges.C_CHANGE_ID);
        }
        if (changenum_index == null) {
            changenum_index = cursor.getColumnIndex(UserChanges.C_COMMIT_NUMBER);
        }
        if (status_index == null) {
            status_index = cursor.getColumnIndex(UserChanges.C_STATUS);
        }

        viewHolder.changeid = cursor.getString(changeid_index);
        viewHolder.changeStatus = cursor.getString(status_index);
        viewHolder.webAddress = getWebAddress(cursor.getInt(changenum_index));
        view.setTag(viewHolder);

        View.OnClickListener cardListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder vh = (ViewHolder) view.getTag();
                Intent intent = new Intent(PatchSetViewerFragment.NEW_CHANGE_SELECTED);
                intent.putExtra(PatchSetViewerFragment.CHANGE_ID, vh.changeid);
                intent.putExtra(PatchSetViewerFragment.STATUS, vh.changeStatus);
                intent.putExtra(PatchSetViewerFragment.EXPAND_TAG, true);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        };

        if (viewHolder.moarInfo != null) {
            viewHolder.moarInfo.setTag(viewHolder);
            viewHolder.moarInfo.setOnClickListener(cardListener);
        } else {
            // Root view already has viewHolder tagged
            view.setOnClickListener(cardListener);
        }

        viewHolder.shareView.setTag(viewHolder);
        viewHolder.shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder vh = (ViewHolder) view.getTag();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        String.format(mContext.getResources().getString(R.string.commit_shared_from_mgerrit),
                                vh.changeid));
                intent.putExtra(Intent.EXTRA_TEXT, vh.webAddress + " #mGerrit");
                mContext.startActivity(intent);
            }
        });

        viewHolder.browserView.setTag(viewHolder);
        viewHolder.browserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String webAddr = ((ViewHolder) view.getTag()).webAddress;
                if (webAddr != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(webAddr));
                    mContext.startActivity(browserIntent);
                } else {
                    Toast.makeText(view.getContext(),
                            R.string.failed_to_find_url,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        super.bindView(view, context, cursor);
    }

    private String getWebAddress(int commitNumber) {
        return String.format("%s#/c/%d/", Prefs.getCurrentGerrit(mContext), commitNumber);
    }

    private static class ViewHolder {
        ImageView browserView;
        ImageView shareView;
        ImageView moarInfo;

        String changeid;
        String changeStatus;
        String webAddress;

        ViewHolder(View view) {
            browserView = (ImageView) view.findViewById(R.id.commit_card_view_in_browser);
            shareView = (ImageView) view.findViewById(R.id.commit_card_share_info);
            moarInfo = (ImageView) view.findViewById(R.id.commit_card_moar_info);
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        CommitCardBinder binder = (CommitCardBinder) getViewBinder();
        if (binder != null ) {
            binder.onCursorChanged();
        }

        changeid_index = null;
        changenum_index = null;
        status_index = null;

        return super.swapCursor(c);
    }
}