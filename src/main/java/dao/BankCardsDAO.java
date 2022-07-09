package dao;

public interface BankCardsDAO {
    void deleteCardFromDataBase(String cardId);

    int getBalance(String loginCard);

    void addIncome(String cardId, int income);

    boolean doTransfer(String loginCardId, String transferCardId, int transferMoney);

    boolean checkLoginCardInDataBase(String loginCardId, String loginPin);

    boolean checkTransferCardNumberInDataBase(String transferCard);

    void addCardToDataBase(String cardId, String cardPin);

    void printDataBaseTable();

    void deleteDataBaseTable();
}
