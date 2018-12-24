/****************************************************/
/* File: analyze.c                                  */
/* Semantic analyzer implementation                 */
/* for the TINY compiler                            */
/* Compiler Construction: Principles and Practice   */
/* Kenneth C. Louden                                */
/****************************************************/

#include "globals.h"
#include "symtab.h"
#include "analyze.h"
#include "util.h"

ScopeList global;
static char g_name[] = "global";
static char * funcname = g_name;
static int location = 0;
static int flag = 0;
static int s_flag = 0;

/* Procedure traverse is a generic recursive 
 * syntax tree traversal routine:
 * it applies preProc in preorder and postProc 
 * in postorder to tree pointed to by t
 */
static void traverse( TreeNode * t,
               void (* preProc) (TreeNode *),
               void (* postProc) (TreeNode *) )
{ if (t != NULL)
  { preProc(t);
    { int i;
      for (i=0; i < MAXCHILDREN; i++)
        traverse(t->child[i],preProc,postProc);
    }
    postProc(t);
    traverse(t->sibling,preProc,postProc);
  }
}

/* nullProc is a do-nothing procedure to 
 * generate preorder-only or postorder-only
 * traversals from traverse
 */
static void nullProc(TreeNode * t)
{ if (t==NULL) return;
  else return;
}

static void symbolError(TreeNode *t , char *message) {
	fprintf(listing,"Symbol error at line %d : %s",t->lineno,message);
}

static void pop_K(TreeNode *t) {
	if(t->nodekind==StmtK) {
		if(t->kind.stmt == CompK) {
			pop_scope();
			ScopeList sc = get_top_scopelist();
			funcname = sc->name;
		}
	}
}

/* Procedure insertNode inserts 
 * identifiers stored in t into 
 * the symbol table 
 */
static void insertNode( TreeNode * t) { 
	switch (t->nodekind) { 
		case StmtK:
			switch (t->kind.stmt) { 
				case WhileK:
					flag=3;
					break;
				case IfK:
					if(t->child[2]) flag=1;
					funcname=".if";
					break;
				case CompK:
					if(s_flag==1) {
						s_flag=0;
						flag=0;
					} else{
						if(flag==1) { flag=2;}
						else if(flag==2) { funcname=".else"; flag=0; }
						else if(flag==3) { funcname=".while"; flag=0; }
						push_scope(create_scopelist(funcname));
					}
					t->scope = get_top_scopelist();
					break;
				default:
					break;
			}
			break;
		case ExpK:
			switch(t->kind.exp) {
				case OpK: break;
				case ConstK: break;
				case IdK:
				case ArrayK:
					if(st_lookup(t->attr.name)) { addLine(t->attr.name,t->lineno);}
					else { symbolError(t,"Undeclared Symbol\n"); break; }
				default: 
					break;
			}
			break;
		case DecK:
			switch(t->kind.dec) {
				case FuncK:
					if(st_lookup_excluding_parent(funcname,t->attr.name)) {
						symbolError(t,"Function already declared in same scope");
						break;
					}
					st_insert(funcname,t->attr.name,t->type,t->lineno,addLocation(),t);
					push_scope(create_scopelist(t->attr.name));
					funcname = t->attr.name;
					s_flag=1;
					break;
				case VarK:
					if(st_lookup_excluding_parent(funcname,t->attr.name)) {
						symbolError(t,"Function already declared in same scope");
						break;
					}
					st_insert(funcname,t->attr.name,t->type,t->lineno,addLocation(),t);
					break;
				case ParamK:
					if(t->type != Void) {
						st_insert(funcname,t->attr.name,t->type,t->lineno,addLocation(),t);
					}
					break;
				default: 
					break;
			}
			break;
		default: break;
		}
}

static void inoutput() {
		TreeNode *input;
		input = newDecNode(VarK);
		input->type=Integer;
		input->kind.dec=FuncK;
		input->attr.name=(char*)malloc(strlen("input")+1);
		strcpy(input->attr.name,"input");
		input->child[0]=NULL;
		input->child[1]=NULL;

		TreeNode *output;
		output = newDecNode(VarK);
		output->type=Void;
		output->kind.dec=FuncK;
		output->attr.name=(char*)malloc(strlen("output")+1);
		strcpy(output->attr.name,"output");
		output->child[0]=NULL;
		output->child[1]=NULL;

		st_insert(funcname,input->attr.name,input->type,0,addLocation(),input);
		st_insert(funcname,output->attr.name,output->type,0,addLocation(),output);
}

