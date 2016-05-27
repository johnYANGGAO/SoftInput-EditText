package com.johnson.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    LinearLayout rootview;
    ListView listView;
    ArrayList<String> items = new ArrayList<>();
    ViewTreeObserver.OnGlobalLayoutListener listener;
    TextWatcher mTextWatcher;
    FrameLayout pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        rootview = (LinearLayout) findViewById(R.id.root);
        pop = (FrameLayout) findViewById(R.id.pop);
        final View decorView = this.getWindow().getDecorView();

        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (isKeyboardShown(decorView)) {
                    Log.v("ssd", "键盘弹起");
                    editText.addTextChangedListener(mTextWatcher);

                } else {
                    Log.v("ssd", "键盘收起状态");
                    dismissPopWindow();
                }

            }
        };
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                    addGolobalListener(listener);

                } else {
                    dismissPopWindow();
                    removeGlobalListener(listener);
                }
            }
        });

        mTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(editText.getEditableText())) {

                    dismissPopWindow();
                } else {
                    //模拟网络请求加载数据
                    addListView(editText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

    }


    private void addGolobalListener(ViewTreeObserver.OnGlobalLayoutListener listener) {

        rootview.getViewTreeObserver().addOnGlobalLayoutListener(listener);

    }

    private void removeGlobalListener(ViewTreeObserver.OnGlobalLayoutListener listener) {

        rootview.getViewTreeObserver().removeOnGlobalLayoutListener(listener);

    }

    //模拟 加载 数据
    private void initData() {

        for (int i = 0; i < 10; i++) {

            items.add("haha : " + i);
        }

    }


    //加载pop代码
    private void addListView(final EditText editText) {

        if (items != null) {

            items.clear();
        }
        initData();
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);


        if (listView != null) {
            listView = null;
        }

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listView.setDividerHeight(1);
        listView.setCacheColorHint(Color.alpha(1));
        listView.setBackgroundColor(Color.WHITE);

        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mTextWatcher != null) {
                    editText.removeTextChangedListener(mTextWatcher);
                }
                editText.setText(items.get(i));
                dismissPopWindow();
                quitSoftInput();

            }
        });
        pop.addView(listView);

    }

    public void dismissPopWindow() {
        if (pop.getChildCount() > 0) {
            pop.removeAllViews();
        }
    }


    private void quitSoftInput() {
        rootview.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }
}
