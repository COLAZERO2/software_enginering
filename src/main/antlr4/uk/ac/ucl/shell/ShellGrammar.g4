grammar ShellGrammar;

/*
 * Parser Rules
 */

command : (pipe | call) seq* ;

seq : ';' (pipe | call) ;

pipe : (call '|')+ call ;

call : (WHITESPACES)* (redirection WHITESPACES)* argument ((WHITESPACES (redirection | argument))*) (WHITESPACES)* ;

argument : (quoted | NONSPECIAL)+ ;

redirection : '<' (WHITESPACES)* argument | '>' (WHITESPACES)* argument ;

quoted : SINGLEQUOTED | DOUBLEQUOTED | BACKQUOTED ;

/*
 * Lexer Rules
 */

WHITESPACES : (' ' | '\t')+;
NONSPECIAL : (~[ `'";<>|\n\r\t\\] | (('\\') .))+;
BACKQUOTED : '`'(~[\n\r`\\] | ('\\') ~[`])+'`';
SINGLEQUOTED : '\''(~[\n\r'\\] | (('\\') .))+'\'';
DOUBLEQUOTED : '"' ( (~[\n\r`"\\] | (('\\') .)) | ('`'(~[\n\r`\\] | (('\\') ~[`]))*'`') )* '"';
