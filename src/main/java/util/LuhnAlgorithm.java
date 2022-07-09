package util;

public final class LuhnAlgorithm {
    private LuhnAlgorithm() {}

    public static int generateCheckSum(StringBuilder number) {
        long num = Long.parseLong(number.toString());
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            long l = num % 10;
            if (i % 2 == 0) {
                l *= 2;
                if (l > 9) {
                    l -= 9;
                }
            }
            num = num / 10;
            sum += l;
        }
        return 10 - sum % 10;
    }

    public static boolean checkNumberValid(String number) {
        int nDigits = number.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = number.charAt(i) - '0';
            if (isSecond){
                d = d * 2;
            }
            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
        return nSum % 10 == 0;
    }
}
