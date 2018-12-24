/* A Bison parser, made by GNU Bison 3.0.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2013 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

#ifndef YY_YY_Y_TAB_H_INCLUDED
# define YY_YY_Y_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token type.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    IF = 258,
    THEN = 259,
    ELSE = 260,
    END = 261,
    REPEAT = 262,
    UNTIL = 263,
    READ = 264,
    WRITE = 265,
    WHILE = 266,
    INT = 267,
    VOID = 268,
    RETURN = 269,
    ID = 270,
    NUM = 271,
    ASSIGN = 272,
    LE = 273,
    LT = 274,
    GT = 275,
    GE = 276,
    EQ = 277,
    NE = 278,
    PLUS = 279,
    MINUS = 280,
    TIMES = 281,
    OVER = 282,
    SEMI = 283,
    COMMA = 284,
    LPAREN = 285,
    RPAREN = 286,
    LBRACE = 287,
    RBRACE = 288,
    LCURLY = 289,
    RCURLY = 290,
    ERROR = 291
  };
#endif
/* Tokens.  */
#define IF 258
#define THEN 259
#define ELSE 260
#define END 261
#define REPEAT 262
#define UNTIL 263
#define READ 264
#define WRITE 265
#define WHILE 266
#define INT 267
#define VOID 268
#define RETURN 269
#define ID 270
#define NUM 271
#define ASSIGN 272
#define LE 273
#define LT 274
#define GT 275
#define GE 276
#define EQ 277
#define NE 278
#define PLUS 279
#define MINUS 280
#define TIMES 281
#define OVER 282
#define SEMI 283
#define COMMA 284
#define LPAREN 285
#define RPAREN 286
#define LBRACE 287
#define RBRACE 288
#define LCURLY 289
#define RCURLY 290
#define ERROR 291

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;

int yyparse (void);

#endif /* !YY_YY_Y_TAB_H_INCLUDED  */
