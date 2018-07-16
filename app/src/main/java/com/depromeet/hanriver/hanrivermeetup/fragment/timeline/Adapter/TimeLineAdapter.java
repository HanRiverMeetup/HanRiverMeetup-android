package com.depromeet.hanriver.hanrivermeetup.fragment.timeline.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.model.timeline.TimeLineVO;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ItemViewHolder> {

    LayoutInflater inflater;
    private List<TimeLineVO> mItems;
    private android.app.Activity mAct;

    public TimeLineAdapter(android.app.Activity act, List<TimeLineVO> items) {
        mAct = act;
        mItems = items;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 커스텀 뷰홀더 
// item layout 에 존재하는 위젯들을 바인딩합니다. 
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;

        public ItemViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mTextView = (TextView) view.findViewById(R.id.textview);
        }
    }

    // 새로운 뷰 홀더 생성 
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
        View view = inflater.inflate(R.layout.timeline_item, parent, false);
        return new ItemViewHolder(view);
    }

    // View 의 내용을 해당 포지션의 데이터로 바꿉니다. 
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.mTextView.setText(mItems.get(position).getText());
        holder.mImageView.setImageResource(mItems.get(position).getImg());

    }

    // 데이터 셋의 크기를 리턴해줍니다. e
    @Override
    public int getItemCount() {
        return mItems.size();
    }
}