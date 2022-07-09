package dao;

import domain.BankCard;

public interface BankCardsDAO {
    void delete(BankCard card);

    BankCard get(String loginCard);

    void update(BankCard card, int income);

    boolean doTransfer(BankCard card, String transferCardId, int transferMoney);

    void save(BankCard card);
}
