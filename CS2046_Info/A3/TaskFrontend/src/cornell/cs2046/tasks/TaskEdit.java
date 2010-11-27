package cornell.cs2046.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class TaskEdit extends Activity {
    private Cursor mCursor;
    private Uri mUri;
    private EditText mTitle;
    private CheckBox mCompleted;
    private EditText mDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_edit);

        mTitle = (EditText) findViewById(R.id.edit_title);
        mCompleted = (CheckBox) findViewById(R.id.edit_completed);
        mDetails = (EditText) findViewById(R.id.edit_details);

        mUri = getIntent().getData();

        String[] projection = new String[] {
                Tasks.TITLE,
                Tasks.COMPLETED,
                Tasks.DETAILS
        };

        mCursor = managedQuery(mUri, projection, null, null, null);

        ((Button) findViewById(R.id.save_task)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(Tasks.TITLE, mTitle.getText().toString());
                values.put(Tasks.COMPLETED, mCompleted.isChecked() ? 1 : 0);
                values.put(Tasks.DETAILS, mDetails.getText().toString());
                getContentResolver().update(mUri, values, null, null);
                finish();
            }
        });

        ((Button) findViewById(R.id.delete_task)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(mUri, null, null);
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCursor != null) {
            ContentValues values = new ContentValues();
            values.put(Tasks.TITLE, mTitle.getText().toString());
            values.put(Tasks.COMPLETED, mCompleted.isChecked() ? 1 : 0);
            values.put(Tasks.DETAILS, mDetails.getText().toString());
            getContentResolver().update(mUri, values, null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCursor != null) {
            mCursor.moveToFirst();

            mTitle.setText(mCursor.getString(0));
            mCompleted.setChecked(mCursor.getInt(1) == 1);
            mDetails.setText(mCursor.getString(2));
        }
    }
}
