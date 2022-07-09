import dao.impl.BankCardDaoImpl;
import domain.BankCard;
import util.DataBaseUtils;
import util.LuhnAlgorithm;
import util.PrintMenuUtils;

import java.util.Objects;
import java.util.Scanner;

public class BankApp {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static BankCardDaoImpl bankCardDao;

    public BankApp(String[] args) {
        DataBaseUtils.setURL(args);
    }

    public void run() {
        bankCardDao = new BankCardDaoImpl();
        while (true) {
            // todo try-catch
            PrintMenuUtils.showLoginMenu();
            switch (SCANNER.nextInt()) {
                case 1:
                    createAnAccount();
                    break;
                case 2:
                    if (logIntoAccount()) {
                        return;
                    }
                    break;
                case 0:
                    PrintMenuUtils.bye();
                    return;
                default:
                    System.out.println("Please enter number 0-2!");
                    break;
            }
        }
    }

    private boolean logIntoAccount() {
        System.out.println("\nEnter your card number:");
        String loginCard = SCANNER.next();
        System.out.println("Enter your PIN:");
        String loginPin = SCANNER.next();
        // card = get(loginCard)
        // card.checkPin(loginPin)
        // return pin.equals(loginPin)
        if (!bankCardDao.checkLoginCardInDataBase(loginCard, loginPin)) {
            System.out.println("\nWrong card number or PIN!");
            return false;
        }
        System.out.println("\nYou have successfully logged in!");
        while (true) {
            PrintMenuUtils.showAccountMenu();
            switch (SCANNER.nextInt()) {
                case 1:
                    System.out.println("\nBalance: " + bankCardDao.getBalance(loginCard));
                    break;
                case 2:
                    addIncome(card);
                    break;
                case 3:
                    transfer(loginCard);
                    break;
                case 4:
                    closeAccount(loginCard);
                    return false;
                case 5:
                    System.out.println("\nYou have successfully logged out!");
                    return false;
                case 0:
                    System.out.println("\nBye");
                    return true;
                default:
                    System.out.println("\nPlease enter number 0-5");
                    return false;
            }
        }
    }

    private void closeAccount(String loginCard) {
        System.out.println("\nThe account has been closed!");
        bankCardDao.delete(loginCard);
    }

    private void addIncome(String loginCard) {
        System.out.println("\nEnter income:");
        bankCardDao.addIncome(loginCard, SCANNER.nextInt());
    }

    private void transfer(String loginCard) {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String transferCard = SCANNER.next();
        checkInputTransferCard(loginCard, transferCard);
    }


    private void checkInputTransferCard(String loginCard, String transferCard) {
        if (Objects.equals(loginCard, transferCard)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!LuhnAlgorithm.checkNumberValid(transferCard)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!bankCardDao.checkTransferCardNumberInDataBase(transferCard)) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            if (bankCardDao.doTransfer(loginCard, transferCard, SCANNER.nextInt())) {
                System.out.println("Success!");
            } else {
                System.out.println("Not enough money!");
            }
        }

    }

    private void createAnAccount() {
        BankCard card = new BankCard();
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getCardId());
        System.out.println("Your card PIN:");
        System.out.println(card.getCardPin() + "\n");
        bankCardDao.save(card);
        bankCardDao.getAll().forEach(bankCard -> {
            System.out.println("balance: " + bankCard.getBalance());
            System.out.println("...");
        });
    }
}

