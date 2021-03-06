//package com.depromeet.hanriver.hanrivermeetup.fragment.meeting.Adapter;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ExpandableListView;
//import android.widget.TextView;
//
//import com.depromeet.hanriver.hanrivermeetup.R;
//import com.depromeet.hanriver.hanrivermeetup.model.meeting.MeetingDetail;
//
//import java.util.List;
//
//public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    public static final int HEADER = 0;
//    public static final int CHILD = 1;
//
//    private List<MeetingDetail> data ;
//
//    public ExpandableListAdapter(List<MeetingDetail> data){
//        this.data=data;
//
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
//        View view = null;
//        Context context = parent.getContext();
//        float dp = context.getResources().getDisplayMetrics().density;
//        int subItemPaddingLeft = (int) (18 * dp);
//        int subItemPaddingTopAndBottom = (int) (5 * dp);
//        switch (type) {
//            case HEADER:
//                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                view = inflater.inflate(R.layout.createroom_item, parent, false);
//                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
//                return header;
//            case CHILD:
//                TextView itemTextView = new TextView(context);
//                itemTextView.setPadding(subItemPaddingLeft, subItemPaddingTopAndBottom, 0, subItemPaddingTopAndBottom);
//                itemTextView.setTextColor(0x88000000);
//                itemTextView.setLayoutParams(
//                        new ViewGroup.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT));
//                return new RecyclerView.ViewHolder(itemTextView) {
//                };
//        }
//        return null;
//    }
//
//
//
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        final MeetingDetail room = data.get(position);
//        switch (room.type) {
//            case HEADER:
//                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
//                itemController.refferalItem = room;
//                itemController.header_title.setText(room.text);
//                if (room.invisibleChildren == null) {
//                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
//                } else {
//                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
//                }
//                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (item.invisibleChildren == null) {
//                            item.invisibleChildren = new ArrayList<Item>();
//                            int count = 0;
//                            int pos = data.indexOf(itemController.refferalItem);
//                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
//                                item.invisibleChildren.add(data.remove(pos + 1));
//                                count++;
//                            }
//                            notifyItemRangeRemoved(pos + 1, count);
//                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
//                        } else {
//                            int pos = data.indexOf(itemController.refferalItem);
//                            int index = pos + 1;
//                            for (Item i : item.invisibleChildren) {
//                                data.add(index, i);
//                                index++;
//                            }
//                            notifyItemRangeInserted(pos + 1, index - pos - 1);
//                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
//                            item.invisibleChildren = null;
//                        }
//                    }
//                });
//                break;
//            case CHILD:
//                TextView itemTextView = (TextView) holder.itemView;
//                itemTextView.setText(data.get(position).text);
//                break;
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return data.get(position).type;
//    }
//
//    @Override
//    public int getItemCount() {
//        return data.size();
//    }
//
//    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
//        public TextView location;
//        public TextView time;
//        public TextView member;
//        public MeetingDetail room;
//
//        public ListHeaderViewHolder(View itemView) {
//            super(itemView);
//            header_title = (TextView) itemView.findViewById(R.id.header_title);
//            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
//        }
//    }
//
//
//}
