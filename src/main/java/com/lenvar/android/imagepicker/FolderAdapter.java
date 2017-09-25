package com.lenvar.android.imagepicker;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder> {

    private Activity mContext;
    private LayoutInflater mLayoutInflater;
    private List<Folder> mFolders;
    private List<String> mPickList;
    private PickerConfig mConfig;

    public FolderAdapter(Activity context, PickerConfig config) {
        this.mContext = context;
        this.mLayoutInflater = mContext.getLayoutInflater();
        this.mFolders = new ArrayList<>();
        this.mConfig = config;
    }

    public void set(List<Folder> folders) {
        mFolders.clear();
        mFolders.addAll(folders);
        notifyDataSetChanged();
    }

    public void setPickList(List<String> pickList) {
        mPickList = null;
        mPickList = pickList;
        notifyDataSetChanged();
    }

    @Override
    public FolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FolderHolder(mLayoutInflater.inflate(R.layout.item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(FolderHolder holder, int position) {
        final Folder folder = mFolders.get(position);

        if (folder.imgs != null && folder.imgs.size() > 0)
            mConfig.getImageLoader().displayImage(mContext, holder.mImg, folder.imgs.get(0));
        if (folder.path != null) {
            if (folder.path.contains("/"))
                holder.mName.setText(folder.path.substring(folder.path.lastIndexOf("/")));
            else
                holder.mName.setText(folder.path);
        }
        int count = contains(folder.path);
        if (count == 0) {
            holder.mCount.setText(null);
            holder.mCount.setVisibility(View.INVISIBLE);
        } else {
            holder.mCount.setText(count + "");
            holder.mCount.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnFolderClickListener != null)
                    mOnFolderClickListener.onFolderClick(folder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFolders.size();
    }

    private int contains(String path) {
        int count = 0;
        if (mPickList != null) {
            for (String imgPath : mPickList) {
                if (imgPath.substring(0, imgPath.lastIndexOf("/")).equalsIgnoreCase(path)) {
                    count++;
                }
            }
        }
        return count;
    }

    class FolderHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mName, mCount;

        public FolderHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.image);
            mName = (TextView) itemView.findViewById(R.id.folder);
            mCount = (TextView) itemView.findViewById(R.id.count);
            if (mCount.getBackground() != null) {
                ((GradientDrawable) mCount.getBackground()).setColor(mConfig.getStyleColor());
            }
        }
    }

    static class Folder {
        String path;
        List<String> imgs;

        public Folder() {
        }

        public Folder(String path, List<String> imgs) {
            this.path = path;
            this.imgs = imgs;
        }
    }

    private OnFolderClickListener mOnFolderClickListener;

    public void setOnFolderClickListener(OnFolderClickListener listener) {
        this.mOnFolderClickListener = listener;
    }

    public interface OnFolderClickListener {

        void onFolderClick(Folder folder);
    }
}
