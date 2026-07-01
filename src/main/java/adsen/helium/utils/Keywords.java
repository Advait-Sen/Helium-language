package adsen.helium.utils;
import adsen.helium.tokeniser.TokenType;
import java.util.HashMap;
import java.util.Map;

import static adsen.helium.tokeniser.TokenType.*;

public class Keywords {
    public static Map<String, TokenType> tokeniserKeywords = new HashMap<>() {{
        put("true", BOOL_LITERAL);
        put("false", BOOL_LITERAL);

        /*
        Types todo re-add primitive type strings
         put(IntPrimitive.TYPE_STRING, PRIMITIVE_TYPE);
         put(FloatPrimitive.TYPE_STRING, PRIMITIVE_TYPE);
         put(BoolPrimitive.TYPE_STRING, PRIMITIVE_TYPE);
         put(CharPrimitive.TYPE_STRING, PRIMITIVE_TYPE);
        */
        put("void", VOID);


        //Keywords
        put("let", LET);
        put("exit", EXIT);
        put("if", IF);
        put("else", ELSE);
        put("while", WHILE);
        put("for", FOR);
        put("return", RETURN);
        put("continue", CONTINUE);
        put("break", BREAK);
        put("import", IMPORT);
    }};

    public static Map<String, TokenType> operatorTokens = new HashMap<>();

    static {
       // OperatorType.noop(); todo OperatorType here
    }
}