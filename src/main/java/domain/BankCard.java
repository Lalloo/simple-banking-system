package domain;


import util.LuhnAlgorithm;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BankCard {
    private long balance = 0;
    private static final Random RANDOM = new Random();
    private static final Supplier<Integer> randomInt = () -> RANDOM.nextInt(10);

    private final String cardId;
    private final String cardPin;
    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void addIncome(long income) {
        this.balance += income;
    }

    public BankCard() {
        cardId = generateCardId();
        cardPin = generatePin();
    }

    public BankCard(String cardId, String cardPin, int balance) {
        this.cardId = cardId;
        this.cardPin = cardPin;
        this.balance = balance;
    }

    public String getCardId() {
        return cardId;
    }

    public String getCardPin() {
        return cardPin;
    }

    private String generateCardId() {
        StringBuilder number = new StringBuilder("400000");
        Stream.generate(randomInt)
                .limit(9)
                .forEach(number::append);
        number.append(LuhnAlgorithm.generateCheckSum(number));
        return LuhnAlgorithm.checkNumberValid(number.toString()) ?
                number.toString() :
                generateCardId();
    }

    private String generatePin() {
        StringBuilder pin = new StringBuilder();
        Stream.generate(randomInt)
                .limit(4)
                .forEach(pin::append);
        return pin.toString();
    }

    public void checkMoneyToTransfer(int transferMoney) {
        if (balance - transferMoney < 0)
            throw new IllegalStateException("Not enough money!");
    }
}
