
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class PLCProject{
    public static int charClass;
    public static char[] lexeme = new char[100];
    public static char nextChar;
    public static int lexLen;
    public static int token;
    public static int nextToken;
    static File file = new File("Program.txt");
    static Scanner sc;
    static FileReader reader;
    public static int character;
    static Map<String, Integer> reservedWordsMap = new HashMap<>();
    static Set<String> symbolTable = new HashSet<>();
    static boolean declaration;
    public static int line = 1;
    public static boolean  isNewLine = false;

    public static final int LETTER = 0;
    public static final int DIGIT = 1;
    public static final int UNDERSCORE = 2;
    public static final int LINE_END = 98;
    public static final int UNKNOWN = 99;
    public static final int NUM = 10;
    public static final int IDENT = 11;
    public static final int FLOAT = 12;
    public static final int DOUBLE = 13;

    public static final int ASSIGN_OP = 20;
    public static final int ADD_OP = 21;
    public static final int SUB_OP = 22;
    public static final int MULT_OP = 23;
    public static final int DIV_OP = 24;
    public static final int LEFT_PAREN = 25;
    public static final int RIGHT_PAREN = 26;
    public static final int LESS_OP = 27;
    public static final int GREATER_OP = 28;
    public static final int COLON = 29;
    public static final int SEMICOLON = 30;
    public static final int COMMA = 31;
    public static final int PERIOD = 32;
    public static final int EQUAL = 33;

    public static final int PROGRAM = 40;
    public static final int BEGIN = 41;
    public static final int END = 51;
    public static final int IF = 52;
    public static final int THEN = 53;
    public static final int ELSE = 54;
    public static final int INPUT = 55;
    public static final int OUTPUT = 56;
    public static final int INT = 57;
    public static final int WHILE = 58;
    public static final int LOOP = 59;
    public static final int EOF = -1;


    public static void main(String[] args) {
        try {
            reader = new FileReader("Program.txt");
        } catch (IOException e) {
            System.err.println("Failed to open file: " + e.getMessage());
        }

        reservedWordsMap.put("program", PROGRAM);
        reservedWordsMap.put("begin", BEGIN);
        reservedWordsMap.put("end", END);
        reservedWordsMap.put("if", IF);
        reservedWordsMap.put("then", THEN);
        reservedWordsMap.put("else", ELSE);
        reservedWordsMap.put("input", INPUT);
        reservedWordsMap.put("output", OUTPUT);
        reservedWordsMap.put("int", INT);
        reservedWordsMap.put("float", FLOAT);
        reservedWordsMap.put("double", DOUBLE);
        reservedWordsMap.put("while", WHILE);
        reservedWordsMap.put("loop", LOOP);

        getChar();
        start();
    }


    //=============================================Scanner========================================================================
    static int lookup(char ch){
        switch (ch){
            case '(':
                addChar();
                nextToken = LEFT_PAREN;
                break;
            case ')':
                addChar();
                nextToken = RIGHT_PAREN;
                break;
            case '+':
                addChar();
                nextToken = ADD_OP;
                break;
            case '-':
                addChar();
                nextToken = SUB_OP;
                break;
            case '*':
                addChar();
                nextToken = MULT_OP;
                break;
            case '/':
                addChar();
                nextToken = DIV_OP;
                break;
            case '=':
                addChar();
                nextToken = EQUAL;
                break;
            case ',':
                addChar();
                nextToken = COMMA;
                break;
            case ';':
                addChar();
                nextToken = SEMICOLON;
                break;
            case ':':
                addChar();
                nextToken = COLON;
                break;
            case '.':
                addChar();
                nextToken = PERIOD;
                break;
            case '>':
                addChar();
                nextToken = GREATER_OP;
                break;
            case '<':
                addChar();
                nextToken = LESS_OP;
                break;
            case '\r':
                nextToken = UNKNOWN;
                break;
            case '\n':
                nextToken = UNKNOWN;
                break;
            default:
                addChar();
                nextToken = EOF;
                break;
        }
        return nextToken;
    }

    static void addChar(){
        if(lexLen <= 98){
            lexeme[lexLen++] = nextChar;
            lexeme[lexLen] = 0;
        } else {
            error("Error: Lexeme is too long");
        }
    }

    static void getChar(){
        try {
            if((character = reader.read()) != EOF){
                nextChar = (char)character;
                if(Character.isLetter(nextChar)){
                    charClass = LETTER;
                }else if (nextChar == '_') {
                    charClass = UNDERSCORE;
                }else if (Character.isDigit(nextChar)){
                    charClass = DIGIT;
                } else if (nextChar == '.') {
                    charClass = PERIOD;
                } else if (nextChar == '\n' || nextChar == '\r') {
                    charClass = LINE_END;
                    if(isNewLine == false){
                        isNewLine = true;
                    } else {
                        line++;
                        isNewLine = false;
                    }
                }else {
                    charClass = UNKNOWN;
                }
            } else {
                charClass = EOF;
            }
        } catch (IOException e) {
        }
    }

    static void getNonBlank(){
        while(Character.isWhitespace(nextChar)){
            getChar();
        }
    }

    static int lex(){
        lexLen = 0;
        Arrays.fill(lexeme, ' ');

        getNonBlank();
        switch (charClass){
            case LETTER:
            case UNDERSCORE:    
            
                addChar();
                getChar();
                while(charClass == LETTER || charClass == DIGIT || charClass == UNDERSCORE){
                    addChar();
                    getChar();
                }
                String key = String.valueOf(lexeme).trim();
                Integer result = reservedWordsMap.get(key);
                if(result != null){
                    nextToken = result;
                } else {
                    nextToken = IDENT;
                }
                break;
            case DIGIT:
                addChar();
                getChar();
                int size = 1;
                boolean isFloat = false;
                while(charClass == DIGIT || charClass == PERIOD){
                    if(size > 10){
                        error("Error: Number exceeds 10 digits");
                        System.exit(0);
                    } else if (isFloat == true && charClass == PERIOD) {
                        error("Error: More than one decimal point");
                        System.exit(0);
                    } else if (charClass == PERIOD) {
                        isFloat = true;
                    }
                    addChar();
                    getChar();
                    size++;
                }
                if(isFloat == true){
                    nextToken = FLOAT;
                } else {
                    nextToken = NUM;
                }
                break;
                
            case UNKNOWN:
                lookup(nextChar);
                if (nextToken == COLON) {
                getChar();
                if (nextChar == '=') {
                    addChar();
                    nextToken = ASSIGN_OP;
                }
            }
                getChar();
                break;
            case LINE_END:
                addChar();
                getChar();
                if(nextToken == LINE_END){
                    line++;
                }
            case EOF:
                nextToken = EOF;
                lexeme[0] = 'E';
                lexeme[1] = 'O';
                lexeme[2] = 'F';
                lexeme[3] = 0;
                break;

        }
        return nextToken;
    }

    //===================================================Parser==========================================================

    static void start(){
        lex();
        ProgramRule();
    }

    static void ProgramRule(){
        System.out.println("PROGRAM");
        if(nextToken == PROGRAM){
            lex();
            if(nextToken == IDENT){
                declSec();
            }
            if(nextToken == BEGIN){
                lex();
                stmtSec();
                if(nextToken == END){
                    lex();
                    System.out.println("Program successful");
                } else {
                    error("Error: Expected 'end' at the end of program");
                }
            } else {
                error("Error: Expected 'begin' after program declaration");
            }
        } else {
            error("Error: Program must start with the keyword 'program'");
        }
    }

    static void declSec(){
        System.out.println("DECL_SEC");
        decl();
       if(nextToken == IDENT){
        declSec();
       }
    }

    static void decl(){
        System.out.println("DECL");
        declaration = true;
        idList();
        declaration = false;
        if(nextToken == COLON){
            lex();
            type();
            if(nextToken == SEMICOLON){
                lex();
            } else {
                error("Error: Expected ';' after declaration.");
            }
        } else {
            error("Error: Expected ':' in declaration.");
        }
    }

    static void idList(){
        System.out.println("ID_LIST");
        id();
        if(nextToken == COMMA){
            lex();
            idList();
        }
    }

    static void id(){
        if(nextToken == IDENT){
            String variable = String.valueOf(lexeme).trim();
            if (declaration) {
                if (symbolTable.contains(variable)) {
                    error("Error: Variable '" + variable + "' already declared.");
                } else {
                    symbolTable.add(variable);
                }
            } else {
                if (!symbolTable.contains(variable)) {
                    error("Error: Variable '" + variable + "' used before declaration.");
                }
            }
            lex();
        } else {
            error("Error: Invalid identifier");
        }
    }

    static void type(){
        if(nextToken == INT || nextToken == FLOAT || nextToken == DOUBLE){
            lex();
        } else {
            error("Error: Expected a valid type (int, float, double)");
        }
    }

    static void stmtSec(){
        System.out.println("STMT_SEC");
        stmt();
        if(nextToken == IDENT || nextToken == IF || nextToken == WHILE || nextToken == INPUT || nextToken == OUTPUT ){
            stmtSec();
        }
    }

    static void stmt(){
        System.out.println("STMT");
        switch (nextToken){
            case IDENT:
                assign();
                break;
            case IF:
                ifStmt();
                break;
            case WHILE:
                whileStmt();
                break;
            case INPUT:
                input();
                break;
            case OUTPUT:
                output();
                break;
            default:
                error("Error: Invalid statement");
        }
    }

    static void assign(){
        System.out.println("ASSIGN");
        declaration = false;
        id();
        if(nextToken == ASSIGN_OP){
            lex();
            expr();
            if(nextToken == SEMICOLON){
                lex();
            } else {
                error("Error: Expexted ';' after assignment");
            }
        } else {
            error("Error: Expected ':=' in assignment");
        }
    }

    static void ifStmt() {
        System.out.println("IF_STMT");
        if (nextToken == IF) {
            lex();
            comp();
            if (nextToken == THEN) {
                lex();
                stmtSec();
                if (nextToken == END) {
                    lex();
                    if (nextToken == IF) {
                        lex();
                        if (nextToken == SEMICOLON) {
                            lex();
                        } else {
                            error("Error: Expected ';' after 'end if'");
                        }
                    } else {
                        error("Error: Expected 'if' after 'end'");
                    }
                } else if (nextToken == ELSE) {
                    lex();
                    stmtSec();
                    if (nextToken == END) {
                        lex();
                        if (nextToken == IF) {
                            lex();
                            if (nextToken == SEMICOLON) {
                                lex();
                            } else {
                                error("Error: Expected ';' after 'end if'");
                            }
                        } else {
                            error("Error: Expected 'if' after 'end'");
                        }
                    } else {
                        error("Error: Expected 'end' after 'else'");
                    }
                } else {
                    error("Error: Expected 'end if' or 'else' after 'then' block");
                }
            } else {
                error("Error: Expected 'then' after 'if' condition");
            }
        } else {
            error("Error: Expected 'if'");
        }
    }

    static void whileStmt() {
        System.out.println("WHILE_STMT");
        if (nextToken == WHILE) {
            lex();
            comp();
            if (nextToken == LOOP) {
                lex();
                stmtSec();
                if (nextToken == END) {
                    lex();
                    if (nextToken == LOOP) {
                        lex();
                        if(nextToken == SEMICOLON){
                            lex();
                        } else {
                            error("Error: Expected ';' after 'end loop'");
                        }
                    } else {
                        error("Error: Expected 'loop' after 'end'");
                    }
                } else {
                    error("Error: Expected 'end loop'");
                }
            } else {
                error("Error: Expected 'loop' after 'while' condition");
            }
        } else {
            error("Error: Expected 'while'");
        }
    }

    static void input() {
        System.out.println("INPUT");
        if (nextToken == INPUT) {
            lex();
            idList();
            if (nextToken == SEMICOLON) {
                lex();
            } else {
                error("Error: Expected ';' after input statement");
            }
        } else {
            error("Error: Expected 'input'");
        }
    }

    static void output() {
        System.out.println("OUTPUT");
        if (nextToken == OUTPUT) {
            lex();
            if (nextToken == IDENT ) {
                idList();
            } else if(nextToken == NUM){
                lex();
            } else {
                error("Error: Expected identifier or number in output statement");
            }
            if (nextToken == SEMICOLON) {
                lex();
            } else {
                error("Error: Expected ';' after output statement");
            }
        } else {
            error("Error: Expected 'output'");
        }
    }

    static void expr() {
        System.out.println("EXPR");
        factor();
        if (nextToken == ADD_OP || nextToken == SUB_OP) {
            lex();
            expr();
        }
    }

    static void factor() {
        System.out.println("FACTOR");
        operand();
        if (nextToken == MULT_OP || nextToken == DIV_OP) {
            lex();
            factor();
        }
    }

    static void operand() {
        System.out.println("OPERAND");
        if (nextToken == IDENT) {
            String variable = String.valueOf(lexeme).trim();
            if(symbolTable.contains(variable)){
                lex();
            } else {
                error("Error: Identifier not declared");
            }
        } else if(nextToken == NUM){
            lex();
        } else if (nextToken == LEFT_PAREN) {
            lex();
            expr();
            if (nextToken == RIGHT_PAREN) {
                lex();
            } else {
                error("Error: Expected ')' after expression");
            }
        } else {
            error("Error: Invalid operand");
        }
    }

    static void comp() {
        System.out.println("COMP");
        if (nextToken == LEFT_PAREN) {
            lex();
            operand();
            if (nextToken == ASSIGN_OP || nextToken == LESS_OP || nextToken == GREATER_OP) {
                lex();
                operand();
                if (nextToken == RIGHT_PAREN) {
                    lex();
                } else {
                    error("Error: Expected ')' after comparison");
                }
            } else {
                error("Error: Expected comparison operator");
            }
        } else {
            error("Error: Expected '(' at the start of comparison");
        }
    }

    static void error(String message) {
        System.err.println("Syntax Error: " + message + " in line " + line);
        System.exit(-1);
    }
}