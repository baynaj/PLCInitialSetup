package plc.project;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
/**
 * The lexer works through three main functions:
 *
 *  - {@link #lex()}, which repeatedly calls lexToken() and skips whitespace
 *  - {@link #lexToken()}, which lexes the next token
 *  - {@link CharStream}, which manages the state of the lexer and literals
 *
 * If the lexer fails to parse something (such as an unterminated string) you
 * should throw a {@link ParseException} with an index at the character which is
 * invalid or missing.
 *
 * The {@link #peek(String...)} and {@link #match(String...)} functions are
 * helpers you need to use, they will make the implementation a lot easier.
 */
public final class Lexer {

    private final CharStream chars;

//    public static final Pattern
//            IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_-]*"),
//            INTEGER = Pattern.compile("[+-]?[0-9]*"),
//            DECIMAL = Pattern.compile("[+-]?[0-9]+(.[0-9]+)?"),
//            CHARACTER = Pattern.compile("'([^'\\n\\r\\\\]|(\\\\[bnrt'\\\"\\\\]))'"),
//            STRING = Pattern.compile("\\\"([^\\\"\\n\\r\\\\]|(\\\\[bnrt'\\\"\\\\]))*\\\""),
//            OPERATOR = Pattern.compile("[<>!=]=?");

    public Lexer(String input) {
        chars = new CharStream(input);
    }
    /**
     * Repeatedly lexes the input using {@link #lexToken()}, also skipping over
     * whitespace where appropriate.
     */
    public List<Token> lex() {
        String whitespace = "[ \b\n\r\t]";
        List<Token> lexResult = new ArrayList<>();

        // doesnt play nice with the index already used
//        for(int i = 0 ; i < chars.length ; i++){
//            // checking if char is not whitespace, if it is not, then fetch token
//            if (peek(".")) {
//                i++;
//                chars.advance();
//                chars.skip();
        while (peek("."))
        {
            if (peek(whitespace))  //skip over whitespaces and continue
            {
                chars.advance();
                chars.skip();
            }
            else {
                lexResult.add(lexToken());
            }
        }
        return lexResult;
        //throw new UnsupportedOperationException(); //TODO
    }

    /**
     * This method determines the type of the next token, delegating to the
     * appropriate lex method. As such, it is best for this method to not change
     * the state of the char stream (thus, use peek not match).
     *
     * The next character should start a valid token since whitespace is handled
     * by {@link #lex()}
     */
    public Token lexToken() {
        // top-down flow
        // starting with identifier
        if (peek("[A-Za-z_]")) {
            return lexIdentifier();
        }
        // move to number
        else if (peek("[+\\-]", "\\d") || peek("\\d")) {
            return lexNumber();
        }
        // move to character
        else if (peek("'")) {
            return lexCharacter();
        }
        // move to string
        else if (peek("\\\"")) {
            return lexString();
        }
        // default check for operator
        else {
            return lexOperator();
        }
        //throw new UnsupportedOperationException(); //TODO
    }

    public Token lexIdentifier() {
        // since it passed the initial lexToken check, we can just check for everything stuff
        while(peek("[\\w\\-]"))
        {
            match("[\\w\\-]");
        }
        return chars.emit(Token.Type.IDENTIFIER);
    }

    public Token lexNumber() {
        //throw new UnsupportedOperationException(); //TODO

        //peek and match pair is redundant but easier to read.
        if(peek("[+\\-]"))
        {
            match("[+\\-]");
        }

        while(peek("\\d"))
        {
            match("\\d");
        }
        // if we find a decimal that has a digit after it,
        // we need to take the dot and all following digits
        if(peek("[.]", "\\d"))
        {
            match("[.]");
            while(peek("\\d")) // get all digits after the dot till a whitespace
            {
                match("\\d");
            }
            // since we found a '.' we can just return this as a decimal
            return chars.emit(Token.Type.DECIMAL);
        }
        // since no '.' was found, return only an integer
        return chars.emit(Token.Type.INTEGER);
    }

    public Token lexCharacter() {
        //throw new UnsupportedOperationException(); //TODO
        //if (peek("'"))
        //{
        match("'");
        //}

        if(peek("\\\\")){ // dont take backslashes
            lexEscape();
        }
        else if(peek("[^'\\n\\r\\\\]")) //find and grab anything that isnt an illegal escape char
        {
            match("[^'\\n\\r\\\\]");
        }
        else
        {
            throw new ParseException("Character is not allowed at this index", chars.index);
        }

        if(match("'")) // make sure we finish closing the single quotes
        {
            return chars.emit(Token.Type.CHARACTER);
        }
        throw new ParseException("Character single quote not closed", chars.index);
    }

    public Token lexString() {
        match("\\\"");
        while (peek("[^\"\\n\\r]"))
        {
            if (peek("\\\\"))
            {
                // if an escape is found, perform that operation
                lexEscape();
            }
            else
            {
                //take in everything thats is inbetween quotes otherwise
                match(".");
            }
        }
        //check for an endquote. If its not there, throw an exception, else take it
        if (!match("\\\""))
        {
            match("."); // take whatever is there so we spit out the correct index on error
            throw new ParseException("String not terminated. Endquote expected", chars.index);
        }

        return chars.emit(Token.Type.STRING);
    }

    public void lexEscape() {
        //throw new UnsupportedOperationException(); //TODO
        if(!match("\\\\", "[bnrt\'\"\\\\]"))
        {
            match(".","."); //move the cursor forward 2 index to support exception
            throw new ParseException("Unsupported Escape", chars.index);
        }
    }

    public Token lexOperator() {
        //throw new UnsupportedOperationException(); //TODO
        if(!match("[<>!=]", "="))
        {
            match("."); // if it made it past all lexFunctions then take <any other character>
        }
        return chars.emit(Token.Type.OPERATOR);
    }

    /**
     * Returns true if the next sequence of characters match the given patterns,
     * which should be a regex. For example, {@code peek("a", "b", "c")} would
     * return true if the next characters are {@code 'a', 'b', 'c'}.
     */
    public boolean peek(String... patterns) {
        for ( int i=0; i < patterns.length ; i++){
            if ( !chars.has(i) || !String.valueOf(chars.get(i)).matches(patterns[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true in the same way as {@link #peek(String...)}, but also
     * advances the character stream past all matched characters if peek returns
     * true. Hint - it's easiest to have this method simply call peek.
     */
    public boolean match(String... patterns) {
        boolean peek = peek(patterns);
        if(peek){
            for(int i=0; i < patterns.length; i++){
                chars.advance();
            }
        }
        return peek;
    }

    /**
     * A helper class maintaining the input string, current index of the char
     * stream, and the current length of the token being matched.
     *
     * You should rely on peek/match for state management in nearly all cases.
     * The only field you need to access is {@link #index} for any {@link
     * ParseException} which is thrown.
     */
    public static final class CharStream {

        private final String input;
        private int index = 0;
        private int length = 0;

        public CharStream(String input) {
            this.input = input;
        }

        // check to see if there is a char at offset position
        public boolean has(int offset) {
            return index + offset < input.length();
        }

        // returns the character at the cursor position
        public char get(int offset) {
            return input.charAt(index + offset);
        }

        // moves the cursor forward one position
        public void advance() {
            index++;
            length++;
        }

        public void skip() {
            length = 0;
        }

        public Token emit(Token.Type type) {
            int start = index - length;
            skip();
            return new Token(type, input.substring(start, index), start);
        }

    }

}
