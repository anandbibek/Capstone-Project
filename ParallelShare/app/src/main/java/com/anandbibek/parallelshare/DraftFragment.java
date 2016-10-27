package com.anandbibek.parallelshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DraftFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    View root;
    ListView listView;
    SimpleCursorAdapter cursorAdapter;
    Communicator communicator;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ProviderContract.DraftTable._ID + " DESC";
        return new CursorLoader(getActivity(),
                ProviderContract.DraftTable.CONTENT_URI,
                ProviderContract.MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter = new SimpleCursorAdapter(root.getContext(),
                R.layout.list_item,
                data,
                new String[] {ProviderContract.DraftTable.COLUMN_NAME_CONTENT,
                        ProviderContract.DraftTable.COLUMN_NAME_TIME,
                        ProviderContract.DraftTable._ID},
                new int[] {R.id.cv1,R.id.cv2,R.id.idView},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(cursorAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface Communicator {
        void onSendIntent(Intent i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_draft,container,false);
        listView = (ListView) root.findViewById(R.id.listViewDraft);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,((TextView) (view.findViewById(R.id.cv1))).getText());
                communicator.onSendIntent(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final View v = view;
                new AlertDialog.Builder(root.getContext())
                        .setMessage("Delete selected draft?")
                        .setTitle("Confirm delete")
                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().getContentResolver().delete(
                                        ProviderContract.DraftTable.CONTENT_URI,
                                        ProviderContract.DraftTable._ID + "=?",
                                        new String[]{((TextView)(v.findViewById(R.id.idView)))
                                                .getText().toString()});
                                restartLoader();
                            }
                        })
                        .show();
                return true;
            }
        });

        //new DatabaseReader().execute();
        restartLoader();
        return root;
    }

    void restartLoader(){
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            communicator = (Communicator) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Communicator interface");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
