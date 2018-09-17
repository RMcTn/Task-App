import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SQLiteDBConnection {
    private static final String TABLE_NAME = "tasks";
    private static final String DATABASE_FILE = "jdbc:sqlite:tasks.sqlite";
    private static final String SQL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n"
            + "	id integer PRIMARY KEY,\n"
            + "	message text NOT NULL,\n"
            + "	due_date DATETIME,\n"
            + " creation_date DATETIME,\n"
            + " has_notified bit,\n"
            + " is_completed bit"
            + ");";

    private Connection connection;

    public SQLiteDBConnection() throws SQLException {
            connection = DriverManager.getConnection(DATABASE_FILE);
            createTable();
    }

    public SQLiteDBConnection(String url) throws SQLException {
            connection = DriverManager.getConnection(url);
            createTable();
    }

    public void dropTable(boolean confirmation) {
        if (confirmation) {
            try {
                final String dropQuery = "DROP TABLE " + TABLE_NAME;
                Statement statement = connection.createStatement();
                statement.execute(dropQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.execute(SQL_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertTask(String message, Calendar dueDate, Calendar creationDate, boolean notified, boolean completed) throws SQLException {
        String query = "INSERT INTO tasks(message, due_date, creation_date, has_notified, is_completed) VALUES(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, message);
        java.sql.Date sqlDueDate = new java.sql.Date((dueDate).getTimeInMillis());
        preparedStatement.setDate(2, sqlDueDate);
        java.sql.Date sqlCreationDate = new java.sql.Date((creationDate).getTimeInMillis());
        preparedStatement.setDate(3, sqlCreationDate);
        preparedStatement.setBoolean(4, notified);
        preparedStatement.setBoolean(5, completed);
        preparedStatement.executeUpdate();
    }

    public void removeTask(Task task) throws SQLException {
        final String searchQuery = "DELETE FROM " + TABLE_NAME + " WHERE message = ? AND due_date = ?";

        //Convert date stored in task to a way SQLite can manage
        java.sql.Date sqlDate = new java.sql.Date(task.getTaskDate().getTimeInMillis());
        PreparedStatement preparedStatement = connection.prepareStatement(searchQuery);
        preparedStatement.setString(1, task.getMessage());
        preparedStatement.setDate(2, sqlDate);
        preparedStatement.executeUpdate();
    }

    //Loads a list of tasks from the database
    public List<Task> loadTasks() throws SQLException {
        final String searchQuery = "SELECT * FROM " + TABLE_NAME;
        List<Task> tasks = null;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(searchQuery);
        tasks = sqlDataToTask(resultSet);
        return tasks;
    }

    //Converts the data the database returns into a list of Tasks that are usable in the program
    private List<Task> sqlDataToTask(ResultSet resultSet) {
        List<Task> tasks = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                Calendar dueDate = Calendar.getInstance();
                dueDate.setTime(resultSet.getDate("due_date"));
                Calendar creationDate = Calendar.getInstance();
                creationDate.setTime(resultSet.getDate("creation_date"));
                boolean hasNotified = resultSet.getBoolean("has_notified");
                boolean isCompleted = resultSet.getBoolean("is_completed");
                Task task = new Task(message, dueDate, creationDate, hasNotified, isCompleted);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

}
