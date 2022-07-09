package domain;

import util.LuhnAlgorithm;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BankCard {
    private static final Random RANDOM = new Random();
    private static final Supplier<Integer> randomInt = () -> RANDOM.nextInt(10);
    private Integer id;
    private final String cardId;
    private final String cardPin;
    private double balance = 0;
    // todo добавить баланс

    // при создании новой карты
    public BankCard() {
        cardId = generateCardId();
        cardPin = generatePin();
    }

    public BankCard(Integer id, String cardId, String cardPin, double balance) {
        this.id = id;
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
        return number.toString();
    }

    private String generatePin() {
        StringBuilder pin = new StringBuilder();
        Stream.generate(randomInt)
                .limit(4)
                .forEach(pin::append);
        return pin.toString();
    }

    public double getBalance() {
        return this.balance;
    }
}
