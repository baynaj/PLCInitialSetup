package plc.homework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Contains JUnit tests for {@link Regex}. A framework of the test structure 
 * is provided, you will fill in the remaining pieces.
 *
 * To run tests, either click the run icon on the left margin, which can be used
 * to run all tests or only a specific test. You should make sure your tests are
 * run through IntelliJ (File > Settings > Build, Execution, Deployment > Build
 * Tools > Gradle > Run tests using <em>IntelliJ IDEA</em>). This ensures the
 * name and inputs for the tests are displayed correctly in the run window.
 */
public class RegexTests {

    /**
     * This is a parameterized test for the {@link Regex#EMAIL} regex. The
     * {@link ParameterizedTest} annotation defines this method as a
     * parameterized test, and {@link MethodSource} tells JUnit to look for the
     * static method {@link #testEmailRegex()}.
     *
     * For personal preference, I include a test name as the first parameter
     * which describes what that test should be testing - this is visible in
     * IntelliJ when running the tests (see above note if not working).
     */
    @ParameterizedTest
    @MethodSource
    public void testEmailRegex(String test, String input, boolean success) {
        test(input, Regex.EMAIL, success);
    }

    /**
     * This is the factory method providing test cases for the parameterized
     * test above - note that it is static, takes no arguments, and has the same
     * name as the test. The {@link Arguments} object contains the arguments for
     * each test to be passed to the function above.
     */
    public static Stream<Arguments> testEmailRegex() {
        return Stream.of(
                Arguments.of("Alphanumeric", "thelegend27@gmail.com", true),
                Arguments.of("UF Domain", "otherdomain@ufl.edu", true),
                Arguments.of("Dot Between title", "bayron.najera@workemail.com", true),
                Arguments.of("ALL CAPS Title", "SOMEEMAIL@email.net", true),
                Arguments.of("Sad Face", "-_-@sadFace.com", true),
                Arguments.of("Missing Domain Dot", "missingdot@gmailcom", false),
                Arguments.of("Symbols", "symbols#$%@gmail.com", false),
                Arguments.of("Missing at symbol", "noatsymbolyahoo.com", false),
                Arguments.of("Comma in Domain", "commadomain@gmail,com", false),
                Arguments.of("Bad Domain", "badtopleveldomain@gmail.website", false),
                Arguments.of("Bad Domain Pt2", "badtopleveldomain@gmail.m", false),
                Arguments.of("CAPS DOMAIN", "capsdomain@YAHOO.COM", false),
                Arguments.of("Space in title", "space in@gmail.com", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testEvenStringsRegex(String test, String input, boolean success) {
        test(input, Regex.EVEN_STRINGS, success);
    }

    public static Stream<Arguments> testEvenStringsRegex() {
        return Stream.of(
                //what has ten letters and starts with gas?
                Arguments.of("10 Characters", "automobile", true),
                Arguments.of("14 Characters", "i<3pancakes10!", true),
                Arguments.of("all even spaces", "          ", true),
                Arguments.of("20 Characters", "thereShouldBe20Chars", true),
                Arguments.of("Even special chars", "..................", true),
                Arguments.of("spaced words", "Spacing Words!", true),
                Arguments.of("6 Characters", "6chars", false),
                Arguments.of("13 Characters", "i<3pancakes9!", false),
                Arguments.of("empty", "", false),
                Arguments.of("29 Chars", "CharmanderCharmeleonCharizard", false),
                Arguments.of("New Line and Tab", "Hello \n there \t", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testIntegerListRegex(String test, String input, boolean success) {
        test(input, Regex.INTEGER_LIST, success);
    }

    public static Stream<Arguments> testIntegerListRegex() {
        return Stream.of(
                Arguments.of("Single Element", "[1]", true),
                Arguments.of("Multiple Elements", "[1,2,3]", true),
                Arguments.of("Empty list", "[]", true),
                Arguments.of("Space after comma", "[1,2,3, 6, 7]", true),
                Arguments.of("Larger Integers", "[200, 400, 600]", true),
                Arguments.of("Zeros", "[00000000,0,000000000]", true),
                Arguments.of("Missing Brackets", "1,2,3", false),
                Arguments.of("Missing Commas", "[1 2 3]", false),
                Arguments.of("No Integers", "[, , , ]", false),
                Arguments.of("Comma after last int", "[5,9,13,17,21,]", false),
                Arguments.of("Too many spaces", "[1  , 2  ,  3]", false),
                Arguments.of("No Decimals", "[0.1, 2.0, 30.0]", false),
                Arguments.of("Non Ints", "[Ten, nine, eight, seven]", false),
                Arguments.of("Negative", "[-5,-4,-3,-2,-1]", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testNumberRegex(String test, String input, boolean success) {
        test(input, Regex.NUMBER, success);
    }

    public static Stream<Arguments> testNumberRegex() {
        return Stream.of(
                Arguments.of("Single Digit Integer", "1", true),
                Arguments.of("Multiple Digit Decimal", "123.456", true),
                Arguments.of("Negative Decimal", "-1.0", true),
                Arguments.of("Very Small number", "0.00000000000000000001", true),
                Arguments.of("Very Large Decimal", "9999999999999999999.99", true),
                Arguments.of("Negative Int", "-5000", true),
                Arguments.of("Positive Int", "+1500", true),
                Arguments.of("Pi", "3.1415926535", true),
                Arguments.of("Trailing Decimal", "1.", false),
                Arguments.of("Leading Decimal", ".5", false),
                Arguments.of("Lots of leading decimals", ".........101", false),
                Arguments.of("Multiple Decimals", "05.20.2022", false),
                Arguments.of("Non number", "Two Hundred", false),
                Arguments.of("Double Negative", "--81", false),
                Arguments.of("Double Positive", "++100", false),
                Arguments.of("Spaced out numbers", "3 0 0", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testStringRegex(String test, String input, boolean success) {
        test(input, Regex.STRING, success);
    }

    public static Stream<Arguments> testStringRegex() {
        return Stream.of(
                Arguments.of("Empty", "\"\"", true),
                Arguments.of("Hello World", "\"Hello, World!\"", true),
                Arguments.of("Escape", "\"1\\t2\"", true),
                Arguments.of("Special Chars", "\"$+=- *%\"", true),
                Arguments.of("Inner Quotes", "\"He exclaimed, 'Wow!'\"", true),
                Arguments.of("Lots of spaces", "\"0                 0\"", true),
                Arguments.of("Algebra", "\"7j + 5 - 9 = 0\"", true),
                Arguments.of("Unterminated", "\"unterminated", false),
                Arguments.of("Invalid Escape", "\"invalid\\escape\"", false),
                Arguments.of("No quotes", "Am I a literal string?", false),
                Arguments.of("Wrong amount of escape sequences", "\"\\\\\\\\\\\"", false),
                Arguments.of("Two separate strings", "\"Me\" \"You\"", false),
                Arguments.of("Only numbers", "123", false),
                Arguments.of("Single quotes", "'This ain't a string literal'", false)
        );
    }

    /**
     * Asserts that the input matches the given pattern. This method doesn't do
     * much now, but you will see this concept in future assignments.
     */
    private static void test(String input, Pattern pattern, boolean success) {
        Assertions.assertEquals(success, pattern.matcher(input).matches());
    }

}
