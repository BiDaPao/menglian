package com.aihuan.doc.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihuan.doc.R;
import com.aihuan.doc.bean.Node;

import java.util.List;

/**
 * Created by cxf on 2019/3/9.
 */

public class ContentsAdapter extends RecyclerView.Adapter {

    private static final String PAYLOAD = "payload";
    private static final int MODULE = 0;
    private static final int PACKAGE = 1;
    private static final int PACKAGE_NORMAL = 2;
    private static final int NORMAL = 3;


    private SparseArray<Node> mSparseArray;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnItemClickListener;

    public ContentsAdapter(Context context) {
        mSparseArray = new SparseArray<>();
        mInflater = LayoutInflater.from(context);
        mOnItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                Node node = (Node) tag;
                if (node.isExpand()) {
                    collapsed(node);
                } else {
                    expand(node);
                }
            }
        };
    }

    public void setData(List<Node> parentNodeList) {
        if (mSparseArray != null && parentNodeList != null) {
            mSparseArray.clear();
            int size = parentNodeList.size();
            int index = 0;
            for (int i = 0; i < size; i++) {
                Node parentNode = parentNodeList.get(i);
                if (parentNode != null) {
                    parentNode.setIndex(index);
                    mSparseArray.put(index, parentNode);
                    index++;
                }
            }
        }
    }

    private void expand(Node parentNode) {
        if (parentNode.isExpand()) {
            return;
        }
        if (parentNode.hasChildren()) {
            parentNode.setExpand(true);
            List<Node> childList = parentNode.getChildList();
            int childSize = childList.size();
            int parentIndex = parentNode.getIndex();
            for (int i = mSparseArray.size() - 1; i > parentIndex; i--) {
                int index = i + childSize;
                Node node = mSparseArray.get(i);
                node.setIndex(index);
                mSparseArray.put(index, node);
            }
            for (int i = 0; i < childSize; i++) {
                int index = parentIndex + i + 1;
                Node node = childList.get(i);
                node.setIndex(index);
                mSparseArray.put(index, node);
            }
            notifyItemChanged(parentIndex, PAYLOAD);
            notifyItemRangeInserted(parentIndex + 1, childSize);
            notifyItemRangeChanged(parentIndex + childSize, getItemCount(), PAYLOAD);
        }
    }

    private void collapsed(Node parentNode) {
        if (!parentNode.isExpand()) {
            return;
        }
        if (parentNode.hasChildren()) {
            parentNode.setExpand(false);
            int parentIndex = parentNode.getIndex();
            int childCount = 0;
            List<Node> childList = parentNode.getChildList();
            for (Node node : childList) {
                childCount++;
                if (node.hasChildren() && node.isExpand()) {
                    node.setExpand(false);
                    childCount += node.getChildList().size();
                }
            }
            int allCount = mSparseArray.size();
            for (int i = parentIndex + childCount + 1; i < allCount; i++) {
                Node node = mSparseArray.get(i);
                if (node != null) {
                    int index = i - childCount;
                    node.setIndex(index);
                    mSparseArray.put(index, node);
                }
            }
            for (int i = 0; i < childCount; i++) {
                mSparseArray.remove(allCount - 1 - i);
            }
            notifyItemRangeRemoved(parentIndex + 1, childCount);
            notifyItemRangeChanged(parentIndex, getItemCount(), PAYLOAD);
        }
    }


    @Override
    public int getItemViewType(int position) {
        Node node = mSparseArray.get(position);
        switch (node.getLevel()) {
            case 0:
                return MODULE;
            case 1:
                return node.hasChildren() ? PACKAGE : PACKAGE_NORMAL;
            default:
                return NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MODULE:
                return new ParentVh(mInflater.inflate(R.layout.item_module, parent, false));
            case PACKAGE:
                return new ParentVh(mInflater.inflate(R.layout.item_package, parent, false));
            case PACKAGE_NORMAL:
                return new Vh(mInflater.inflate(R.layout.item_package_normal, parent, false));
            default:
                return new Vh(mInflater.inflate(R.layout.item_normal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Node node = mSparseArray.get(position);
        if (node != null) {
            Object payload = payloads.size() > 0 ? payloads.get(0) : null;
            ((Vh) vh).setData(node, payload);
        }
    }

    @Override
    public int getItemCount() {
        return mSparseArray.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mDes;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mDes = itemView.findViewById(R.id.des);
        }

        void setData(Node node, Object payload) {
            if (payload == null) {
                mName.setText(node.getName());
                mDes.setText(node.getDes());
            }
        }
    }

    class ParentVh extends Vh {

        View mTriangle;

        public ParentVh(View itemView) {
            super(itemView);
            mTriangle = itemView.findViewById(R.id.triangle);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        void setData(Node node, Object payload) {
            super.setData(node, payload);
            itemView.setTag(node);
            if (node.isExpand()) {
                mTriangle.setRotation(90);
            } else {
                mTriangle.setRotation(0);
            }
        }
    }

}
