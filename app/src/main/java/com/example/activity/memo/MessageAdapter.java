package com.example.activity.memo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.voice.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter{

    private MemoActivity memoActivity;
    //适配器的信息集合
    private List<Message> mList;
    //构造函数，此时传入集合mList
    public MessageAdapter(List<Message> mList, MemoActivity memoActivity){
        this.mList=mList;
        this.memoActivity = memoActivity;
    }

    public void setmList(List<Message> mList) {
        this.mList = mList;
    }

    //发送信息的ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        //时间
        public TextView time;
        //内容
        public TextView content;
        //删除按钮
        public LinearLayout linearLayout;
        //自定义Menu，为了设置监听
        SlidingMenu slidingMenu;
        //构造时直接传入了View，然后得到了控件的实例
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = (TextView)itemView.findViewById(R.id.memo_activity_list_time);
            content = (TextView)itemView.findViewById(R.id.memo_activity_list_content);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.memo_activity_list_layoutright);
            slidingMenu = (SlidingMenu)itemView.findViewById(R.id.memo_activity_list_slidingmenu);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建视图
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_activity_list, parent, false);
        //创建ViewHolder并传入视图
        RecyclerView.ViewHolder mViewHolder = new ViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final long id = mList.get(position).getId();
        String time = mList.get(position).getTime();
        String content = mList.get(position).getContent();

        TextView timeView = ((ViewHolder)holder).time;
        timeView.setText(time);
        TextView contextView = ((ViewHolder)holder).content;
        contextView.setText(content);
        //为删除按钮设置监听
        LinearLayout linearLayout = ((ViewHolder)holder).linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //让界面工具类去删除这个数据
                memoActivity.interfaceUtil.deleteMessage(id);
            }
        });
        ((ViewHolder)holder).slidingMenu.setSlidingMenuUtilListener(new SlidingMenu.SlidingMenuUtilListener() {
            @Override
            public void openItem() {
                //让界面工具类去打开这个item
                memoActivity.interfaceUtil.openItem(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //自定义滑动菜单，用来记住最后一个打开的菜单
    private SlidingMenu lastSlidingMenu;

    /**
     * 设置适配器打开的最后一个菜单
     * @param slidingMenu
     */
    public void setLastMenu(SlidingMenu slidingMenu) {
        lastSlidingMenu = slidingMenu;
    }

    /**
     * 关闭上一个打开的菜单，让界面工具类去操作
     */
    public void closeLastMenu() {
        memoActivity.interfaceUtil.closeLastMenu(lastSlidingMenu);
    }


}
