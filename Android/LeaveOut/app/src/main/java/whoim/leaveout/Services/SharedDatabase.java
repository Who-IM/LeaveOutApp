package whoim.leaveout.Services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kim on 2017-07-19.
 */

public class SharedDatabase extends SQLiteOpenHelper {

    public SharedDatabase(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Shared", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table fence (id integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void fenceInsert(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from fence where id = " + id, null);
        if (cursor.getCount() == 0) {
            db.execSQL("insert into fence values(" + id + ")");
        }
        db.close();
    }

    public boolean getFenceQuery(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from fence where id = " + id, null);
        return cursor.getCount() > 0;
    }
}
