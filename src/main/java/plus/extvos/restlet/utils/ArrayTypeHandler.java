package plus.extvos.restlet.utils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;

import java.sql.*;

/**
 * @author shenmc
 */
public class ArrayTypeHandler extends BaseTypeHandler<Object[]> {
    private static final String TYPE_NAME_VARCHAR = "varchar";
    private static final String TYPE_NAME_INTEGER = "integer";
    private static final String TYPE_NAME_BOOLEAN = "boolean";
    private static final String TYPE_NAME_NUMERIC = "numeric";

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object[] ts, JdbcType jdbcType) throws SQLException {
        String typeName = null;
        if (ts instanceof Integer[]) {
            typeName = TYPE_NAME_INTEGER;
        } else if (ts instanceof String[]) {
            typeName = TYPE_NAME_VARCHAR;
        } else if (ts instanceof Boolean[]) {
            typeName = TYPE_NAME_BOOLEAN;
        } else if (ts instanceof Double[]) {
            typeName = TYPE_NAME_NUMERIC;
        }

        if (typeName == null) {
            throw new TypeException("ArrayTypeHandler parameter typeName error, your type is " + ts.getClass().getName());
        }

        // 这3行是关键的代码，创建Array，然后ps.setArray(i, array)就可以了
        Connection conn = preparedStatement.getConnection();
        Array array = conn.createArrayOf(typeName, ts);
        preparedStatement.setArray(i, array);
    }

    @Override
    public Object[] getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getArray(resultSet.getArray(s));
    }

    @Override
    public Object[] getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getArray(resultSet.getArray(i));
    }

    @Override
    public Object[] getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getArray(callableStatement.getArray(i));
    }

    private Object[] getArray(Array array) {
        if (array == null) {
            return null;
        }
        try {
            return (Object[]) array.getArray();
        } catch (Exception ignored) {
        }
        return null;
    }
}
