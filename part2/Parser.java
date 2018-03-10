/* *** This file is given as part of the programming assignment. *** */

public class Parser {


	// tok is global to all these parsing methods;
	// scan just calls the scanner's scan method and saves the result in tok.
	private Token tok; // the current token
	private void scan() {
		tok = scanner.scan();
	}

	private Scan scanner;
	Parser(Scan scanner) {
		this.scanner = scanner;
		scan();
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
		mustbe(TK.ID);
		//System.out.println("Inside declaration");
		while( is(TK.COMMA) ) {
			//System.out.println("Inside While of declaration");
			scan();
			mustbe(TK.ID);
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
		 System.out.println("Pai gia panga");
		}

	}

	private void assignment(){
		ref_id();
		mustbe(TK.ASSIGN);
		expr();
	}

	private void ref_id(){
		if(is(TK.TILDE)){
			mustbe(TK.TILDE);
			if(is(TK.NUM)){
				mustbe(TK.NUM);
			}
			mustbe(TK.ID);
		}
		else{
			mustbe(TK.ID);
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
		guarded_command();
		mustbe(TK.ENDDO);
	}

	private void guarded_command(){
		expr();
		mustbe(TK.THEN);
		block();
	}

	private void If(){
		mustbe(TK.IF);
		guarded_command();
		while(is(TK.ELSEIF)){
			mustbe(TK.ELSEIF);
			guarded_command();
		}
		if(is(TK.ELSE)){
			mustbe(TK.ELSE);
			block();
		}
		mustbe(TK.ENDIF);
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

	private void parse_error(String msg) {
		System.err.println( "can't parse: line "
		+ tok.lineNumber + " " + msg );
		System.exit(1);
	}
}
