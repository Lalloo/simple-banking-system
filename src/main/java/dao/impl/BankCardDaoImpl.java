package dao.impl;

import dao.BankCardDAO;
import domain.BankCard;
import util.DataBaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class BankCardDaoImpl implements BankCardDAO {
    private final String initTable = "CREATE TABLE IF NOT EXISTS card(" +
            "id INTEGER PRIMARY KEY, " + // todo id has autoincrement
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


    public BankCardDaoImpl() {
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
                        connection.commit();
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
    public void save(BankCard card) {
        // if new -> INSERT, else UPDATE
        try (Connection connection = DataBaseUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addCard)) {
            preparedStatement.setString(1, card.getCardId());
            preparedStatement.setString(2, card.getCardPin());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCardFromDataBase(String cardId) {
        try (Connection connection = DataBaseUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
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
    public List<BankCard> getAll() {
        List<BankCard> bankCards = new ArrayList<>();
        try (Connection connection = DataBaseUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectAllDataBaseTable);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                bankCards.add(new BankCard(
                        resultSet.getInt("id"),
                        resultSet.getString("number"),
                        resultSet.getString("pin"),
                        resultSet.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankCards;
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
