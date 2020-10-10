import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dao {
    private static Connection getConnection() throws NamingException, SQLException {
        Connection conn = null;
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/cloudDisk");
        conn = ds.getConnection();
        return conn;
    }

    public static boolean exists(String s) {
        return query(s) != null;
    }

    public static String query(String s) {
        String ans = null;
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(s); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                ans = rs.getString(1);
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static boolean update(String s) {
        int result = 0;
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(s)) {
            result = pstmt.executeUpdate();
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return result > 0;
    }
}
