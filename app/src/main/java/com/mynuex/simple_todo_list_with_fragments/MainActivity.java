package com.mynuex.simple_todo_list_with_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        AddToDoItemFragment.NewItemCreatedListener,
        ToDoItemDetailFragment.MarkItemAsDoneListener,
        ToDoListFragment.ListItemSelectedListener {

    private static final String BUNDLE_KEY_TODO_ITEMS = "TODO ITEMS ARRAY LIST";

    private static final String TAG_ADD_NEW_FRAG = "ADD NEW FRAGMENT";
    private static final String TAG_LIST_FRAG = "LIST FRAGMENT";
    private static final String TAG_DETAIL_FRAG = "DETAIL FRAGMENT";

    private ArrayList<ToDoItem> mTodoItems;

    private static final String TAG = "MAIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            //no saved instance state - first time Activity been created
            //Create new ArrayList, and add Add and List fragments.
            Log.d(TAG, "onCreate has no instance state. Setting up ArrayList, adding List Fragment and Add Fragment");

            mTodoItems = new ArrayList<>();

            // Add example data for testing. Remove/edit these lines for testing app
            mTodoItems.add(new ToDoItem("Water plants", false));
            mTodoItems.add(new ToDoItem("Feed cat", true));
            mTodoItems.add(new ToDoItem("Grocery shopping", false));

            // TODO create and add initial fragments (Add and List) to screen
            AddToDoItemFragment addNewFragment = AddToDoItemFragment.newInstance();
            ToDoListFragment listFragment = ToDoListFragment.newInstance(mTodoItems);
            ToDoItemDetailFragment detailFragment = ToDoItemDetailFragment.newInstance(new ToDoItem("", false));

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            // Add, including tags to help find the fragments on screen, if they need to be updated
            ft.add(R.id.add_todo_view_container, addNewFragment, TAG_ADD_NEW_FRAG);
            ft.add(R.id.todo_list_view_container, listFragment, TAG_LIST_FRAG);
            ft.add(R.id.todo_detail_view_container, detailFragment, TAG_DETAIL_FRAG);

            ft.commit();

        } else {
            //There is saved instance state, so the app has already run,
            //and the Activity should already have fragments.
            //Restore saved instance state, the ArrayList
            mTodoItems = savedInstanceState.getParcelableArrayList(BUNDLE_KEY_TODO_ITEMS);
            Log.d(TAG, "onCreate has saved instance state ArrayList =  " + mTodoItems);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outBundle) {
        // Save the list of ToDoItems when app is rotated
        super.onSaveInstanceState(outBundle);
        outBundle.putParcelableArrayList(BUNDLE_KEY_TODO_ITEMS, mTodoItems);
    }


    @Override
    public void newItemCreated(ToDoItem newItem) {
        // Add item to the ArrayList
        mTodoItems.add(newItem);

        Log.d(TAG, "Notified that this new item was created: " + mTodoItems);

        // Get reference to List Fragment from the FragmentManager, tell this Fragment that the data has changed
        FragmentManager fm = getSupportFragmentManager();
        ToDoListFragment listFragment = (ToDoListFragment) fm.findFragmentByTag(TAG_LIST_FRAG);
        listFragment.notifyItemsChanged();
        hideKeyboard();

    }

    @Override
    public void itemSelected(ToDoItem selected) {
        FragmentManager fm = getSupportFragmentManager();
        ToDoItemDetailFragment toDoItemDetailFragment = (ToDoItemDetailFragment) fm.findFragmentByTag(TAG_DETAIL_FRAG);
        toDoItemDetailFragment.setTodoItem(selected);
    }

    @Override
    public void todoItemDone(ToDoItem doneItem) {
        mTodoItems.remove(doneItem);
        //Find List fragment and tell it that the data has changed
        FragmentManager fm = getSupportFragmentManager();
        ToDoListFragment listFragment = (ToDoListFragment) fm.findFragmentByTag(TAG_LIST_FRAG);
        listFragment.notifyItemsChanged();
    }

    private void hideKeyboard() {
        View mainView = findViewById(android.R.id.content);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mainView.getWindowToken(), 0);
    }

}
