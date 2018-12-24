/****************************************************/
/* File: symtab.c                                   */
/* Symbol table implementation for the TINY compiler*/
/* (allows only one symbol table)                   */
/* Symbol table is implemented as a chained         */
/* hash table                                       */
/* Compiler Construction: Principles and Practice   */
/* Kenneth C. Louden                                */
/****************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "symtab.h"
#include "globals.h"
/* the hash function */
static int hash ( char * key )
{ int temp = 0;
  int i = 0;
  while (key[i] != '\0')
  { temp = ((temp << SHIFT) + key[i]) % SIZE;
    ++i;
  }
  return temp;
}

char *typeString[] = {"void","int","int[]"};
static ScopeList scopelist[500];
static ScopeList scopestack[500];
static int sizeof_scopelist = 0;
static int sizeof_scopestack = 0;
static int location[500];

ScopeList get_top_scopelist() { return scopestack[sizeof_scopestack-1]; }
ScopeList create_scopelist(char * name) {
	ScopeList slist = (ScopeList)malloc(sizeof(struct ScopeListRec));
	slist->name=name;
	slist->parent = get_top_scopelist();
	scopelist[sizeof_scopelist++] = slist;
	return slist;
}

void push_scope(ScopeList scope) {
	scopestack[sizeof_scopestack] = scope;
	location[sizeof_scopestack++] = 0;
}

void pop_scope() { sizeof_scopestack -= 1; }
int addLocation() { return location[sizeof_scopestack-1]++; }

BucketList st_lookup(char * name) {
	int h = hash(name);
	ScopeList slist = get_top_scopelist();
	while(slist) {
		BucketList blist = slist->bucket[h];
		while(blist!=NULL) {
			if(strcmp(blist->name,name)==0)
				return blist;
			blist = blist->next;
		}
		slist = slist->parent;
	}
	return NULL;
}

BucketList st_lookup_excluding_parent(char * scope , char * name) {
	int h = hash(name);
	ScopeList slist = get_top_scopelist();
	if(strcmp(slist->name,scope)) {
		BucketList blist = slist->bucket[h];
		while(blist!=NULL) {
			if(strcmp(blist->name,name)==0)
				return blist;
			blist = blist->next;
		}
	}
	return NULL;
}




/* Procedure st_insert inserts line numbers and
 * memory locations into the symbol table
 * loc = memory location is inserted only the
 * first time, otherwise ignored
 */
void st_insert(char * scope , char * name , ExpType type , int lineno, int loc , TreeNode *t ) { 
	int h = hash(name);
	ScopeList slist = get_top_scopelist();
	while(slist) {
		if(strcmp(slist->name,scope)==0) break;
		slist = slist->parent;
	}
	BucketList blist = slist->bucket[h];
	
	while((blist!=NULL) && (strcmp(blist->name,name)!=0)) blist = blist->next;
	if(blist==NULL) {
		blist = (BucketList)malloc(sizeof(struct BucketListRec));
		blist->name = name;
		blist->lines = (LineList)malloc(sizeof(struct LineListRec));
		blist->lines->lineno=lineno;
		blist->lines->next = NULL;
		blist->type = type;
		blist->memloc = loc;
		blist->next = slist->bucket[h];
		blist->treenode=t;
		slist->bucket[h]=blist;
	} else {
		LineList llist = blist->lines;
		while(llist->next != NULL) llist = llist->next;
		llist->next = (LineList)malloc(sizeof(struct LineListRec));
		llist->next->lineno = lineno;
		llist->next->next = NULL;
	}
}

void addLine(char * name , int lineno) {
	BucketList blist = st_lookup(name);
	LineList llist = blist->lines;
	while(llist->next != NULL) llist = llist->next;
	llist->next = (LineList)malloc(sizeof(struct LineListRec));
	llist->next->lineno = lineno;
	llist->next->next = NULL;
}

void printSymTab(FILE * listing) { 
	int i,j;
	fprintf(listing,"Variable Name\tType\tLocation\tScope\tLine Numbers\n");
	fprintf(listing,"-------------\t-----\t --------\t------\t------------\n");
  
	for(i = 0 ; i < sizeof_scopelist ; i++) {
		ScopeList slist = scopelist[i];
		for(j = 0 ; j < SIZE ; ++j) {
			if(slist->bucket[j] != NULL) {
				BucketList blist = slist->bucket[j];
				while(blist != NULL) {
					LineList llist = blist->lines;
					fprintf(listing,"%s\t\t",blist->name);						
					fprintf(listing,"%s\t\t",typeString[blist->type]);		
					fprintf(listing,"%d\t",blist->memloc);		
					fprintf(listing,"%s\t\t",slist->name);
					while(llist != NULL) {
						fprintf(listing,"%d,",llist->lineno);
						llist = llist->next;
					}
					fprintf(listing,"\n");
					llist = llist->next;
				}
			}
		}	
	}
}

		

