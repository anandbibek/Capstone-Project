/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anandbibek.parallelshare;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class DraftInfoProvider extends ContentProvider {

    private DatabaseManager mSQLOpenHelper;


    @Override
    public boolean onCreate() {
        mSQLOpenHelper = new DatabaseManager(getContext());
        return true;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        return ProviderContract.DraftTable.CONTENT_TYPE;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        retCursor = mSQLOpenHelper.getReadableDatabase().query(
                        ProviderContract.DraftTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
        //retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mSQLOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id = db.insert(ProviderContract.DraftTable.TABLE_NAME, null, values);
        if ( _id > 0 )
            returnUri = ProviderContract.DraftTable.buildDraftUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        //getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSQLOpenHelper.getWritableDatabase();
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        rowsDeleted = db.delete(ProviderContract.DraftTable.TABLE_NAME, selection, selectionArgs);

        //if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSQLOpenHelper.getWritableDatabase();
        int rowsUpdated;

        rowsUpdated = db.update(ProviderContract.DraftTable.TABLE_NAME, values, selection, selectionArgs);
        //if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}