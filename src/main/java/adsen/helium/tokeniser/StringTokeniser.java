package adsen.helium.tokeniser;

import adsen.helium.arguments.BaseConfig;
import adsen.helium.arguments.ConfigClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static adsen.helium.tokeniser.TokenType.*;
import static adsen.helium.utils.Keywords.operatorTokens;
import static adsen.helium.utils.Keywords.tokeniserKeywords;

/**
 * Turns an input string into a list of {@link Token}
 */
public class StringTokeniser extends ConfigClass<StringTokeniser.StringTokeniserConfig> {
    protected StringTokeniser(String input, Tokeniser.TokeniserConfig config) {
        super(new StringTokeniserConfig() {
            @Override
            public boolean listTokens() {
                return config.listTokens();
            }

            @Override
            public String input() {
                return input;
            }

            @Override
            public boolean verbose() {
                return config.verbose();
            }

            @Override
            public boolean debug() {
                return config.debug();
            }

            @Override
            public boolean anyVerbose() {
                return config.anyVerbose();
            }
        });

        pos = 0;
        linepos = 0;
        colpos = 0;
    }

    /**
     * Overall position within input string
     */
    private int pos;
    /**
     * Line position within input string
     */
    private int linepos;
    /**
     * Column position within input string
     */
    private int colpos;

