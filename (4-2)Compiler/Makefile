#
# Makefile for TINY
# Gnu C Version
# K. Louden 2/3/98
#

CC = gcc

CFLAGS = -Wall -g

OBJS = y.tab.o lex.yy.o main.o util.o symtab.o analyze.o

cminus: $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o cminus -lfl

main.o: main.c globals.h util.h scan.h analyze.h
	$(CC) $(CFLAGS) -c main.c

util.o: util.c util.h globals.h
	$(CC) $(CFLAGS) -c util.c

symtab.o: symtab.c symtab.h globals.h
	$(CC) $(CFLAGS) -c symtab.c

analyze.o: analyze.c globals.h symtab.h analyze.h util.h
	$(CC) $(CFLAGS) -c analyze.c

lex.yy.o: cminus.l scan.h util.h globals.h
	flex -o lex.yy.c cminus.l
	$(CC) $(CFLAGS) -c lex.yy.c -lfl

y.tab.o: cminus.y globals.h util.h scan.h
	bison -d cminus.y --yacc
	$(CC) $(CFLAGS) -c y.tab.c

clean:
	-rm lex.yy.c
	-rm y.tab.c y.tab.h
	-rm cminus
	-rm $(OBJS)


