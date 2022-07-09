package dao.impl;

import dao.BankCardsDAO;
import util.DataBaseUtils;

import java.sql.*;
import java.util.Objects;

public class BankCardsDAOimpl implements BankCardsDAO {
    private final String initTable = "CREATE TABLE IF NOT EXISTS card(" +
            "id INTEGER PRIMARY KEY, " +
            "number TEXT NOT NULL, " +
            "pin TEXT NOT NULL, " +
            "balance INTEGER DEFAULT 0" +
            ");";
    private final String delete = "DELETE FROM card WHERE number = (?);";
    private final String getBalance = "SELECT number, balance FROM card;";
    private final String updateBalance = "UPDATE card SET balance = (balance + ?) WHERE number = ?;";
    private final String takeNumber = "SELECT number FROM card;";
    private final String addCard = "INSERT INTO card(number, pin) VALUES (?,?);";
    private final String takeLogin = "SELECT number, pin FROM card";
    private final String selectAllDataBaseTable = "SELECT * FROM card;";


    public BankCardsDAOimpl() {
        createTable();
    }

    private void createTable() {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(initTable);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBalance(String loginCard) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(getBalance);
            ResultSet card = preparedStatement.executeQuery();
            while (card.next()) {
                String number = card.getString("number");
                int balance = card.getInt("balance");
                if (Objects.equals(loginCard, number)) {
                    return balance;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void addIncome(String cardId, int income) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(updateBalance);
            preparedStatement.setInt(1, income);
            preparedStatement.setString(2, cardId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean doTransfer(String loginCardId, String transferCardId, int transferMoney) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            PreparedStatement preparedStatement1 = connection.prepareStatement(getBalance);
            ResultSet card = preparedStatement1.executeQuery();
            while (card.next()) {
                String number = card.getString("number");
                int balance = card.getInt("balance");
                if (Objects.equals(loginCardId, number) && transferMoney <= balance) {
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateBalance)) {
                        preparedStatement2.setInt(1, -transferMoney);
                        preparedStatement2.setString(2, loginCardId);
                        preparedStatement2.executeUpdate();

                        preparedStatement2.setInt(1, transferMoney);
                        preparedStatement2.setString(2, transferCardId);
                        preparedStatement2.executeUpdate();
                        return true;
                    } catch (SQLException e ) {
                        connection.rollback(savepoint);
                        return  false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addCardToDataBase(String cardId, String cardPin) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(addCard);
            preparedStatement.setString(1, cardId);
            preparedStatement.setString(2, cardPin);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCardFromDataBase(String cardId) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setString(1, cardId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //хуй его знает пока
    @Override
    public boolean checkLoginCardInDataBase(String loginCard, String loginPin) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(takeLogin);
            ResultSet card = preparedStatement.executeQuery();
            while (card.next()) {
                String number = card.getString("number");
                String pin = card.getString("pin");
                if (Objects.equals(loginCard, number) && Objects.equals(loginPin, pin)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean checkTransferCardNumberInDataBase(String transferCard) {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(takeNumber);
            ResultSet card = preparedStatement.executeQuery();
            while (card.next()) {
                String number = card.getString("number");
                if (Objects.equals(transferCard, number)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void printDataBaseTable() {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(selectAllDataBaseTable);
            ResultSet card = preparedStatement.executeQuery();
            while (card.next()) {
                int i = card.getInt("id");
                String cardNumber = card.getString("number");
                String cardPin = card.getString("pin");
                int balance = card.getInt("balance");
                System.out.println("\ncard number " + i);
                System.out.println("id: " + i);
                System.out.println("cardNumber: " + cardNumber);
                System.out.println("cardPin: " + cardPin);
                System.out.println("cardBalance: " + balance + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDataBaseTable() {
        try (Connection connection = DataBaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(selectAllDataBaseTable);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
