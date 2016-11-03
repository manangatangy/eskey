package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import butterknife.OnClick;

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

    @BindView(R.id.add_entry_fab)
    View mAddEntryButton;

    @OnClick(R.id.add_entry_fab)
    public void onAddEntryClick() {
        mListPresenter.onListItemClick(null);
    }

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
            // Scroll back to the top of the list.  If the list is short, no scrolling
            // will occur and so we also have to trigger the sticky header refresh.
            mRecyclerView.scrollToPosition(0);
            onItemAlignedToTop(0);
        }
    }

    private GroupingRecyclerAdapter getAdapter() {
        GroupingRecyclerAdapter adapter = (GroupingRecyclerAdapter)mRecyclerView.getAdapter();
        if (adapter == null) {
            adapter = new GroupingRecyclerAdapter();
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
    public void setAddEntryVisibility(boolean visible) {
        mAddEntryButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void hideStickyHeader() {
        mStickyHeaderFrame.setVisibility(View.GONE);
    }

    @Override
    public void onListItemClick(Entry selectedEntry) {
        mListPresenter.onListItemClick(selectedEntry);
    }

}
