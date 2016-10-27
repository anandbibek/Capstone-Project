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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class ProviderContract {

    public static final String CONTENT_AUTHORITY = "com.anandbibek.parallelshare";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DRAFT_DATA = "draft_data";

    public static final String[] MOVIE_COLUMNS = {

            ProviderContract.DraftTable._ID,
            DraftTable.COLUMN_NAME_CONTENT,
            DraftTable.COLUMN_NAME_SHARE,
            DraftTable.COLUMN_NAME_TIME
    };

    public static final class DraftTable implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRAFT_DATA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRAFT_DATA;

        static final String TABLE_NAME = "posts";
        static final String COLUMN_NAME_CONTENT = "content";
        static final String COLUMN_NAME_TIME = "time";
        static final String COLUMN_NAME_SHARE = "share";

        public static Uri buildDraftUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
