import dao.impl.BankCardsDAOimpl;
import domain.BankCard;
import util.DataBaseUtils;
import util.LuhnAlgorithm;
import util.PrintMenuUtils;

import java.util.Objects;
import java.util.Scanner;

public class BankApp {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static BankCardsDAOimpl dB;

    public BankApp(String[] args) {
        DataBaseUtils.setURL(args);
    }

    public void run() {
        dB = new BankCardsDAOimpl();
            while (true) {
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
                        System.out.println("\nBye!");
                        return;
                    default:
                        System.out.println("Please enter number 0-2!");
                        break;
                }
            }
    }

    private static boolean logIntoAccount() {
        System.out.println("\nEnter your card number:");
        String loginCard = SCANNER.next();
        System.out.println("Enter your PIN:");
        String loginPin = SCANNER.next();
        if (!dB.checkLoginCardInDataBase(loginCard, loginPin)) {
            System.out.println("\nWrong card number or PIN!");
            return false;
        } else {
            System.out.println("\nYou have successfully logged in!");
            while (true) {
                PrintMenuUtils.showAccountMenu();
                switch (SCANNER.nextInt()) {
                    case 1:
                        System.out.println("\nBalance: " + dB.getBalance(loginCard));
                        break;
                    case 2:
                        addIncome(loginCard);
                        break;
                    case 3:
                        transfer(loginCard);
                        break;
                    case 4:
                        return closeAccount(loginCard);
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
    }

    private static boolean closeAccount(String loginCard) {
        System.out.println("\nThe account has been closed!");
        dB.deleteCardFromDataBase(loginCard);
        return false;
    }

    private static void addIncome(String loginCard) {
        System.out.println("\nEnter income:");
        dB.addIncome(loginCard, SCANNER.nextInt());
    }

    private static void transfer(String loginCard) {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String transferCard = SCANNER.next();
        checkInputTransferCard(loginCard, transferCard);
    }


    private static void checkInputTransferCard(String loginCard, String transferCard) {
        if (Objects.equals(loginCard, transferCard)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!LuhnAlgorithm.checkNumberValid(transferCard)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!dB.checkTransferCardNumberInDataBase(transferCard)) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            if (dB.doTransfer(loginCard, transferCard, SCANNER.nextInt())) {
                System.out.println("Success!");
            } else {
                System.out.println("Not enough money!");
            }
        }

    }

    private static void createAnAccount() {
        BankCard bankCard = new BankCard();
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(bankCard.getCardId());
        System.out.println("Your card PIN:");
        System.out.println(bankCard.getCardPin() + "\n");
        dB.addCardToDataBase(bankCard.getCardId(), bankCard.getCardPin());
        dB.printDataBaseTable();
    }
}

