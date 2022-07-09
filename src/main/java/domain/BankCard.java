package domain;

import util.LuhnAlgorithm;

import java.util.Random;
import java.util.stream.Stream;

public class BankCard {
    private final String cardId;
    private final String cardPin;
    private final Random RANDOM = new Random();

    public BankCard() {
        cardId = generateCardId();
        cardPin = generatePin();
    }

    public String getCardId() {
        return cardId;
    }

    public String getCardPin() {
        return cardPin;
    }

    private String generateCardId() {
        StringBuilder number = new StringBuilder("400000");
        Stream.generate(() -> RANDOM.nextInt(10))
                .limit(9)
                .forEach(number::append);
        number.append(LuhnAlgorithm.generateCheckSum(number));
        return number.toString();
    }

    //сделано
    private String generatePin() {
        StringBuilder pin = new StringBuilder();
        Stream.generate(() -> RANDOM.nextInt(10))
                .limit(4)
                .forEach(pin::append);
        return pin.toString();
    }

}
