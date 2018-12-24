/****************************************************/
/* File: tiny.y                                     */
/* The TINY Yacc/Bison specification file           */
/* Compiler Construction: Principles and Practice   */
/* Kenneth C. Louden                                */
/****************************************************/
%{
#define YYPARSER /* distinguishes Yacc output from other code files */

#include "globals.h"
#include "util.h"
#include "scan.h"
#include "parse.h"

#define YYSTYPE TreeNode *
static char * savedName; /* for use in assignments */
static int savedNumber;
static int savedLineNo;  /* ditto */
static TreeNode * savedTree; /* stores syntax tree for later return */
static int yylex(void); // added 11/2/11 to ensure no conflict with lex
static TreeNode *l=NULL;
static TreeNode *s=NULL;
static TreeNode *d=NULL;
static TreeNode *p=NULL;
static TreeNode *a=NULL;
%}

%token IF THEN ELSE END REPEAT UNTIL READ WRITE WHILE INT VOID RETURN
%token ID NUM 
%token ASSIGN LE LT GT GE EQ NE PLUS MINUS TIMES OVER SEMI COMMA
%token LPAREN RPAREN LBRACE RBRACE LCURLY RCURLY
%token ERROR 

%% /* Grammar for TINY */

program     : dec_list
                 { savedTree = $1;} 
            ;
dec_list	: dec_list dec { 
				YYSTYPE t = $1;
				if (t != NULL) {
					while(t->sibling!=NULL)
						t=t->sibling;
					t->sibling=$2;
					$$=$1;
				} else {
					$$=$2;
				}
			}
			| dec { $$=$1;}
			;
dec			: var_dec { $$=$1;}
			| func_dec { $$=$1;}
			;
var_dec		: type_spec tid SEMI {
				$$=newDecNode(VarK);
				$$->child[0]=$1;
				$$->lineno=lineno;
				$$->attr.name=savedName;
			}
			| type_spec tid LBRACE tnumber RBRACE SEMI {
				$$=newDecNode(ArrK);
				$$->child[0]=$1;
				$$->lineno=lineno;
				$$->attr.arrayattr.name=savedName;
				$$->attr.arrayattr.size=savedNumber;
				$$->attr.arrayattr.type=$1->type;
			}
			;
type_spec	: INT {
				$$=newTypeNode(TypenameK);
				$$->type=Integer;
			}
			| VOID {
				$$=newTypeNode(TypenameK);
				$$->type=Void;
			}
			;
func_dec	: type_spec tid {
				$$=newDecNode(FuncK);
				$$->lineno=lineno;
				$$->attr.name=savedName;
			} LPAREN params RPAREN comp_stmt {
				$$=$3;
				$$->child[0]=$1;
				$$->child[1]=$5;
				$$->child[2]=$7;
			}
			;
params		: param_list { $$=$1; }
			| VOID {
				$$=newTypeNode(TypenameK);
				$$->attr.type=Void;
			}
			;
param_list	: param_list COMMA param {
				YYSTYPE t=$1;
				if(t!=NULL) {
					while(t->sibling!=NULL)
						t=t->sibling;
					t->sibling=$3;
					$$=$1;
				} else {
					$$=$3;
				}
			}
			| param { $$=$1; }
			;
param		: type_spec tid {
				$$=newParamNode(SingleParamK);
				$$->attr.name=savedName;
				$$->child[0]=$1;
			}
			| type_spec tid LBRACE RBRACE {
				$$=(TreeNode *)newParamNode(ArrayParamK);
				$$->child[0]=$1;
				$$->attr.arrayattr.name=savedName;
				$$->attr.arrayattr.type=$1->type;
			}
			;
comp_stmt	: LCURLY local_dec stmt_list RCURLY {
				$$=newStmtNode(CompK);
				$$->child[0]=$2;
				$$->child[1]=$3;
			}
			;
local_dec	: local_dec var_dec {
				YYSTYPE t=$1;
				if(t!=NULL) {
					while(t->sibling!=NULL)
						t=t->sibling;
					t->sibling=$2;
					$$=$1;
				} else {
					$$=$2;
				}
			}
			| { $$=NULL;}
			;
stmt_list	: stmt_list stmt {
				YYSTYPE t = $1;
				if(t!=NULL) {
					while(t->sibling!=NULL)
						t=t->sibling;
					t->sibling=$2;
					$$=$1;
				} else {
					$$=$2;
				}
			}
			| { $$=NULL;}
			;
stmt		: exp_stmt	{ $$=$1;}
			| comp_stmt	{ $$=$1;}
			| sel_stmt	{ $$=$1;}
			| iter_stmt	{ $$=$1;}
			| ret_stmt	{ $$=$1;}
			;
