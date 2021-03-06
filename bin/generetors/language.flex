package lexical;
import java_cup.*;
import java_cup.runtime.*;
import lexical.*;

%%

%public
%class LexicalAnalyzer
%unicode
%line
%column
%cup
%cupdebug

%{
   StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
	return new JavaSymbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
	return new JavaSymbol(type, yyline+1, yycolumn+1, value);
  }

  private long parseLong(int start, int end, int radix) {
	long result = 0;
	long digit;

	for (int i = start; i < end; i++) {
	  digit  = Character.digit(yycharat(i),radix);
	  result*= radix;
	  result+= digit;
	}

	return result;
  }
%}

/*Macros*/

O =  [0-7]
D = [0-9]
NZ = [1-9]
L = [a-zA-Z_]
A =  [a-zA-Z_0-9]
H = [a-fA-F0-9]
HP = (0[xX])
E = [Ee][+-]?{D}+
P =  ([Pp][+-]?{D}+)
FS = (f|F|l|L)
IS = (u|U|l|L)*
CP = (u|U|L)
SP = (u8|u|U|L)
ES = (\\([\'\"\?\\abfnrtv]|[0-7]{1,3}|x[a-fA-F0-9]+))
WS = [ \t\v\n\f]

/* White spaces*/
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

/* Float literals */
FloatLiteral  = ({Float1}|{Float2}|{Float3}) [fF]
DoubleLiteral = ({Float1}|{Float2}|{Float3}) [dD]

Float1    = [0-9]"."[0-9]*
Float2    = "."[0-9]+
Float3    = [0-9]+

/* Integer literals */
DecimalLiteral = 0 | [1-9][0-9]*
LongLiteral    = {DecimalLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]


/* Strings */
Marker = [\"]
Other_Symbols = \*|\+|\[|\]|\!|\£|\$|\%|\&|\=|\?|\^|\-|\°|\#|\@|\:|\(|\)
Separators = \r|\n|\r\n\t\f
Alphanumerics_ = [ a-zA-Z0-9_]

/* Identifiers */
Identifier = [:jletter:][:jletterdigit:]*

temp = [ \*|\+|\[|\]|\!|\£|\$|\%|\&|\=|\?|\^|\-|\°|\#|\@|\:|\(|\)|\"|\r|\n|\r\n\t\f a-zA-Z0-9_]*

/* String and Character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state STRING, CHARLITERAL


/* Comments */
Comment = "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"

%%

<YYINITIAL> {

    /* Keywords */
    "abstract"                      { return symbol(sym.ABSTRACT); }
    "boolean"                       { return symbol(sym.BOOLEAN); }
    "break"                         { return symbol(sym.BREAK); }
    "byte"                          { return symbol(sym.BYTE); }
    "case"                          { return symbol(sym.CASE); }
    "catch"                         { return symbol(sym.CATCH); }
    "char"                          { return symbol(sym.CHAR); }
    "class"                         { return symbol(sym.CLASS); }
    "continue"                      { return symbol(sym.CONTINUE); }
    "default"                       { return symbol(sym.DEFAULT); }
    "double"                        { return symbol(sym.DOUBLE); }
    "do"                            { return symbol(sym.DO); }
    "else"                          { return symbol(sym.ELSE); }
    "extends"                       { return symbol(sym.EXTENDS); }
    "finally"                       { return symbol(sym.FINALLY); }
    "final"                         { return symbol(sym.FINAL); }
    "float"                         { return symbol(sym.FLOAT); }
    "for"                           { return symbol(sym.FOR); }
    "if"                            { return symbol(sym.IF); }
    "implements"                    { return symbol(sym.IMPLEMENTS); }
    "import"                        { return symbol(sym.IMPORT); }
    "instanceof"                    { return symbol(sym.INSTANCEOF); }
    "interface"                     { return symbol(sym.INTERFACE); }
    "int"                           { return symbol(sym.INT); }
    "long"                          { return symbol(sym.LONG); }
    "native"                        { return symbol(sym.NATIVE); }
    "new"                           { return symbol(sym.NEW); }
    "package"                       { return symbol(sym.PACKAGE); }
    "private"                       { return symbol(sym.PRIVATE); }
    "protected"                     { return symbol(sym.PROTECTED); }
    "public"                        { return symbol(sym.PUBLIC); }
    "return"                        { return symbol(sym.RETURN); }
    "short"                         { return symbol(sym.SHORT); }
    "static"                        { return symbol(sym.STATIC); }
    "super"                         { return symbol(sym.SUPER); }
    "switch"                        { return symbol(sym.SWITCH); }
    "synchronized"                  { return symbol(sym.SYNCHRONIZED); }
    "this"                          { return symbol(sym.THIS); }
    "threadsafe"    			    { return symbol(sym.THREADSAFE);}
    "throw"                         { return symbol(sym.THROW); }
    "transient"                     { return symbol(sym.TRANSIENT); }
    "try"                           { return symbol(sym.TRY); }
    "while"                         { return symbol(sym.WHILE); }

    /* Boolean literals*/
    "true"                          { return symbol(sym.BOOLEAN_LITERAL, new Boolean(true)); }
    "false"                         { return symbol(sym.BOOLEAN_LITERAL, new Boolean(false)); }

//    {FloatLiteral}                  { return symbol(sym.FLOATING_POINT_LITERAL, new Float(yytext().substring(0,yylength()))); }
//    {DoubleLiteral}                 { return symbol(sym.FLOATING_POINT_LITERAL, new Double(yytext().substring(0,yylength()))); }
//
//    {DecimalLiteral}                { return symbol(sym.INTEGER_LITERAL, new Integer(yytext())); }
//    {LongLiteral}                   { return symbol(sym.INTEGER_LITERAL, new Long(yytext().substring(0,yylength()-1))); }
//
//    {HexIntegerLiteral}             { return symbol(sym.INTEGER_LITERAL, new Integer((int) parseLong(2, yylength(), 16))); }
//    {HexLongLiteral}                { return symbol(sym.INTEGER_LITERAL, new Long(parseLong(2, yylength()-1, 16))); }
//
//    {OctIntegerLiteral}             { return symbol(sym.INTEGER_LITERAL, new Integer((int) parseLong(0, yylength(), 8))); }
//    {OctLongLiteral}                { return symbol(sym.INTEGER_LITERAL, new Long(parseLong(0, yylength()-1, 8))); }

    /* Identifier*/
    {Identifier} 					{ return symbol(sym.IDENTIFIER,yytext());}

    /* Comments*/
    {Comment}                       { /* just ignore it */ }

    /* Separators */
    "("                             { return symbol(sym.LPAREN); }
    ")"                             { return symbol(sym.RPAREN); }
    "{"                             { return symbol(sym.LBRACE); }
    "}"                             { return symbol(sym.RBRACE); }
    "["                             { return symbol(sym.LBRACK); }
    "]"                             { return symbol(sym.RBRACK); }
    ";"                             { return symbol(sym.SEMICOLON); }
    ","                             { return symbol(sym.COMMA); }
    "."   		  				    { return symbol(sym.DOT); }

    /* String literal */
    \"                              { yybegin(STRING); string.setLength(0); }

    /* Character literal */
    \'                              { yybegin(CHARLITERAL); }

    /* White spaces */
    {WhiteSpace}				    { /* just ignore it*/}

    /* Assignment */
    "="							    {return symbol(sym.EQ);}

    /* Arithmetical operators*/
    "+" 							{return symbol(sym.PLUS);}
    "-" 						    {return symbol(sym.MINUS);}
    "*" 							{return symbol(sym.MULT);}
    "/"							    {return symbol(sym.DIV);}
    "++"							{return symbol(sym.PLUSPLUS);}
    "+="							{return symbol(sym.PLUSEQ);}
    "-="							{return symbol(sym.MINUSEQ);}
    "*="						    {return symbol(sym.MULTEQ);}
    "/="				            {return symbol(sym.DIVEQ);}
    "--"							{return symbol(sym.MINUSMINUS);}
    "%"							    {return symbol(sym.MOD);}
    "%="							{return symbol(sym.MODEQ);}
    "<<"							{return symbol(sym.LSHIFT);}
    ">>"							{return symbol(sym.RSHIFT);}
    ">>>"							{return symbol(sym.URSHIFT);}

    /* Operators */
    ":"                             {return symbol(sym.COLON);}

    /* Logical Operators*/
    "=="							{return symbol(sym.EQEQ);}
    ">="							{return symbol(sym.GTEQ);}
    "<="							{return symbol(sym.LTEQ);}
    "<"							    {return symbol(sym.LT);}
    ">"							    {return symbol(sym.GT);}
    "||="                           {return symbol(sym.OROREQ);}
    "&&"							{return symbol(sym.AMPERSANDAMPERSAND);}
    "&"							    {return symbol(sym.AMPERSAND);}
    "!"							    {return symbol(sym.NOT);}
    "!="							{return symbol(sym.NOTEQ);}
    "|"							    {return symbol(sym.OR);}
    "&="							{return symbol(sym.AMPERSANDEQ);}
    "|="							{return symbol(sym.OREQ);}
    "^"						        {return symbol(sym.XOR);}
    "^="                            {return symbol(sym.XOREQ);}
    ">>="							{return symbol(sym.RSHIFTEQ);}
    "?"                             { return symbol(sym.QUESTION); }

	{HP}{H}+{IS}?					{ return symbol(sym.INTEGER_LITERAL , new String(yytext())); }
	{NZ}{D}*{IS}?					{ return symbol(sym.INTEGER_LITERAL , new String(yytext())); }
	"0"{O}*{IS}?					{ return symbol(sym.INTEGER_LITERAL , new String(yytext())); }
	{CP}?"'"([^'\\\n]|{ES})+"'"		{ return symbol(sym.INTEGER_LITERAL , new String(yytext())); }

    "-"{NZ}{D}*{IS}?				{ return symbol(sym.INTEGER_LITERAL , new String(yytext())); }
    "-"{HP}{H}+{IS}?					{ return symbol(sym.INTEGER_LITERAL , new String(yytext())); }


	{D}+{E}{FS}?				{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
	{D}*"."{D}+{E}?{FS}?		{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
	{D}+"."{E}?{FS}?			{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
	{HP}{H}+{P}{FS}?			{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
	{HP}{H}*"."{H}+{P}{FS}?		{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
	{HP}{H}+"."{P}{FS}?			{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }

	"-"{D}+{E}{FS}?				{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
    "-"{D}*"."{D}+{E}?{FS}?		{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
    "-"{D}+"."{E}?{FS}?			{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
    "-"{HP}{H}+{P}{FS}?			{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
    "-"{HP}{H}*"."{H}+{P}{FS}?		{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }
    "-"{HP}{H}+"."{P}{FS}?			{ return symbol(sym.FLOAT_LITERAL , new String(yytext())); }

}

 <STRING> {
  \"                                { yybegin(YYINITIAL); return symbol(sym.STRING_LITERAL, string.toString()); }

  {StringCharacter}+                { string.append( yytext() ); }

  /* Escape sequences */
  "\\b"                             { string.append( '\b' ); }
  "\\t"                             { string.append( '\t' ); }
  "\\n"                             { string.append( '\n' ); }
  "\\f"                             { string.append( '\f' ); }
  "\\r"                             { string.append( '\r' ); }
  "\\\""                            { string.append( '\"' ); }
  "\\'"                             { string.append( '\'' ); }
  "\\\\"                            { string.append( '\\' ); }
  \\[0-3]?{OctDigit}?{OctDigit}     { char val = (char) Integer.parseInt(yytext().substring(1),8);
                                        string.append( val ); }

  /* Error cases */
  \\.                               { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}                  { throw new RuntimeException("Unterminated string at end of line"); }

}

<CHARLITERAL> {
  {SingleCharacter}\'               { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character(yytext().charAt(0))); }

  /* Escape sequences */
  "\\b"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\b'));}
  "\\t"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\t'));}
  "\\n"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\n'));}
  "\\f"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\f'));}
  "\\r"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\r'));}
  "\\\""\'                          { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\"'));}
  "\\'"\'                           { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\''));}
  "\\\\"\'                          { yybegin(YYINITIAL); return symbol(sym.CHARACTER_LITERAL, new Character('\\')); }
  \\[0-3]?{OctDigit}?{OctDigit}\'   { yybegin(YYINITIAL);
                                        int val = Integer.parseInt(yytext().substring(1,yylength()-1),8);
										return symbol(sym.CHARACTER_LITERAL, new Character((char)val)); }

  /* Error cases */
  \\.                               { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}                  { throw new RuntimeException("Unterminated character literal at end of line"); }

}