package carvajal.autenticador.android.dal.greendao.read;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import carvajal.autenticador.android.dal.greendao.read.Templates;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TEMPLATES.
*/
public class TemplatesDao extends AbstractDao<Templates, String> {

    public static final String TABLENAME = "TEMPLATES";

    /**
     * Properties of entity Templates.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Cedula = new Property(0, String.class, "Cedula", true, "CEDULA");
        public final static Property Template1 = new Property(1, byte[].class, "Template1", false, "TEMPLATE1");
        public final static Property Template2 = new Property(2, byte[].class, "Template2", false, "TEMPLATE2");
        public final static Property Template3 = new Property(3, byte[].class, "Template3", false, "TEMPLATE3");
        public final static Property Template4 = new Property(4, byte[].class, "Template4", false, "TEMPLATE4");
        public final static Property Template5 = new Property(5, byte[].class, "Template5", false, "TEMPLATE5");
        public final static Property Template6 = new Property(6, byte[].class, "Template6", false, "TEMPLATE6");
        public final static Property Template7 = new Property(7, byte[].class, "Template7", false, "TEMPLATE7");
        public final static Property Template8 = new Property(8, byte[].class, "Template8", false, "TEMPLATE8");
        public final static Property Template9 = new Property(9, byte[].class, "Template9", false, "TEMPLATE9");
        public final static Property Template10 = new Property(10, byte[].class, "Template10", false, "TEMPLATE10");
    };


    public TemplatesDao(DaoConfig config) {
        super(config);
    }
    
    public TemplatesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TEMPLATES' (" + //
                "'CEDULA' TEXT PRIMARY KEY NOT NULL ," + // 0: Cedula
                "'TEMPLATE1' BLOB," + // 1: Template1
                "'TEMPLATE2' BLOB," + // 2: Template2
                "'TEMPLATE3' BLOB," + // 3: Template3
                "'TEMPLATE4' BLOB," + // 4: Template4
                "'TEMPLATE5' BLOB," + // 5: Template5
                "'TEMPLATE6' BLOB," + // 6: Template6
                "'TEMPLATE7' BLOB," + // 7: Template7
                "'TEMPLATE8' BLOB," + // 8: Template8
                "'TEMPLATE9' BLOB," + // 9: Template9
                "'TEMPLATE10' BLOB);"); // 10: Template10
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TEMPLATES'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Templates entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getCedula());
 
        byte[] Template1 = entity.getTemplate1();
        if (Template1 != null) {
            stmt.bindBlob(2, Template1);
        }
 
        byte[] Template2 = entity.getTemplate2();
        if (Template2 != null) {
            stmt.bindBlob(3, Template2);
        }
 
        byte[] Template3 = entity.getTemplate3();
        if (Template3 != null) {
            stmt.bindBlob(4, Template3);
        }
 
        byte[] Template4 = entity.getTemplate4();
        if (Template4 != null) {
            stmt.bindBlob(5, Template4);
        }
 
        byte[] Template5 = entity.getTemplate5();
        if (Template5 != null) {
            stmt.bindBlob(6, Template5);
        }
 
        byte[] Template6 = entity.getTemplate6();
        if (Template6 != null) {
            stmt.bindBlob(7, Template6);
        }
 
        byte[] Template7 = entity.getTemplate7();
        if (Template7 != null) {
            stmt.bindBlob(8, Template7);
        }
 
        byte[] Template8 = entity.getTemplate8();
        if (Template8 != null) {
            stmt.bindBlob(9, Template8);
        }
 
        byte[] Template9 = entity.getTemplate9();
        if (Template9 != null) {
            stmt.bindBlob(10, Template9);
        }
 
        byte[] Template10 = entity.getTemplate10();
        if (Template10 != null) {
            stmt.bindBlob(11, Template10);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Templates readEntity(Cursor cursor, int offset) {
        Templates entity = new Templates( //
            cursor.getString(offset + 0), // Cedula
            cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1), // Template1
            cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2), // Template2
            cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3), // Template3
            cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4), // Template4
            cursor.isNull(offset + 5) ? null : cursor.getBlob(offset + 5), // Template5
            cursor.isNull(offset + 6) ? null : cursor.getBlob(offset + 6), // Template6
            cursor.isNull(offset + 7) ? null : cursor.getBlob(offset + 7), // Template7
            cursor.isNull(offset + 8) ? null : cursor.getBlob(offset + 8), // Template8
            cursor.isNull(offset + 9) ? null : cursor.getBlob(offset + 9), // Template9
            cursor.isNull(offset + 10) ? null : cursor.getBlob(offset + 10) // Template10
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Templates entity, int offset) {
        entity.setCedula(cursor.getString(offset + 0));
        entity.setTemplate1(cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1));
        entity.setTemplate2(cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2));
        entity.setTemplate3(cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3));
        entity.setTemplate4(cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4));
        entity.setTemplate5(cursor.isNull(offset + 5) ? null : cursor.getBlob(offset + 5));
        entity.setTemplate6(cursor.isNull(offset + 6) ? null : cursor.getBlob(offset + 6));
        entity.setTemplate7(cursor.isNull(offset + 7) ? null : cursor.getBlob(offset + 7));
        entity.setTemplate8(cursor.isNull(offset + 8) ? null : cursor.getBlob(offset + 8));
        entity.setTemplate9(cursor.isNull(offset + 9) ? null : cursor.getBlob(offset + 9));
        entity.setTemplate10(cursor.isNull(offset + 10) ? null : cursor.getBlob(offset + 10));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Templates entity, long rowId) {
        return entity.getCedula();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Templates entity) {
        if(entity != null) {
            return entity.getCedula();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}