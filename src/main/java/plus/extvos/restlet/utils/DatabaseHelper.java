package plus.extvos.restlet.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shenmc
 */
public class DatabaseHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseHelper.class);
    public static final String MySQL = "MySQL";
    public static final String PostgreSQL = "PostgreSQL";


    private DataSource dataSource;
    private Connection connection;
    private DatabaseMetaData metaData;

    private DatabaseHelper() {

    }

    public boolean isMySQL() {
        try {
            return metaData.getDatabaseProductName().equals(MySQL);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean isPostgreSQL() {
        try {
            return metaData.getDatabaseProductName().equals(PostgreSQL);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public String[] getTableAndView() {
        List<String> tableNames = new LinkedList<>();
        try {
            ResultSet ts = metaData.getTables(null, isPostgreSQL() ? "public" : "", null,
                    new String[]{"TABLE", "VIEW"});
            while (ts.next()) {
                String tableName = ts.getString("TABLE_NAME");
                log.debug(ts.getString("TABLE_NAME") + "  "
                        + ts.getString("TABLE_TYPE"));
                tableNames.add(tableName);
            }
        } catch (SQLException e) {
            log.error(">>", e);

        }
        return tableNames.toArray(new String[0]);
    }

    public String[] getTables() {
        List<String> tableNames = new LinkedList<>();
        try {
            ResultSet ts = metaData.getTables(null, isPostgreSQL() ? "public" : "", null,
                    new String[]{"TABLE"});
            while (ts.next()) {
                String tableName = ts.getString("TABLE_NAME");
                log.debug(ts.getString("TABLE_NAME") + "  "
                        + ts.getString("TABLE_TYPE"));
                tableNames.add(tableName);
            }
        } catch (SQLException e) {
            log.error(">>", e);

        }
        return tableNames.toArray(new String[0]);
    }

    public String[] getViews() {
        List<String> viewNames = new LinkedList<>();
        try {
            ResultSet ts = metaData.getTables(null, isPostgreSQL() ? "public" : "", null,
                    new String[]{"VIEW"});
            while (ts.next()) {
                String viewName = ts.getString("TABLE_NAME");
                log.debug(ts.getString("TABLE_NAME") + "  "
                        + ts.getString("TABLE_TYPE"));
                viewNames.add(viewName);
            }
        } catch (SQLException e) {
            log.error(">>", e);

        }
        return viewNames.toArray(new String[0]);
    }

    public int tableAbsent(String... tables) {
        if (tables.length < 1) {
            return 0;
        }
        int n = 0;
        Map<String, Boolean> tableExists = Arrays.stream(getTableAndView()).collect(Collectors.toMap(s -> s, b -> false, (o, v) -> v, LinkedHashMap::new));
        for (String s : tables) {
            if (!tableExists.containsKey(s)) {
                n += 1;
            }
        }
        return n;
    }

    public int tablePresent(String... tables) {
        if (tables.length < 1) {
            return 0;
        }
        int n = 0;
        Map<String, Boolean> tableExists = Arrays.stream(getTableAndView()).collect(Collectors.toMap(s -> s, b -> false, (o, v) -> v, LinkedHashMap::new));
        for (String s : tables) {
            if (tableExists.containsKey(s)) {
                n += 1;
            }
        }
        return n;
    }

    public void runScripts(String... files) throws SQLException, IOException {
        ScriptRunner runner = new ScriptRunner(connection);
        try {
            runner.setLogWriter(dataSource.getLogWriter());
        }catch(Exception ignore){

        }
        for (String path : files) {
            Reader reader = Resources.getResourceAsReader(path);
            //执行SQL脚本
            runner.runScript(reader);
            //关闭文件输入流
            reader.close();
        }
    }

    public void runScriptsIfMySQL(String... files) throws SQLException, IOException {
        if (isMySQL()) {
            runScripts(files);
        }
    }

    public void runScriptsIfPostgreSQL(String... files) throws SQLException, IOException {
        if (isPostgreSQL()) {
            runScripts(files);
        }
    }

    public static DatabaseHelper with(DataSource ds) throws SQLException {
        log.debug(">>>> with: ds :: {}", ds.getConnection().getMetaData().getDatabaseProductName());
        DatabaseHelper dh = new DatabaseHelper();
        dh.dataSource = ds;
        dh.connection = ds.getConnection();
        dh.metaData = dh.connection.getMetaData();

        return dh;
    }
}