/* Function buildSymtab constructs the symbol 
 * table by preorder traversal of the syntax tree
 */
void buildSymtab(TreeNode * syntaxTree) { 
	global = create_scopelist(funcname);
	push_scope(global);
	inoutput();
	traverse(syntaxTree,insertNode,pop_K);
	pop_scope();  
	if (TraceAnalyze) { 
		fprintf(listing,"\nSymbol table:\n\n");
		printSymTab(listing);
	}
}

static void typeError(TreeNode * t, char * message)
{ fprintf(listing,"Type error at line %d: %s\n",t->lineno,message);
  Error = TRUE;
}

static void push_K(TreeNode *t) {
	switch(t->nodekind) {
		case StmtK:
			switch(t->kind.stmt) {
				case IfK: break;
				case WhileK: break;
				case CompK: push_scope(t->scope); break;
				default: break;
			}
			break;
		case DecK:
			switch(t->kind.dec) {
				case FuncK: funcname=t->attr.name; break;
				default: break;
			}
		default: break;
	}
}

/* Procedure checkNode performs
 * type checking at a single tree node
 */
static void checkNode(TreeNode * t) { 
	switch (t->nodekind) { 
		case StmtK:
			switch(t->kind.stmt) {
				case IfK:
					if(t->child[0]->type==Void)
						typeError(t->child[0],"Void is only avaliable for function");
					break;
				case WhileK:
					if(t->child[0]->type==Void)
						typeError(t->child[0],"void is only available for function");
					break;
				case CompK:
					pop_scope();
					funcname=get_top_scopelist()->name;
					break;
				case ReturnK:
				{
					ExpType functype=st_lookup(funcname)->type;
					if(functype==Void && (t->child[0]!=NULL || t->child[0]->type != Void)) {
						typeError(t,"void function should return void");
					}
					else if(functype==Integer && (t->child[0]==NULL || t->child[0]->type!=Integer)) {
						typeError(t,"integer function should return integer");
					}
					break;
				}
				case CallK:
				{
					BucketList b = st_lookup(t->attr.name);
					if(b==NULL) break;
					TreeNode *args = t->child[0];
					TreeNode *params = b->treenode->child[0];
					while(params!=NULL) {
						if(args==NULL) {
							typeError(args,"#args and #params should be same");
							break;
						} else if(args->type==Void) {
							typeError(args,"void is only available for function");
							break;
						} else if(args->type!=params->type) {
							typeError(args,"args.type and params.type should be same");
							break;
						} else {
							args=args->sibling;
							params=params->sibling;	
						}
					}
					t->type=b->type;
					break;
				}
				default: break;
			}
			break;		
		case ExpK:
			switch(t->kind.exp) {
				case OpK:
				{
					ExpType left = t->child[0]->type;
					ExpType right = t->child[1]->type;
					TokenType op = t->attr.op;
					if(left==Void)
						typeError(t->child[0],"void is only available for function");
					if(right==Void)
						typeError(t->child[1],"void is only available for function");
					if(op==ASSIGN) {
						if(left!=right) {	
							typeError(t->child[0],"two operand should have same type when assign");
						} else { t->type=t->child[0]->type; }
					} else {
						if(left!=right) { 	
							typeError(t->child[0],"two operand should have same type");
						} else if(left==Array && right == Array) {	
							typeError(t,"two operand should not be array");
						} else if(op==MINUS && left==Integer && right==Array) {
							typeError(t,"minus no int-array");
						} else if((op==TIMES || op==OVER) && (left==Array || right==Array)) {
							typeError(t,"no times or over opearation on array");
						} else { t->type=Integer; }
						break;
					}
				}
			case ConstK:
				t->type=Integer;
				break;
			case IdK:
			{
				BucketList b = st_lookup(t->attr.name);
				if(b==NULL)
					break;
				t->type = b->type;
				break;
			}
			case ArrayK:
			{
				BucketList b = st_lookup(t->attr.name);
				if(b==NULL) break;
				if(t->child[0]->type!=Integer) {
					typeError(t->child[0],"exp should be Integer");
				} else { t->type=Integer; }
				break;
			}
			default: break;
			}
		case DecK: break;
		default: break;
	}
}

/* Procedure typeCheck performs type checking 
 * by a postorder syntax tree traversal
 */
void typeCheck(TreeNode * syntaxTree) { 
	push_scope(global);	
	traverse(syntaxTree,push_K,checkNode);
	pop_scope();
}
