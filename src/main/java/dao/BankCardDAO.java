package dao;

import domain.BankCard;

import java.util.List;

public interface BankCardDAO {
    void deleteCardFromDataBase(String cardId);

    int getBalance(String loginCard);

    void addIncome(String cardId, int income);

    boolean doTransfer(String loginCardId, String transferCardId, int transferMoney);

    boolean checkLoginCardInDataBase(String loginCardId, String loginPin);

    boolean checkTransferCardNumberInDataBase(String transferCard);

    void save(BankCard card);

    List<BankCard> getAll();

    void deleteDataBaseTable();
}
