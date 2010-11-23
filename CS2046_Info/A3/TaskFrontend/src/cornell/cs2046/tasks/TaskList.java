package cornell.cs2046.tasks;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * TaskList Activity which reads Tasks from a ContentProvider.
 *
 * Note that queries to the ContentProvider are done in the UI thread; this is not the best
 * practice, and they should be done in a background thread.
 */
public class TaskList extends Activity {
    private static final int MENU_ITEM_ADD = Menu.FIRST;
    private static final int MENU_ITEM_DELETE_OLD = Menu.FIRST + 1;
    private static final int MENU_ITEM_BACKUP = Menu.FIRST + 2;
    private static final int MENU_ITEM_RESTORE = Menu.FIRST + 3;

    private static final int CONTEXT_MENU_ITEM_MARK_COMPLETED = Menu.FIRST;
    private static final int CONTEXT_MENU_ITEM_MODIFY = Menu.FIRST + 1;
    private static final int CONTEXT_MENU_ITEM_DELETE = Menu.FIRST + 2;

    private ListView mTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        mTasks = (ListView) findViewById(R.id.tasks);
        mTasks.setEmptyView(findViewById(R.id.no_tasks));

        mTasks.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                ContentValues values = new ContentValues();
                values.put(Tasks.COMPLETED, mTasks.isItemChecked(position) ? 1 : 0);
                Uri updateUri = ContentUris.withAppendedId(Tasks.CONTENT_URI, id);
                getContentResolver().update(updateUri, values, null, null);
            }
        });

        mTasks.setOnCreateContextMenuListener(this);

        String[] projection = new String[] {
                Tasks._ID,
                Tasks.TITLE,
                Tasks.COMPLETED
        };

        Cursor cursor = managedQuery(Tasks.CONTENT_URI, projection, null, null,
                Tasks.COMPLETED + " ASC," + Tasks.DATE_MODIFIED + " DESC");

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, cursor,
                new String[] { Tasks.TITLE, Tasks.COMPLETED },
                new int[] { android.R.id.text1, android.R.id.text1 });

        adapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cur, int columnIndex) {
                switch(columnIndex) {
                    case 2:
                        mTasks.setItemChecked(cur.getPosition(), cur.getInt(columnIndex) == 1);
                        CheckedTextView checked = (CheckedTextView) view;
                        if (cur.getInt(columnIndex) == 1) {
                            checked.setPaintFlags(checked.getPaintFlags()
                                    | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            checked.setPaintFlags(checked.getPaintFlags()
                                    & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        return true;
                }
                return false;
            }
        });

        mTasks.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mTasks.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_ITEM_ADD, Menu.NONE, R.string.add_task)
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(Menu.NONE, MENU_ITEM_DELETE_OLD, Menu.NONE, R.string.delete_old)
                .setIcon(android.R.drawable.ic_menu_delete);
        menu.add(Menu.NONE, MENU_ITEM_BACKUP, Menu.NONE, R.string.backup)
                .setIcon(android.R.drawable.ic_menu_share);
        menu.add(Menu.NONE, MENU_ITEM_RESTORE, Menu.NONE, R.string.restore)
                .setIcon(android.R.drawable.ic_menu_revert);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case MENU_ITEM_ADD:
                ContentValues values = new ContentValues();
                values.put(Tasks.TITLE, "New task");
                values.put(Tasks.COMPLETED, 0);
                values.put(Tasks.DETAILS, "");
                Uri newTask = getContentResolver().insert(Tasks.CONTENT_URI, values);
                i = new Intent(this, TaskEdit.class);
                i.setData(newTask);
                startActivity(i);
                return true;
            case MENU_ITEM_DELETE_OLD:
                i = new Intent();
                i.setAction(Tasks.ACTION_DELETE_OLD);
                if (startService(i) == null) {
                    Toast.makeText(this, "Unable to connect to TaskService.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Deleting old tasks.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case MENU_ITEM_BACKUP:
                i = new Intent();
                i.setAction(Tasks.ACTION_BACKUP_TASKS);
                if (startService(i) == null) {
                    Toast.makeText(this, "Unable to connect to TaskBackupService.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Backing up tasks.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case MENU_ITEM_RESTORE:
                i = new Intent();
                i.setAction(Tasks.ACTION_RESTORE_TASKS);
                if (startService(i) == null) {
                    Toast.makeText(this, "Unable to connect to TaskBackupService.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Restoring tasks.", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = (Cursor) mTasks.getAdapter().getItem(info.position);
        if (cursor == null) {
            return;
        }
        menu.setHeaderTitle(cursor.getString(1));
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_MODIFY, 0, R.string.modify_task);
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_MARK_COMPLETED, 1, R.string.mark_complete_task);
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_DELETE, 2, R.string.delete_task);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Uri taskUri = ContentUris.withAppendedId(Tasks.CONTENT_URI, info.id);

        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_MARK_COMPLETED:
                ContentValues values = new ContentValues();
                values.put(Tasks.COMPLETED, true);
                getContentResolver().update(taskUri, values, null, null);
                return true;
            case CONTEXT_MENU_ITEM_MODIFY:
                Intent i = new Intent(this, TaskEdit.class);
                i.setData(taskUri);
                startActivity(i);
                return true;
            case CONTEXT_MENU_ITEM_DELETE:
                getContentResolver().delete(taskUri, null, null);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}