package cornell.cs2046.tasks;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TaskProvider extends ContentProvider {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TASKS_TABLE_NAME = "tasks";

    private static final int TASKS = 1;
    private static final int TASK_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Tasks.AUTHORITY, Tasks.TASK_URI, TASKS);
        sUriMatcher.addURI(Tasks.AUTHORITY, Tasks.TASK_URI + "/#", TASK_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TASKS_TABLE_NAME + " ("
                    + Tasks._ID + " INTEGER PRIMARY KEY,"
                    + Tasks.TITLE + " TEXT,"
                    + Tasks.DETAILS + " TEXT,"
                    + Tasks.COMPLETED + " INTEGER,"
                    + Tasks.DATE_MODIFIED + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TASKS_TABLE_NAME);
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                break;
            case TASK_ID:
                qb.appendWhere(Tasks._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != TASKS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        ContentValues modifiedValues = new ContentValues(values);
        modifiedValues.put(Tasks.DATE_MODIFIED, System.currentTimeMillis());

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(TASKS_TABLE_NAME, Tasks.TITLE, modifiedValues);
        if (rowId > 0) {
            Uri taskUri = ContentUris.withAppendedId(Tasks.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(taskUri, null);
            return taskUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        ContentValues modifiedValues = new ContentValues(values);
        modifiedValues.put(Tasks.DATE_MODIFIED, System.currentTimeMillis());

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                count = db.update(TASKS_TABLE_NAME, modifiedValues, selection, selectionArgs);
                break;

            case TASK_ID:
                String taskId = uri.getPathSegments().get(1);
                count = db.update(TASKS_TABLE_NAME, modifiedValues, Tasks._ID + "=" + taskId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                count = db.delete(TASKS_TABLE_NAME, selection, selectionArgs);
                break;

            case TASK_ID:
                String taskId = uri.getPathSegments().get(1);
                count = db.delete(TASKS_TABLE_NAME, Tasks._ID + "=" + taskId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri arg0) {
        return null;
    }
}
