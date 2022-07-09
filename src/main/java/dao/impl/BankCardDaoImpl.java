package dao.impl;

import dao.BankCardsDAO;
import domain.BankCard;
import util.DataBaseUtil;

import javax.swing.text.html.Option;
import java.sql.*;
import java.util.Optional;

public class BankCardDaoImpl implements BankCardsDAO {
    private final String initTable = "CREATE TABLE IF NOT EXISTS card(" +
            "id INTEGER PRIMARY KEY, " +
            "number TEXT NOT NULL, " +
            "pin TEXT NOT NULL, " +
            "balance INTEGER DEFAULT 0" +
            ");";

    private final String delete = "DELETE FROM card WHERE number = (?);";

    private final String get = "SELECT * FROM card WHERE number = (?);";

    private final String updateBalance = "UPDATE card SET balance = (balance + ?) WHERE number = ?;";

    private final String updateCard = "UPDATE card SET balance = ? WHERE number = ?;";

    private final String addCard = "INSERT INTO card(number, pin, balance) VALUES (?,?,?);";


    public BankCardDaoImpl() {
        createTable();
    }

    private void createTable() {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(initTable)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(BankCard card, int income) {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateBalance)) {
            preparedStatement.setInt(1, income);
            preparedStatement.setString(2, card.getCardId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean doTransfer(BankCard card, String transferCardId, int transferMoney) {
        try (Connection connection = DataBaseUtil.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            try (PreparedStatement statement = connection.prepareStatement(updateBalance)) {
                statement.setInt(1, -transferMoney);
                statement.setString(2, card.getCardId());
                statement.executeUpdate();

                statement.setInt(1, transferMoney);
                statement.setString(2, transferCardId);
                statement.executeUpdate();
            } catch (SQLException e) {
                connection.rollback(savepoint);
                return false;
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void save(BankCard card) {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addCard)) {
            preparedStatement.setString(1, card.getCardId());
            preparedStatement.setString(2, card.getCardPin());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<BankCard> get(String loginCard) {
        BankCard card = null;
        try (Connection connection = DataBaseUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(get);
            preparedStatement.setString(1, loginCard);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    card = new BankCard(
                            resultSet.getString("number"),
                            resultSet.getString("pin"),
                            resultSet.getInt("balance")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(card);
    }

    @Override
    public void delete(BankCard card) {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
            preparedStatement.setString(1, card.getCardId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
