package main.utilits;

import main.units.ChillerState;
import main.units.TelegramUser;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static main.Main.logging;

public interface DAO {

    default void saveNewUser(TelegramUser tUser){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO telegramuser (tid,name,filter) VALUES (?,?,?)");
            preparedStatement.setString(1, tUser.getId());
            preparedStatement.setString(2, tUser.getName());
            preparedStatement.setString(3, tUser.getFilter());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
    }

    default boolean validateUser(String userid) {
        Connection connection = null;
        String result;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement prepared = connection.prepareStatement("SELECT tid, name FROM telegramuser WHERE tid = ?;");
            prepared.setString(1, userid);
            ResultSet rs = prepared.executeQuery();
            result = rs.getString(1);
            statement.close();
            return result != null;
        } catch (SQLException e) {
            logging(e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
    }

    default TelegramUser getUserById(String userid){
        TelegramUser tUser = new TelegramUser();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement prepared = connection.prepareStatement("SELECT tid, name, filter FROM telegramuser WHERE tid = ?;");
            prepared.setString(1, userid);
            ResultSet rs = prepared.executeQuery();
            tUser = new TelegramUser(rs.getString(1),rs.getString(2),rs.getString(3));
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return tUser;
    }


    default List<TelegramUser> getAllTelegramUser(){
        List<TelegramUser> telegramUserList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            String selectQuery = "SELECT * FROM telegramuser";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                TelegramUser newUser = new TelegramUser(resultSet.getString("tid"),resultSet.getString("name"),
                        resultSet.getString("filter"));
                telegramUserList.add(newUser);
            }
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return telegramUserList;
    }

    default List<ChillerState> getAllRecords(){
        List<ChillerState> resultList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String selectQuery = "SELECT * FROM record";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                String data = resultSet.getString("rec");
                double code = resultSet.getDouble("timecode");
                System.out.println(code+" / "+data);
                resultList.add(new ChillerState(data));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return resultList;
    }

    default List<ChillerState> getRecordsBetween(String timeperiod){
        List<ChillerState> resultList = new ArrayList<>();
        double startTag = 0;
        double stopTag = 0;
        double dayOnSec = 86400000;
        Date date = new Date();
        switch (timeperiod){
            case "day":
                stopTag = date.getTime();
                startTag = stopTag - dayOnSec;
                break;

            case "week":
                stopTag = date.getTime();
                startTag = stopTag - 7*dayOnSec;
                break;

            case "mounth":
                stopTag = date.getTime();
                startTag = stopTag - 30*dayOnSec;
                break;

            case "all time":
                stopTag = date.getTime();
                startTag = 0;
                break;

            default: break;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM record WHERE (timecode) BETWEEN (?) AND (?)");
            preparedStatement.setDouble(1, startTag);
            preparedStatement.setDouble(2, stopTag);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String data = resultSet.getString("rec");
                resultList.add(new ChillerState(data));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return resultList;
    }

    default void saveStrParam(String name, String value){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE param SET valueStr = ? WHERE name = ?");
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
    }

    default void saveTeleramUserFilter(String tid, String newFilter){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE telegramuser SET filter = ? WHERE tid = ?");
            preparedStatement.setString(1, newFilter);
            preparedStatement.setString(2, tid);
            preparedStatement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
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
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
    }

    default Integer getIntParam(String name){
        int result = 0;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement prepared = connection.prepareStatement("SELECT name, valueInt FROM param WHERE name = ?");
            prepared.setString(1, name);
            ResultSet rs = prepared.executeQuery();
            result = rs.getInt(2);

            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return result;
    }

    default String getStrParam(String name){
        String result = "";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            PreparedStatement prepared = connection.prepareStatement("SELECT name, valueStr FROM param WHERE name = ?;");
            prepared.setString(1, name);
            ResultSet rs = prepared.executeQuery();
            result = rs.getString(2);
            if(result==null){
                prepared = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
                prepared.setString(1, name);
                prepared.setString(2, "");
                prepared.setInt(3, 1);
                prepared.executeUpdate();
            }
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return result;
    }

    default void saveState(String data, double timecode){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO record (rec, timecode) VALUES (?,?)");
            preparedStatement.setString(1, data);
            preparedStatement.setDouble(2, timecode);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
    }

    default String getLastChillerState(){
        String result = null;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            String selectQuery = "SELECT * FROM record ORDER BY rec DESC LIMIT 1;";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            result = resultSet.getString(2);
            statement.close();
        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
        return result;
    }

    default void ClearBase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String deleteSql = "DELETE FROM record";
            statement.executeUpdate(deleteSql);
            statement.close();

            statement = connection.createStatement();
            deleteSql = "DELETE FROM param";
            statement.executeUpdate(deleteSql);
            statement.close();

            statement = connection.createStatement();
            deleteSql = "DELETE FROM telegramuser";
            statement.executeUpdate(deleteSql);
            statement.close();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comnumber");
            preparedStatement.setString(2, "COM10");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "combaudrate");
            preparedStatement.setString(2, "57600");
            preparedStatement.setInt(3, 57600);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comparity");
            preparedStatement.setString(2, "none");
            preparedStatement.setInt(3, 0);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comdatabits");
            preparedStatement.setString(2, "8");
            preparedStatement.setInt(3, 8);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comstopbits");
            preparedStatement.setString(2, "1");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "bottoken");
            preparedStatement.setString(2, "1234567890");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "botpassword");
            preparedStatement.setString(2, "1234");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "timezone");
            preparedStatement.setString(2, "+00:00");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "levelTpi");
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 10);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "levelTpo");
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 10);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "levelTsi");
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 20);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "levelTso");
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 20);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logging(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logging(e.getMessage());
            }
        }
    }

}
