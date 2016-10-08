package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.EntryGroup;
import com.wolfie.eskey.presenter.ListPresenter;
import com.wolfie.eskey.util.DefaultLayoutManager;
import com.wolfie.eskey.view.adapter.GroupingRecyclerAdapter;
import com.wolfie.eskey.view.adapter.ScrollListeningRecyclerView;
import com.wolfie.eskey.presenter.ListPresenter.ListUi;

import java.util.List;

import butterknife.BindView;

public class ListFragment extends BaseFragment implements
        ListUi,
        ScrollListeningRecyclerView.ItemScrollListener,
        GroupingRecyclerAdapter.OnItemInListClickedListener {

    @BindView(R.id.sticky_header)
    View mStickyHeaderFrame;

    @BindView(R.id.heading_divider_top)
    View mStickyHeaderDividerTop;

    @BindView(R.id.heading_text_view)
    TextView mStickyHeaderText;

    @BindView(R.id.recycler_view)
    ScrollListeningRecyclerView mRecyclerView;

    private ListPresenter mListPresenter;

    @Override
    public ListPresenter getPresenter() {
        return mListPresenter;
    }

    public ListFragment() {
        mListPresenter = new ListPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new DefaultLayoutManager(getContext()));
        mRecyclerView.setItemScrollListener(this);
    }

    @Override
    public void refreshListWithDataSet(List<EntryGroup> groups) {
        if (groups == null) {
            getAdapter().clearItems();
        } else {
            getAdapter().setGroups(groups);
            // Briefly hide the sticky heading since its text won't be correctly
            // set until a scroll event occurs.
            mRecyclerView.scrollToPosition(0);
        }
    }

    private GroupingRecyclerAdapter getAdapter() {
        GroupingRecyclerAdapter adapter = (GroupingRecyclerAdapter)mRecyclerView.getAdapter();
        if (adapter == null) {
            adapter = new GroupingRecyclerAdapter(getContext());
            adapter.setOnItemInListClickerListener(this);
            mRecyclerView.setAdapter(adapter);
        }
        return adapter;
    }

    @Override
    public void onItemAlignedToTop(int position) {
        Object item = getAdapter().getItemAt(position);
        String headerText;
        if (item instanceof Entry) {
            Entry entry = (Entry) item;
            headerText = entry.getGroupName();
//            if (headerText == null || headerText.length() == 0) {
//                return;
//            }
        } else {
            headerText = (String) item;
        }
        mStickyHeaderText.setText(headerText);
        mStickyHeaderFrame.setVisibility(View.VISIBLE);
        mStickyHeaderDividerTop.setVisibility(View.GONE);
    }

    @Override
    public void onListItemClick(Entry selectedEntry) {
        mListPresenter.onListItemClick(selectedEntry);
    }

}
