package util;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DataBaseUtils {
    private static final SQLiteDataSource DATA_SOURCE = new SQLiteDataSource();
    private static final String URL = "jdbc:sqlite:";

    private DataBaseUtils() {}

    public static void setURL(String[] args) {
        if (args.length > 1 && args[0].equals("-fileName")) {
            DataBaseUtils.setUrl(args[1]);
        } else {
            DataBaseUtils.setUrl("default.db");
        }
    }

    private static void setUrl(String fileName) {
        DATA_SOURCE.setUrl(URL + fileName);
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
