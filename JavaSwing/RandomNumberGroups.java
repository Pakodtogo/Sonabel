import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomNumberGroups {
    public static void main(String[] args) {
        String code = genererCode();
        System.out.println(code);
    }

    public static String genererCode() {
        List<String> groupsOfDigits = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String randomNumber = String.format("%04d", new Random().nextInt(10000));
            groupsOfDigits.add(randomNumber);
        }

        return String.join(" ", groupsOfDigits);
    }
}
