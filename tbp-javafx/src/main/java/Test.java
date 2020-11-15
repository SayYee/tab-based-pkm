import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author SayYi
 */
public class Test {

    public static void main(String[] args) throws IOException {
        final boolean matches = Pattern.matches("$*+java", "hello.java");
        System.out.println(matches);
    }
}