    public List<Token> tokenise(){
        setVerboseCondition(config::listTokens);

        printlnVerbose(config.input());

        List<Token> tokens = new ArrayList<>();

        // To keep track of matching parentheses. It also gets done automatically later on the parser,
        // but this allows to catch errors earlier on, and I'm proud of this code
        Stack<Token> parens = new Stack<>();

        for (pos = 0; hasNext(); pos++) {
            char c = peek();
            Token token = new Token();

            //Skipping over whitespace
            while (hasNext() && Character.isWhitespace(c)) {
                if (c == '\n') {
                    colpos = 0;
                    linepos++;
                } else {
                    colpos++;
                }
                pos++;
                c = peek();
            }

            token.pos = this.pos;
            token.colpos = colpos;
            token.linepos = linepos;


            // Int literal, Float literal (even .456), Hex literal (0xab3c)
            if (isDigit(c) || (c == '.' && isDigit(peek(1)))) {
                boolean isFloat = false;
                boolean isHex = c == '0' && hasNext() && peek(1) == 'x';

                // While there are more characters to read,
                // and the next character is a hex digit, decimal point, or regular digit
                while (hasNext() && (isDigit(c) || !isFloat && c == '.' || isHex && isHexDigit(c))) {
                    token.append(c);

                    if (c == '.') {
                        isFloat = true;
                    }

                    c = consume();
                }
                //Backtracking since the while loop shoots one character further than necessary
                pos--;
                colpos--;

                token.type = isHex ? HEX_LITERAL : (isFloat ? FLOAT_LITERAL : INT_LITERAL);
            } else if (c == '\'' || c == '"') {//Char or Str literal
                boolean isStr = c == '"';
                token.type = isStr ? STR_LITERAL : CHAR_LITERAL;

                boolean reachedEnd = false;

                //Basically just grabbing all the characters that follow until the closure of the string or char
                while (!reachedEnd && hasNext()) {
                    c = consume();
                    if (c == '\n') {
                        //Incrementing line number after a newline
                        colpos = 0;
                        linepos++;
                    }

                    //Check for end of literal
                    if (isStr && c == '"' || !isStr && c == '\'') {
                        reachedEnd = true;

                    } else if (c == '\\') { //Checking for and handling escape characters
                        c = consume();

                        token.append(switch (c) { //Flexing new Java syntax
                            case 'n' -> '\n';
                            case 't' -> '\t';
                            case '\\' -> '\\';
                            case '"' -> '"'; //Allow to escape " in characters (so '\"') even tho it's unnecessary
                            case '\'' -> '\''; //And same deal with "\'" in strings
                            default -> {
                                token.append("\\" + c);
                                throw new RuntimeException("Invalid escape 'character '\\" + c + "'");
                                //todo throw compile time warning here, not error
                                //throw new ExpressionError("Invalid escape 'character '\\" + c + "'", token);
                            }
                        });

                    } else { //If it's not an escape character, then it's safe to add
                        token.append(c);
                    }
                }

                if (!isStr) { //Checking if char is too short or too long
                    if (token.value.isEmpty()) { //Copied error messages from Java
                        token.append("''");
                        throw new RuntimeException("Empty character literal");
                        //todo throw compile time error here
                        //throw new ExpressionError("Empty character literal", token);
                    } else if (token.value.length() != 1) {
                        throw new RuntimeException("Too many characters in character literal");
                        //todo throw compile time error here
                        //throw new ExpressionError("Too many characters in character literal", token);
                    }
                }
                if (!hasNext()) {
                    throw new RuntimeException("Did not terminate " + (isStr ? "string" : "char"));
                    //todo throw compile time error here
                    //throw new ExpressionError("Did not terminate " + (isStr ? "string" : "char"), token);
                }

            } else if (Character.isLetter(c) || c == '_') {//Identifiers, Bools, Keywords, basically any word

                while (hasNext() && (Character.isLetterOrDigit(c) || c == '_')) {
                    token.append(c);

                    c = consume();
                }
                pos--; //Because the last consume() overshoots by one
                colpos--;

                // If we haven't already mapped a token type (so 'true', 'false', 'int', 'exit', etc.)
                // then it's an identifier, i.e. a function or variable name (so far)
                token.type = tokeniserKeywords.getOrDefault(token.value, IDENTIFIER);

            } else if (c == '/' && (peek(1) == '/' || peek(1) == '*')) {
                //Checking for comments

                c = consume();
                if (c == '/') { //Line comment, consume until end of line
                    while (c != '\n') {
                        c = consume();
                    }
                    //Incrementing the line after the end of the comment
                    colpos = 0;
                    linepos++;
                } else if (c == '*') { //Block comment, consume until '*/'
                    boolean commentFinished = false;

                    do {
                        c = consume();
                        if (c == '\n') {
                            //Incrementing the line after the end of the line
                            colpos = 0;
                            linepos++;
                        }
                        if (c == '*' && peek(1) == '/') commentFinished = true;

                    } while (hasNext(1) && !commentFinished);

                    if (!commentFinished) {
                        throw new RuntimeException("Unclosed block comment");
                        //todo throw compile time error
                        //throw new ExpressionError("Unclosed block comment", token);
                    }

                    consume(); //Consume the / at the end of the block comment
                }

            } else if (c == ';') { //Grabbing special characters that have their own tokens
                token.type = SEMICOLON;
                token.append(c);
            } else if (c == ',') {
                token.type = COMMA;
                token.append(c);
            } else if (c == '(') { //Open parentheses get pushed onto the stack
                token.type = OPEN_PAREN;
                token.append(c);
                parens.push(token);
            } else if (c == '[') {
                token.type = SQ_OPEN_PAREN;
                token.append(c);
                parens.push(token);
            } else if (c == '{') {
                token.type = C_OPEN_PAREN;
                token.append(c);
                parens.push(token);
            } else if (c == ')') { //Closed parentheses pop off the stack, and if they don't match, we've got a problem
                token.type = CLOSE_PAREN;
                token.append(c);
                //todo distinguish between these to give more detailed error message
                if (parens.empty() || parens.pop().type != OPEN_PAREN) {
                    throw new RuntimeException("Mismatched parentheses");
                    //todo compile time error
                    //throw new ExpressionError("Mismatched parentheses", token);
                }
            } else if (c == ']') {
                token.type = SQ_CLOSE_PAREN;
                token.append(c);
                if (parens.empty() || parens.pop().type != SQ_OPEN_PAREN) {
                    throw new RuntimeException("Mismatched parentheses");
                    //todo compile time error
                    //throw new ExpressionError("Mismatched parentheses", token);
                }
            } else if (c == '}') {
                token.type = C_CLOSE_PAREN;
                token.append(c);
                if (parens.empty() || parens.pop().type != C_OPEN_PAREN) {
                    throw new RuntimeException("Mismatched parentheses");
                    //todo compile time error
                    //throw new ExpressionError("Mismatched parentheses", token);
                }
            } else { //Grabbing operators and maybe syntactic sugar later on

                while (hasNext() && !(Character.isWhitespace(c) || Character.isLetterOrDigit(c) || c == '_' || c == '\'' || c == ';' || c == '.' || c == '"' || c == ',' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}')) {
                    //Just grab everything until the next parenthesis, comma, whitespace, char, number, string, or identifier
                    token.append(c);
                    c = consume();
                }
                pos--; //Overshooting by one again
                colpos--;

                if (operatorTokens.containsKey(token.value)) {
                    token.type = operatorTokens.get(token.value);
                } else if (!token.value.isEmpty()) { //In case we ran into a comment or something that leaves an incomplete token
                    throw new RuntimeException("Unknown symbol");
                    //todo throw compile time error
                    //throw new ExpressionError("Unknown symbol", token);
                }
            }

            colpos++; //to make sure column number advances correctly

            if (token.type != null) //Skipping over final whitespaces and comments in file
                tokens.add(token);
        }

        if (!parens.empty()) {
            throw new RuntimeException("Mismatched parentheses");
            //todo compile time error
            //throw new ExpressionError("Mismatched parentheses", parens.getFirst());
        }

        //todo read input into tokens

        setDefaultVerbose();

        return tokens;
    }

    private boolean hasNext() {
        return hasNext(0);
    }

    private boolean hasNext(int offset) {
        return pos + offset < config.input().length();
    }

    private char peek(int offset) {
        if (!hasNext(offset)) return (char) -1;

        return config.input().charAt(pos + offset);
    }

    /**
     * Returns currently looked at character
     */
    private char peek() {
        return peek(0);
    }

    /**
     * Increments position counter and looks at next character in input
     */
    private char consume() {
        pos++;
        colpos++;
        return peek();
    }

    static boolean isDigit(char c) {
        return ('0' <= c && c <= '9');
    }

    static boolean isHexDigit(char c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F') || c == 'x' || c == 'X';
    }

    protected interface StringTokeniserConfig extends BaseConfig {
        boolean listTokens();

        String input();
    }
}
