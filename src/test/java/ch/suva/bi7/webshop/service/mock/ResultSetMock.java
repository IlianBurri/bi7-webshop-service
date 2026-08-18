package ch.suva.bi7.webshop.service.mock;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ResultSetMock implements ResultSet {

    private int index = -1;
    private final List<Map<String, Object>> result;

    public ResultSetMock(List<Map<String, Object>> result) {
        this.result = result;
    }

    @Override
    public boolean next() throws SQLException {
        index++;
        return index < result.size();
    }

    @Override
    public String getString(String s) throws SQLException {
        Map<String, Object> map = result.get(index);
        if (map != null) {
            Object value = map.get(s);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    @Override
    public int getInt(String s) throws SQLException {
        Map<String, Object> map = result.get(index);
        if (map != null && map.get(s) != null) {
            return new BigDecimal(map.get(s).toString()).intValue();
        }
        return 0;
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        Map<String, Object> map = result.get(index);
        if (map != null && map.get(s) != null) {
            return new BigDecimal(map.get(s).toString());
        }
        return null;
    }

    @Override
    public void close() throws SQLException {
    }

    private UnsupportedOperationException notImplemented(String method) {
        return new UnsupportedOperationException("Not implemented: " + method);
    }

    @Override
    public boolean wasNull() throws SQLException {
        throw notImplemented("wasNull()");
    }

    @Override
    public String getString(int i) throws SQLException {
        throw notImplemented("getString(int)");
    }

    @Override
    public boolean getBoolean(int i) throws SQLException {
        throw notImplemented("getBoolean(int)");
    }

    @Override
    public byte getByte(int i) throws SQLException {
        throw notImplemented("getByte(int)");
    }

    @Override
    public short getShort(int i) throws SQLException {
        throw notImplemented("getShort(int)");
    }

    @Override
    public int getInt(int i) throws SQLException {
        throw notImplemented("getInt(int)");
    }

    @Override
    public long getLong(int i) throws SQLException {
        throw notImplemented("getLong(int)");
    }

    @Override
    public float getFloat(int i) throws SQLException {
        throw notImplemented("getFloat(int)");
    }

    @Override
    public double getDouble(int i) throws SQLException {
        throw notImplemented("getDouble(int)");
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        throw notImplemented("getBigDecimal(int, int)");
    }

    @Override
    public byte[] getBytes(int i) throws SQLException {
        throw notImplemented("getBytes(int)");
    }

    @Override
    public Date getDate(int i) throws SQLException {
        throw notImplemented("getDate(int)");
    }

    @Override
    public Time getTime(int i) throws SQLException {
        throw notImplemented("getTime(int)");
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        throw notImplemented("getTimestamp(int)");
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        throw notImplemented("getAsciiStream(int)");
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        throw notImplemented("getUnicodeStream(int)");
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        throw notImplemented("getBinaryStream(int)");
    }

    @Override
    public boolean getBoolean(String s) throws SQLException {
        throw notImplemented("getBoolean(String)");
    }

    @Override
    public byte getByte(String s) throws SQLException {
        throw notImplemented("getByte(String)");
    }

    @Override
    public short getShort(String s) throws SQLException {
        throw notImplemented("getShort(String)");
    }

    @Override
    public long getLong(String s) throws SQLException {
        throw notImplemented("getLong(String)");
    }

    @Override
    public float getFloat(String s) throws SQLException {
        throw notImplemented("getFloat(String)");
    }

    @Override
    public double getDouble(String s) throws SQLException {
        throw notImplemented("getDouble(String)");
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        throw notImplemented("getBigDecimal(String, int)");
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        throw notImplemented("getBytes(String)");
    }

    @Override
    public Date getDate(String s) throws SQLException {
        throw notImplemented("getDate(String)");
    }

    @Override
    public Time getTime(String s) throws SQLException {
        throw notImplemented("getTime(String)");
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        throw notImplemented("getTimestamp(String)");
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        throw notImplemented("getAsciiStream(String)");
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        throw notImplemented("getUnicodeStream(String)");
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        throw notImplemented("getBinaryStream(String)");
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw notImplemented("getWarnings()");
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw notImplemented("clearWarnings()");
    }

    @Override
    public String getCursorName() throws SQLException {
        throw notImplemented("getCursorName()");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw notImplemented("getMetaData()");
    }

    @Override
    public Object getObject(int i) throws SQLException {
        throw notImplemented("getObject(int)");
    }

    @Override
    public Object getObject(String s) throws SQLException {
        throw notImplemented("getObject(String)");
    }

    @Override
    public int findColumn(String s) throws SQLException {
        throw notImplemented("findColumn(String)");
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        throw notImplemented("getCharacterStream(int)");
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        throw notImplemented("getCharacterStream(String)");
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        throw notImplemented("getBigDecimal(int)");
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw notImplemented("isBeforeFirst()");
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw notImplemented("isAfterLast()");
    }

    @Override
    public boolean isFirst() throws SQLException {
        throw notImplemented("isFirst()");
    }

    @Override
    public boolean isLast() throws SQLException {
        throw notImplemented("isLast()");
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw notImplemented("beforeFirst()");
    }

    @Override
    public void afterLast() throws SQLException {
        throw notImplemented("afterLast()");
    }

    @Override
    public boolean first() throws SQLException {
        throw notImplemented("first()");
    }

    @Override
    public boolean last() throws SQLException {
        throw notImplemented("last()");
    }

    @Override
    public int getRow() throws SQLException {
        throw notImplemented("getRow()");
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        throw notImplemented("absolute(int)");
    }

    @Override
    public boolean relative(int i) throws SQLException {
        throw notImplemented("relative(int)");
    }

    @Override
    public boolean previous() throws SQLException {
        throw notImplemented("previous()");
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        throw notImplemented("setFetchDirection(int)");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw notImplemented("getFetchDirection()");
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        throw notImplemented("setFetchSize(int)");
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw notImplemented("getFetchSize()");
    }

    @Override
    public int getType() throws SQLException {
        throw notImplemented("getType()");
    }

    @Override
    public int getConcurrency() throws SQLException {
        throw notImplemented("getConcurrency()");
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw notImplemented("rowUpdated()");
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw notImplemented("rowInserted()");
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw notImplemented("rowDeleted()");
    }

    @Override
    public void updateNull(int i) throws SQLException {
        throw notImplemented("updateNull(int)");
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        throw notImplemented("updateBoolean(int, boolean)");
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        throw notImplemented("updateByte(int, byte)");
    }

    @Override
    public void updateShort(int i, short i1) throws SQLException {
        throw notImplemented("updateShort(int, short)");
    }

    @Override
    public void updateInt(int i, int i1) throws SQLException {
        throw notImplemented("updateInt(int, int)");
    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        throw notImplemented("updateLong(int, long)");
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        throw notImplemented("updateFloat(int, float)");
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        throw notImplemented("updateDouble(int, double)");
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        throw notImplemented("updateBigDecimal(int, BigDecimal)");
    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        throw notImplemented("updateString(int, String)");
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        throw notImplemented("updateBytes(int, byte[])");
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        throw notImplemented("updateDate(int, Date)");
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        throw notImplemented("updateTime(int, Time)");
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        throw notImplemented("updateTimestamp(int, Timestamp)");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        throw notImplemented("updateAsciiStream(int, InputStream, int)");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        throw notImplemented("updateBinaryStream(int, InputStream, int)");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        throw notImplemented("updateCharacterStream(int, Reader, int)");
    }

    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {
        throw notImplemented("updateObject(int, Object, int)");
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        throw notImplemented("updateObject(int, Object)");
    }

    @Override
    public void updateNull(String s) throws SQLException {
        throw notImplemented("updateNull(String)");
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        throw notImplemented("updateBoolean(String, boolean)");
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        throw notImplemented("updateByte(String, byte)");
    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        throw notImplemented("updateShort(String, short)");
    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        throw notImplemented("updateInt(String, int)");
    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        throw notImplemented("updateLong(String, long)");
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        throw notImplemented("updateFloat(String, float)");
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        throw notImplemented("updateDouble(String, double)");
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        throw notImplemented("updateBigDecimal(String, BigDecimal)");
    }

    @Override
    public void updateString(String s, String s1) throws SQLException {
        throw notImplemented("updateString(String, String)");
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        throw notImplemented("updateBytes(String, byte[])");
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        throw notImplemented("updateDate(String, Date)");
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        throw notImplemented("updateTime(String, Time)");
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        throw notImplemented("updateTimestamp(String, Timestamp)");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        throw notImplemented("updateAsciiStream(String, InputStream, int)");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        throw notImplemented("updateBinaryStream(String, InputStream, int)");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        throw notImplemented("updateCharacterStream(String, Reader, int)");
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        throw notImplemented("updateObject(String, Object, int)");
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        throw notImplemented("updateObject(String, Object)");
    }

    @Override
    public void insertRow() throws SQLException {
        throw notImplemented("insertRow()");
    }

    @Override
    public void updateRow() throws SQLException {
        throw notImplemented("updateRow()");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw notImplemented("deleteRow()");
    }

    @Override
    public void refreshRow() throws SQLException {
        throw notImplemented("refreshRow()");
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw notImplemented("cancelRowUpdates()");
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw notImplemented("moveToInsertRow()");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw notImplemented("moveToCurrentRow()");
    }

    @Override
    public Statement getStatement() throws SQLException {
        throw notImplemented("getStatement()");
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        throw notImplemented("getObject(int, Map)");
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        throw notImplemented("getRef(int)");
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        throw notImplemented("getBlob(int)");
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        throw notImplemented("getClob(int)");
    }

    @Override
    public Array getArray(int i) throws SQLException {
        throw notImplemented("getArray(int)");
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        throw notImplemented("getObject(String, Map)");
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        throw notImplemented("getRef(String)");
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        throw notImplemented("getBlob(String)");
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        throw notImplemented("getClob(String)");
    }

    @Override
    public Array getArray(String s) throws SQLException {
        throw notImplemented("getArray(String)");
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        throw notImplemented("getDate(int, Calendar)");
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        throw notImplemented("getDate(String, Calendar)");
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        throw notImplemented("getTime(int, Calendar)");
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        throw notImplemented("getTime(String, Calendar)");
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        throw notImplemented("getTimestamp(int, Calendar)");
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        throw notImplemented("getTimestamp(String, Calendar)");
    }

    @Override
    public URL getURL(int i) throws SQLException {
        throw notImplemented("getURL(int)");
    }

    @Override
    public URL getURL(String s) throws SQLException {
        throw notImplemented("getURL(String)");
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        throw notImplemented("updateRef(int, Ref)");
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        throw notImplemented("updateRef(String, Ref)");
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        throw notImplemented("updateBlob(int, Blob)");
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        throw notImplemented("updateBlob(String, Blob)");
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        throw notImplemented("updateClob(int, Clob)");
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        throw notImplemented("updateClob(String, Clob)");
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        throw notImplemented("updateArray(int, Array)");
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        throw notImplemented("updateArray(String, Array)");
    }

    @Override
    public RowId getRowId(int i) throws SQLException {
        throw notImplemented("getRowId(int)");
    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        throw notImplemented("getRowId(String)");
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        throw notImplemented("updateRowId(int, RowId)");
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        throw notImplemented("updateRowId(String, RowId)");
    }

    @Override
    public int getHoldability() throws SQLException {
        throw notImplemented("getHoldability()");
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw notImplemented("isClosed()");
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        throw notImplemented("updateNString(int, String)");
    }

    @Override
    public void updateNString(String s, String s1) throws SQLException {
        throw notImplemented("updateNString(String, String)");
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        throw notImplemented("updateNClob(int, NClob)");
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        throw notImplemented("updateNClob(String, NClob)");
    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        throw notImplemented("getNClob(int)");
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        throw notImplemented("getNClob(String)");
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        throw notImplemented("getSQLXML(int)");
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        throw notImplemented("getSQLXML(String)");
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        throw notImplemented("updateSQLXML(int, SQLXML)");
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        throw notImplemented("updateSQLXML(String, SQLXML)");
    }

    @Override
    public String getNString(int i) throws SQLException {
        throw notImplemented("getNString(int)");
    }

    @Override
    public String getNString(String s) throws SQLException {
        throw notImplemented("getNString(String)");
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        throw notImplemented("getNCharacterStream(int)");
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        throw notImplemented("getNCharacterStream(String)");
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        throw notImplemented("updateNCharacterStream(int, Reader, long)");
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        throw notImplemented("updateNCharacterStream(String, Reader, long)");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        throw notImplemented("updateAsciiStream(int, InputStream, long)");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        throw notImplemented("updateBinaryStream(int, InputStream, long)");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        throw notImplemented("updateCharacterStream(int, Reader, long)");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        throw notImplemented("updateAsciiStream(String, InputStream, long)");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        throw notImplemented("updateBinaryStream(String, InputStream, long)");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        throw notImplemented("updateCharacterStream(String, Reader, long)");
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        throw notImplemented("updateBlob(int, InputStream, long)");
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        throw notImplemented("updateBlob(String, InputStream, long)");
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        throw notImplemented("updateClob(int, Reader, long)");
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        throw notImplemented("updateClob(String, Reader, long)");
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        throw notImplemented("updateNClob(int, Reader, long)");
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        throw notImplemented("updateNClob(String, Reader, long)");
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        throw notImplemented("updateNCharacterStream(int, Reader)");
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        throw notImplemented("updateNCharacterStream(String, Reader)");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        throw notImplemented("updateAsciiStream(int, InputStream)");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        throw notImplemented("updateBinaryStream(int, InputStream)");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        throw notImplemented("updateCharacterStream(int, Reader)");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        throw notImplemented("updateAsciiStream(String, InputStream)");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        throw notImplemented("updateBinaryStream(String, InputStream)");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        throw notImplemented("updateCharacterStream(String, Reader)");
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        throw notImplemented("updateBlob(int, InputStream)");
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        throw notImplemented("updateBlob(String, InputStream)");
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        throw notImplemented("updateClob(int, Reader)");
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        throw notImplemented("updateClob(String, Reader)");
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        throw notImplemented("updateNClob(int, Reader)");
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        throw notImplemented("updateNClob(String, Reader)");
    }

    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {
        throw notImplemented("getObject(int, Class)");
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {
        throw notImplemented("getObject(String, Class)");
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        throw notImplemented("unwrap(Class)");
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        throw notImplemented("isWrapperFor(Class)");
    }
}