exp_stmt	: exp SEMI	{ $$=$1;}
			| SEMI		{ $$=NULL;}
			;
sel_stmt	: IF LPAREN exp RPAREN stmt ELSE stmt {
				$$=newStmtNode(IfK);
				$$->child[0]=$3;
				$$->child[1]=$5;
				$$->child[2]=$7;
			}
			| IF LPAREN exp RPAREN stmt {
				$$=newStmtNode(IfK);
				$$->child[0]=$3;
				$$->child[1]=$5;
			}
			;
iter_stmt	: WHILE LPAREN exp RPAREN stmt {
				$$=newStmtNode(WhileK);
				$$->child[0]=$3;
				$$->child[1]=$5;
			}
			;
ret_stmt	: RETURN SEMI {
				$$=newStmtNode(ReturnK);	
				$$->child[0]=NULL;
			}
			| RETURN exp SEMI {
				$$=newStmtNode(ReturnK);	
				$$->child[0]=$2;
			}
			;
exp			: var ASSIGN exp {
				$$=newStmtNode(AssignK);	
				$$->child[0]=$1;
				$$->child[1]=$3;
			}
			| simple_exp {$$=$1;}
			;
var			: tid {
				$$=newExpNode(IdK);	
				$$->attr.name=savedName;
			}
			| tid {
				$$=newExpNode(IdK);
				$$->attr.name=savedName;
			} LBRACE exp RBRACE {
				$$=$2;
				$$->child[0]=$4;
			}
			;
simple_exp	: additive_exp relop additive_exp {
				$$=newExpNode(OpK);
				$$->child[0]=$1;
				$$->child[1]=$3;
				$$->attr.op=$2->attr.op;
			}
			| additive_exp {$$=$1;}
			;
relop		:	LE {
				$$=newExpNode(OpK);
				$$->attr.op=LE;
			}
			|	LT {
				$$=newExpNode(OpK);
				$$->attr.op=LT;
			}
			|	GT {
				$$=newExpNode(OpK);
				$$->attr.op=GT;
			}
			|	GE {
				$$=newExpNode(OpK);	
				$$->attr.op=GE;
			}
			|	EQ {
				$$=newExpNode(OpK);	
				$$->attr.op=EQ;
			}
			|	NE {
				$$=newExpNode(OpK);
				$$->attr.op=NE;
			}
			;
additive_exp	:   additive_exp addop term { 
					$$=newExpNode(OpK);
					$$->child[0]=$1;
					$$->child[1]=$3;
					$$->attr.op=$2->attr.op;
				}
				| term {$$ = $1;}
				;
addop	:   PLUS {
			$$ = newExpNode(OpK);
			$$->attr.op = PLUS;
		}
		|   MINUS {
			$$ = newExpNode(OpK);
			$$->attr.op = MINUS;
		}
		;
term    :   term mulop factor {
			$$ = newExpNode(OpK);
			$$->child[0] = $1;
			$$->child[1] = $3;
			$$->attr.op = $2->attr.op;
		}
		|   factor {$$ = $1;}
		;
mulop   :   TIMES {
			$$ = newExpNode(OpK);
			$$ -> attr.op = TIMES;
		}
		|   OVER {
			$$ = newExpNode(OpK);
			$$ -> attr.op = OVER;
		}
		;
factor	:   LPAREN exp RPAREN	{ $$=$2;}
		|	var					{ $$=$1;}
		|	call				{ $$=$1;}
		|	NUM					{ $$=newExpNode(ConstK); $$->attr.val=atoi(tokenString); }
		;
call	:	tid	{ $$=newStmtNode(CallK); $$->attr.name=savedName; }
			LPAREN args RPAREN { $$=$2; $$->child[0]=$4;}
		;
args	: arg_list { $$=$1;}
		| {$$=NULL;}
		;
arg_list	:	arg_list COMMA exp {
				YYSTYPE t = $1;
				if(t!=NULL) {
					while(t->sibling!=NULL)
						t=t->sibling;
					t->sibling=$3;
					$$=$1;
				} else {		
					$$=$3;
				}
			}
			| exp { $$=$1;}
			;
tid	: ID {
		savedName=copyString(tokenString);
		savedLineNo=lineno;
	}
	;
tnumber	: NUM {
			savedNumber=atoi(tokenString);
			savedLineNo=lineno;
		}
		;
%%

int yyerror(char * message)
{ 
fprintf(listing,"Syntax error at line %d: %s\n",lineno,message);
  fprintf(listing,"Current token:");
  printToken(yychar,tokenString);
  Error = TRUE;
  return 0;
}

/* yylex calls getToken to make Yacc/Bison output
 * compatible with ealier versions of the TINY scanner
 */
static int yylex(void)
{ return getToken(); }

TreeNode * parse(void)
{ yyparse();
  return savedTree;
}

