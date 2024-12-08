package Tools;

import java.util.Random;

public class StringGenerator {
    private static long uniqueStringCounter = 0;

    public static long getUniqueStringCounter() {
        return uniqueStringCounter;
    }

    public static void setUniqueStringCounter(long uniqueStringCounter) {
        StringGenerator.uniqueStringCounter = uniqueStringCounter;
    }

    public static String generateRandomString(int minLength, int maxLength) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        int lengthOfString = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder randomString = new StringBuilder();

        while (randomString.length() < lengthOfString) {
            int randomIndex = random.nextInt(characters.length());
            char character = characters.charAt(randomIndex);
            randomString.append(character);
        }

        return randomString.toString();
    }

    public static String generateUniqueString(int maxLength) {
        // umoznuje vygenerovat cca 78 mil. unikatnych 4-znakovych retazcov
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

        long currCounter = StringGenerator.uniqueStringCounter++;
        StringBuilder uniqueString = new StringBuilder();

        while (currCounter > 0) {
            int remainder = (int) (currCounter % characters.length());
            uniqueString.append(characters.charAt(remainder));
            currCounter /= characters.length();
        }

        while (uniqueString.length() < maxLength) {
            uniqueString.append('A');
        }

        return uniqueString.reverse().toString();
    }
}
