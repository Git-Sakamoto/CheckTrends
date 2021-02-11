package com.example.checktrends.bookmark;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.checktrends.R;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment implements UrlInputDialog.UrlInputDialogListener {
    RecyclerView recyclerView;
    BookmarkRecyclerAdapter bookmarkRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_mark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setBookmarkRecycler();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bookmark,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_register:
                UrlInputDialog urlInputDialog = new UrlInputDialog();
                urlInputDialog.setTargetFragment(BookmarkFragment.this,0);
                urlInputDialog.show(getParentFragmentManager(),"url");
                break;
        }

        /*NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void registrationComplete() {
        setBookmarkRecycler();
    }

    void setBookmarkRecycler(){
        List<Bookmark> list = new ArrayList<>();
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        Cursor c = dbAdapter.selectBookmark();
        if(c.moveToFirst()){
            do {
                list.add(new Bookmark(
                        String.valueOf(c.getInt(0)),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3)
                ));
            }while (c.moveToNext());
        }
        c.close();

        bookmarkRecyclerAdapter = new BookmarkRecyclerAdapter(this,list){
            @Override
            void onItemClick(int position) {
                viewWebPage(Uri.parse(list.get(position).getUrl()));
                updateAccessTime(list.get(position).getId());
            }

            @Override
            void selectDeleteBookmark(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("ブックマークの削除");
                builder.setMessage(list.get(position).getTitle());
                builder.setPositiveButton("削除",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteBookmark(list.get(position).getId());
                    }
                });
                builder.setNegativeButton("キャンセル", null);
                builder.show();
            }
        };

        recyclerView.setAdapter(bookmarkRecyclerAdapter);
    }

    void viewWebPage(Uri uri){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), uri);
    }

    void updateAccessTime(String id){
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.updateAccessTime(id);

        setBookmarkRecycler();
    }

    void deleteBookmark(String id){
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        dbAdapter.deleteBookmark(id);

        setBookmarkRecycler();
    }

}