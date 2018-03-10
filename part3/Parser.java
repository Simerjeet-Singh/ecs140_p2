/* *** This file is given as part of the programming assignment. *** */

import java.util.*;

public class Parser {


	// tok is global to all these parsing methods;
	// scan just calls the scanner's scan method and saves the result in tok.
	private Token tok; // the current token
	LinkedList<String> Scope = new LinkedList<String>();
	int currScope = 100000;
	int inScope = currScope;

	private void scan() {
		tok = scanner.scan();
	}

	private Scan scanner;

	Parser(Scan scanner) {
		this.scanner = scanner;
		scan();

		Scope.addFirst(Integer.toString(currScope));
		//System.out.println(tok.kind);
		program();

		if( tok.kind != TK.EOF )
		parse_error("junk after logical end of program");
	}

	private void program() {
		//System.out.println("in program");
		block();
	}

	private void block(){
		//System.out.println("in block");
		declaration_list();
		statement_list();
	}

	private void declaration_list() {
		///System.out.println("Inside declaration_list");
		// below checks whether tok is in first set of declaration.
		// here, that's easy since there's only one token kind in the set.
		// in other places, though, there might be more.
		// so, you might want to write a general function to handle that.
		while( is(TK.DECLARE) ) {
			//System.out.println("Inside declaration_List while");
			declaration();
		}
	}

	private void declaration() {
		//System.out.println("Inside declaration");
		mustbe(TK.DECLARE);
		//System.out.println("Declare" + currScope);
		updateTable(tok); //add to list if not present else give error
		//mustbe(TK.ID);
		//System.out.println("Inside declaration");
		while( is(TK.COMMA) ) {
			//System.out.println("Inside While of declaration");
			scan();
			updateTable(tok);
			//mustbe(TK.ID);
		}
	}

	private void statement_list() {
		while( is(TK.TILDE) | is(TK.ID) | is(TK.PRINT) | is(TK.DO) | is(TK.IF) )
		statement();
	}

	private void statement(){
		if(is(TK.TILDE) | is(TK.ID)){
			assignment();
		}
		else if(is(TK.PRINT)){
			print();
		}
		else if(is(TK.DO)){
			Do();
		}
		else if(is(TK.IF)){
			If();
		}
		else{
			System.out.println("Error in statement");
		}

	}

	private void assignment(){
		ref_id();
		mustbe(TK.ASSIGN);
		expr();
	}

	private void ref_id(){
		inScope = 100000;
		boolean goGlobe = true;
		if(is(TK.TILDE)){
			mustbe(TK.TILDE);
			if(is(TK.NUM)){
				goGlobe = false;
				inScope = currScope - Integer.parseInt(tok.string)*100000;
				mustbe(TK.NUM);
			}
			findVariable(tok, inScope,goGlobe);
			//mustbe(TK.ID);
		}
		else{
			findAnywhere(tok);
			//mustbe(TK.ID);
		}
	}

	private void expr(){
		term();
		while(is(TK.PLUS) | is(TK.MINUS)){
			addop();
			term();
		}
	}

	private void term(){
		factor();
		while(is(TK.TIMES) | is(TK.DIVIDE)){
			multop();
			factor();
		}
	}

	private void factor(){
		if(is(TK.LPAREN)){
			mustbe(TK.LPAREN);
			expr();
			mustbe(TK.RPAREN);
		}
		else if(is(TK.TILDE) | is(TK.ID)){
			ref_id();
		}
		else if(is(TK.NUM)){
			mustbe(TK.NUM);
		}
		else
		System.out.println("factor error");
	}

	private void multop(){
		if(is(TK.TIMES)) mustbe(TK.TIMES);
		else if(is(TK.DIVIDE)) mustbe(TK.DIVIDE);
		else System.out.println("Error in multop");
	}

	private void addop(){
		if(is(TK.PLUS)) mustbe(TK.PLUS);
		else if(is(TK.MINUS)) mustbe(TK.MINUS);
		else System.out.println("Error in addop");
	}

	private void print(){
		mustbe(TK.PRINT);
		expr();
	}

	private void Do(){
		mustbe(TK.DO);
		createBlock();
		guarded_command();
		deleteBlock();
		mustbe(TK.ENDDO);
	}

	private void guarded_command(){
		expr();
		mustbe(TK.THEN);
		block();
	}

	private void If(){
		mustbe(TK.IF);
		createBlock();
		guarded_command();
		//deleteBlock();
		while(is(TK.ELSEIF)){
			mustbe(TK.ELSEIF);
			//createBlock();
			guarded_command();
			//deleteBlock();
		}
		if(is(TK.ELSE)){
			mustbe(TK.ELSE);
			//createBlock();
			block();
			//deleteBlock();
		}

		//System.out.println(currScope);
		mustbe(TK.ENDIF);
		deleteBlock();

	}
	// is current token what we want?
	private boolean is(TK tk) {
		return tk == tok.kind;
	}

