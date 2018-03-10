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
		System.out.println("#include <stdio.h>");
		System.out.println("int main()");
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
		System.out.println("{");
		declaration_list();
		statement_list();
		System.out.println("}");

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

		while( is(TK.COMMA) ) {
			//System.out.println("Inside While of declaration");
			scan();
			updateTable(tok);
			//mustbe(TK.ID);
		}
		// System.out.println(";");
	}

	private void statement_list() {
		while( is(TK.TILDE) | is(TK.ID) | is(TK.PRINT) | is(TK.DO) | is(TK.IF) | is(TK.FOR))
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
		else if(is(TK.FOR)){
			For();
		}
		// else{
		// 	System.out.println("Error in statement");
		// }

	}

	private void For(){
		mustbe(TK.FOR);
		createBlock();
		System.out.print("for(");
		if(is(TK.ID)){
			assignment();
		}
		mustbe(TK.THEN);
		expr();
		System.out.println("<= 0;");
		mustbe(TK.THEN);
		increment();
		System.out.print(")");
		if(is(TK.THEN)) {
			mustbe(TK.THEN);
			block();
		}
		deleteBlock();
		mustbe(TK.ENDFOR);
	}

	private void increment(){
		ref_id();
		mustbe(TK.ASSIGN);
		System.out.print("=");
		expr();
	}

	private void assignment(){
		ref_id();
		String dummy = tok.string;
		mustbe(TK.ASSIGN);
		System.out.print(" " + dummy + " ");
		expr();
		System.out.println(";");
	}

	private void ref_id(){
		inScope = 100000;
		boolean goGlobe = true;
		if(is(TK.TILDE)){
			mustbe(TK.TILDE);

			if(is(TK.NUM)){
				goGlobe = false;
				inScope = (currScope - Integer.parseInt(tok.string)*100000)/100000;
				int xNum = Integer.parseInt(tok.string);
				int tempo = 0;
				String xes = "";
				while(inScope > 0)
				{
					xes = xes + "x";
					// System.out.println(xes);
					tempo++;
					inScope--;
				}
				if(inScope == 0)
					inScope = tempo*100000;
				else inScope *= 100000;
				if( (Integer.parseInt(tok.string)) == 0){
					int txes = currScope/100000;
					String xxes = "";
					while(txes > 0)
					{
						xxes = xxes + "x";
						// System.out.println(xes);
						txes--;
					}
					System.out.print(xxes + "_");
				}
				else System.out.print(xes + "_");
				mustbe(TK.NUM);
			}

			else{

				System.out.print("x_");
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
			System.out.print("(");
			expr();
			mustbe(TK.RPAREN);
			System.out.print(")");
		}
		else if(is(TK.TILDE) | is(TK.ID)){
			ref_id();
		}
		else if(is(TK.NUM)){
			System.out.print(tok.string);
			mustbe(TK.NUM);
		}
	}

	private void multop(){
		if(is(TK.TIMES)) {
			System.out.print(tok.string);
			mustbe(TK.TIMES);
		}
		else if(is(TK.DIVIDE)) {
			System.out.print(tok.string);
			mustbe(TK.DIVIDE);
		}
	}

	private void addop(){

		if(is(TK.PLUS)) {
			System.out.print(tok.string);
			mustbe(TK.PLUS);
		}
		else if(is(TK.MINUS)) {
			System.out.print(tok.string);
			mustbe(TK.MINUS);
		}
	}

	private void print(){
		mustbe(TK.PRINT);
		System.out.print(" printf(\"%d\\n\", ");
		expr();
		System.out.println(");");
	}

	private void Do(){
		mustbe(TK.DO);
		System.out.print("while(");
		createBlock();
		guarded_command();
		deleteBlock();
		mustbe(TK.ENDDO);
	}

	private void guarded_command(){
		expr();
		System.out.print(" <=0 )"); // if argument close
		mustbe(TK.THEN);
		block();
	}

	private void If(){
		mustbe(TK.IF);
		System.out.print("if(");
		createBlock();
		guarded_command();
		while(is(TK.ELSEIF)){
			mustbe(TK.ELSEIF);
			System.out.print("else if(");
			guarded_command();
		}
		if(is(TK.ELSE)){
			mustbe(TK.ELSE);
			System.out.print("else ");
			block();
		}

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
		mustbe(TK.ID);

		if( Scope.indexOf(tk.string) != -1)
		{
			findWhichX(tk);
		}
		else {
			System.err.println(tk.string + " is an undeclared variable on line " + tk.lineNumber );
			System.exit(1);
		}
	}

	private void findWhichX(Token tk){
		int findingScope = currScope;
		boolean aia = false;
		LinkedList<String> temp = new LinkedList<String>();
		temp.addFirst("ghoman");

		while(!aia){
			if( Scope.indexOf(tk.string) < Scope.indexOf(Integer.toString(findingScope))
			&& (Scope.indexOf(tk.string) != -1) )
			{
				aia = true;
				while( !(temp.getFirst()).equals("ghoman") ){
					Scope.addFirst( (temp.getFirst()) );
					temp.remove( (temp.getFirst()) );
				}

				int xNum = findingScope/100000;
				String xes = "";
				while(xNum > 0)
				{
					xes = xes + "x";
					xNum--;
				}
				System.out.print(xes + "_" + tk.string);
			}
			else{

				while( !(Scope.getFirst()).equals( Integer.toString(findingScope) ) )
				{
					temp.addFirst( (Scope.getFirst()) );
					Scope.remove( (Scope.getFirst()) );
				}
				findingScope -= 100000;
			}
		}
	}

	private void findVariable(Token tk, int inScope, boolean goGlobe){
		mustbe(TK.ID);
		LinkedList<String> temp = new LinkedList<String>();
		temp.addFirst("ghoman");
		if(inScope!=currScope && (inScope>0) ){
			while( !(Scope.getFirst()).equals( Integer.toString(inScope+100000) ) )
			{
				temp.addFirst( (Scope.getFirst()) );
				Scope.remove( (Scope.getFirst()) );
			}
		}
		if( Scope.indexOf(tk.string) < Scope.indexOf(Integer.toString(inScope))
		&& (Scope.indexOf(tk.string) != -1) )
		{
			System.out.print(tk.string);
			if(inScope!= currScope && (inScope >0)){
				while( !(temp.getFirst()).equals("ghoman") ){
					Scope.addFirst( (temp.getFirst()) );
					temp.remove( (temp.getFirst()) );
				}
			}
		}
		else {
			// add strings back to the scope list
			if(inScope!= currScope && (inScope >0)){
				while( !(temp.getFirst()).equals("ghoman") ){
					Scope.addFirst( (temp.getFirst()) );
					temp.remove( (temp.getFirst()) );
				}
			}

			int givenNum = (currScope - inScope)/100000;

			if( givenNum>0 && !goGlobe ){
				System.err.println("no such variable ~" + givenNum + tk.string + " on line " + tk.lineNumber );
				System.exit(1);
			}
			else if(givenNum>0 && goGlobe){
				System.err.println("no such variable ~" + tk.string + " on line " + tk.lineNumber );
				System.exit(1);
			}
			else{
				System.err.println("no such variable ~" + tk.string + " on line " + tk.lineNumber );
				System.exit(1);
			}
		}
	}

	private void updateTable(Token tk){
		mustbe(TK.ID);
		if( Scope.indexOf(tk.string) < Scope.indexOf(Integer.toString(currScope))
		&& (Scope.indexOf(tk.string) != -1) )
		{
			System.err.println("redeclaration of variable " + tk.string);
		}
		else{
			System.out.print("int ");
			Scope.addFirst(tk.string);
			int xNum = currScope/100000;
			String xes = "";
			while(xNum > 0)
			{
				xes = xes + "x";
				xNum--;
			}

			System.out.println(xes + "_" + tk.string + ";");
		}
	}


	private void createBlock(){
		currScope += 100000;
		Scope.addFirst( Integer.toString(currScope) );

	}

	private void deleteBlock(){
		while( !(Scope.getFirst()).equals(Integer.toString(currScope))){
			Scope.remove(Scope.getFirst());
		}
		Scope.remove(Scope.getFirst()); // remove the currentScope from the list
		currScope -= 100000;
	}

	private void parse_error(String msg) {
		System.err.println( "can't parse: line "
		+ tok.lineNumber + " " + msg );
		System.exit(1);
	}
}
