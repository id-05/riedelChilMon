package sample;

import java.sql.*;

public interface DAO {

    default void saveStrParam(String name, String value){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE param SET valueStr = ? WHERE name=?");
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    default void saveIntParam(String name, Integer value){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE param SET valueInt = ? WHERE name=?");
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    default Integer getIntParam(String name){
        Integer result = 0;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement prepared = connection.prepareStatement("SELECT name, valueInt FROM param WHERE name = ?;");
            prepared.setString(1, name);
            ResultSet rs = prepared.executeQuery();
            result = rs.getInt(2);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    default String getStrParam(String name){
        String result = null;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement prepared = connection.prepareStatement("SELECT name, valueStr FROM param WHERE name = ?;");
            prepared.setString(1, name);
            ResultSet rs = prepared.executeQuery();
            result = rs.getString(2);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