	// ensure current token is tk and skip over it.
	private void mustbe(TK tk) {
		if( tok.kind != tk ) {
			//System.out.println("Inside Mustbe");
			System.err.println( "mustbe: want " + tk + ", got " +
			tok);
			parse_error( "missing token (mustbe)" );
		}
		scan();
	}

	private void findAnywhere(Token tk){
		//System.out.println(" in findAnywhere");
		mustbe(TK.ID);
		if( Scope.indexOf(tk.string) != -1)
		{ // it is present so nothing to do	}
	}
	else {
		System.out.println(tk.string + " is an undeclared variable on line " + tk.lineNumber );
		System.exit(1);
	}
}


private void findVariable(Token tk, int inScope, boolean goGlobe){
	mustbe(TK.ID);
	// System.out.println("Just inside findV " + inScope + "curr " + currScope );
	LinkedList<String> temp = new LinkedList<String>();
	//System.out.println("Size " + inScope + " " +temp.size());
	temp.addFirst("ghoman");
	if(inScope!=currScope && (inScope>0) ){
		//System.out.println("findV getfirst while " + Scope.getFirst());
		while( !(Scope.getFirst()).equals( Integer.toString(inScope+100000) ) )
		{
			// System.out.println("Delete from Scope & first Element SCOPE:" + Scope.getFirst());
			temp.addFirst( (Scope.getFirst()) );
			Scope.remove( (Scope.getFirst()) );
		}

	}
	//System.out.println("findV " + (Scope.indexOf(tk.string)) + " " + (Scope.indexOf(Integer.toString(inScope))));

	if( Scope.indexOf(tk.string) < Scope.indexOf(Integer.toString(inScope))
	&& (Scope.indexOf(tk.string) != -1) )
	{

		if(inScope!= currScope && (inScope >0)){
			while( !(temp.getFirst()).equals("ghoman") ){
				// System.out.println("findV getfirst 2 while 2 " + (temp.getFirst()));

				Scope.addFirst( (temp.getFirst()) );
				temp.remove( (temp.getFirst()) );
				// System.out.println("If ID available addback " + Scope.getFirst());

			}
			// System.out.println("findV getfirst 2 outwhile " + Scope.getFirst());
			//System.out.println("While 1 ");
			// Scope.addFirst( (temp.getFirst()) );
			// temp.remove( (temp.getFirst()) );
		}
	}
	else {

		if(inScope!= currScope && (inScope >0)){
			while( !(temp.getFirst()).equals("ghoman") ){
				// System.out.println("findV getfirst 2 while 2 " + (temp.getFirst()));

				Scope.addFirst( (temp.getFirst()) );
				temp.remove( (temp.getFirst()) );
				// System.out.println("If ID available addback " + Scope.getFirst());

			}
			// System.out.println("findV getfirst 2 outwhile " + Scope.getFirst());
			//System.out.println("While 1 ");
			// Scope.addFirst( (temp.getFirst()) );
			// temp.remove( (temp.getFirst()) );
		}

		int givenNum = (currScope - inScope)/100000;
		if(givenNum>0 && !goGlobe)
			System.out.println("no such variable ~" + givenNum + tk.string + " on line " + tk.lineNumber );
		else if(givenNum>0 && goGlobe)
			System.out.println("no such variable ~" + tk.string + " on line " + tk.lineNumber );
			else
				System.out.println("no such variable ~" + tk.string + " on line " + tk.lineNumber );
	}
}

private void updateTable(Token tk){
	mustbe(TK.ID);
	//System.out.println("Error updatetable");
	if( Scope.indexOf(tk.string) < Scope.indexOf(Integer.toString(currScope))
	&& (Scope.indexOf(tk.string) != -1) )
	{
		//System.out.println("Error updatetable 2");
		System.out.println("redeclaration of variable " + tk.string);
	}
	else{
		Scope.addFirst(tk.string);
	}
}


private void createBlock(){
	// System.out.println("create" + currScope);
	currScope += 100000;
	// System.out.println("create" + currScope);
	Scope.addFirst( Integer.toString(currScope) );
	// System.out.println("create getfirst " + Scope.getFirst());

}

private void deleteBlock(){
	// System.out.println("delete" + currScope);
	// System.out.println("delete getfirst" + Scope.getFirst());
	while( !(Scope.getFirst()).equals(Integer.toString(currScope))){
		// System.out.println(Scope.getFirst());
		Scope.remove(Scope.getFirst());
	}
	// System.err.println( "deleteBlock ");
	Scope.remove(Scope.getFirst()); // remove the currentScope from the list
	currScope -= 100000;
}

private void parse_error(String msg) {
	System.err.println( "can't parse: line "
	+ tok.lineNumber + " " + msg );
	System.exit(1);
}
}
