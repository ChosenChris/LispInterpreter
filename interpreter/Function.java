package interpreter;

import tokenizer.*;

import java.util.LinkedList;

import errorHandling.*;



/**
 * Objekte dieser Klasse speichern alle wichtigen Elemente einer Funktion.
 * 
 * @author	Christian S
 * @version	16.03.2021
 */
public class Function {
	/**
	 * Speichert den Namen der Funktion.
	 */
	private String sName;
	
	/**
	 * Speichert die Liste an Parametern
	 */
	private LinkedList<Atom> lParametersObj;
	
	/**
	 * Speichert alle Ausdruecke als Tokens
	 */
	private LinkedList<LinkedList<Token>> llExpressionsObj;
	
	
	
	/**
	 * Konstruktor der Klasse "Function" erstellt eine neue Funktion, die genutzt wird um alle wichtigen Werte
	 * einer Funktion zu speichern.
	 * 
	 * @param plFunctionObj	Ausdruck, welcher die Funktionsdefinition darstellt (alle Tokens ab einschliesslich dem Namen).
	 */
	public Function(LinkedList<Token> plFunctionObj) {
		lParametersObj = new LinkedList<Atom>();
		llExpressionsObj = new LinkedList<LinkedList<Token>>();
		
		int nBracketsOpened = 0;
		int nBracketsClosed = 0;
		
		//Den Namen der Funktion identifizieren:
		sName = plFunctionObj.poll().getValue();
		
		//Liste an Parametern identifizieren:
		while (!plFunctionObj.isEmpty()) {
			Token currentTokenObj = plFunctionObj.poll();
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			if (nBracketsClosed == nBracketsOpened) {
				//Es wurde gleich viele Klammern geoeffnet und geschlossen:
				nBracketsOpened = 0;
				nBracketsClosed = 0;
				break;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
				//Aktueller Token ist ein Bezeichner (Parameter):
				lParametersObj.add(new Atom(currentTokenObj.getValue(), "0.00", TokenTypes.TOKEN_NUMBER));
			}
		}
		
		//Liste an Ausdruecken identifizieren:
		plFunctionObj.poll(); //Erste geoeffnete Klammer entfernen.
		LinkedList<Token> lCurrentExpressionObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck.
		while (!plFunctionObj.isEmpty()) {
			Token currentTokenObj = plFunctionObj.poll();
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			lCurrentExpressionObj.add(currentTokenObj);
			if ((nBracketsClosed == nBracketsOpened) && (nBracketsClosed != 0 && nBracketsOpened != 0)) {
				//Ein neuer Ausdruck gefunden:
				llExpressionsObj.add(new LinkedList<Token>(lCurrentExpressionObj));
				lCurrentExpressionObj.clear();
				System.out.println();
			}
			if (nBracketsClosed == nBracketsOpened + 1) {
				//Alle Ausdr�cke wurden herausgearbeitet:
				break;
			}
		}
		/*
		//-------------------------- DEBUG --------------------------------------
		System.out.println("NEW FUNCTION DEFINED");
		System.out.println("name>" + sName);
		System.out.print("parameters>");
		for (int i = 0; i < lParametersObj.size(); i++) {
			System.out.print(lParametersObj.get(i).getName());
			if (i != lParametersObj.size() - 1) {
				System.out.print(",");
			}
		}
		System.out.println();
		for (int i = 0; i < llExpressionsObj.size(); i++) {
			System.out.print("expression" + i + ">");
			for (int j = 0; j < llExpressionsObj.get(i).size(); j++) {
				System.out.print(llExpressionsObj.get(i).get(j).getValue() + " ");
			}
			System.out.println();
		}
		//-------------------------- DEBUG --------------------------------------
		*/
	}
	
	/**
	 * Standartkonstruktor der Klasse "Function" erstellt eine leere Funktion, ohne Quellcode und Parameter.
	 */
	public Function() {
		lParametersObj = new LinkedList<Atom>();
		llExpressionsObj = new LinkedList<LinkedList<Token>>();
	}
	
	
	
	/**
	 * Diese Methode gibt den Bezeichner der Funktion zurueck.
	 * 
	 * @return	Name der Funktion.
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * Diese Methode gibt die Liste an Parametern zurueck.
	 * 
	 * @return	Liste der Parameter
	 */
	public LinkedList<Atom> getParameters() {
		return lParametersObj;
	}
	
	/**
	 * Diese Methode gibt die Anzahl an Parametern zurueck.
	 * 
	 * @return	Anzahl der Parameter.
	 */
	public int getParameterAmount() {
		return lParametersObj.size();
	}
	
	/**
	 * Diese Methode gibt den Ausdruck mit dem Index nIndex als Token-Liste zurueck.
	 * 
	 * @param pnIndex	Index des Ausdrucks.
	 * 
	 * @return			Ausdruck als Token-Liste.
	 */
	public LinkedList<Token> getExpression(int pnIndex) {
		return llExpressionsObj.get(pnIndex);
	}
	
	/**
	 * Diese Methode gibt die Anzahl der Ausdruecke innerhalb der Funktion zurueck.
	 * 
	 * @return	Anzahl der Ausdruecke.
	 */
	public int getExpressionAmount() {
		return llExpressionsObj.size();
	}
	
	
	
	/**
	 * 
	 */
	public ReturnValue<Object> importParameterValues(LinkedList<String> plsValues) {
		if (plsValues.size() == lParametersObj.size()) {
			//Es wurde die korrekte Anzahl an Parametern angegeben:
			
			for (int i = 0; i < plsValues.size(); i++) {
				if (isNumber(plsValues.get(i))) {
					//Beim Wert handelt es sich um eine Zahl:
					overrideAtom(new Atom(lParametersObj.get(i).getName(), plsValues.get(i), TokenTypes.TOKEN_NUMBER));
				}
				else if (plsValues.get(i).equals(KeywordTypes.BOOLEAN_F) || plsValues.get(i).equals(KeywordTypes.BOOLEAN_T)) {
					//Beim Wert handelt es sich um einen Wahrheitswert:
					overrideAtom(new Atom(lParametersObj.get(i).getName(), plsValues.get(i), TokenTypes.TOKEN_BOOLEAN));
				}
				else {
					//Beim Wert handelt es sich um einen String:
					overrideAtom(new Atom(lParametersObj.get(i).getName(), plsValues.get(i), TokenTypes.TOKEN_STRING));
				}
			}
			return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
			
		}
		else {
			return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
		}
	}
	
	
	
	/**
	 * Diese Methode fuegt ein Atom der Liste an Atomen hinzu. Wenn der Bezeichner
	 * eines Atoms bereits vorkommt, so wird es nicht hinzugefuegt.
	 * 
	 * @param pAtomObj	Atom, welches hinzugefuegt werden soll.
	 * 
	 * @return			Gibt an, ob das Atom erfolgreich hinzugefuegt wurde.
	 */
	public boolean addAtom(Atom pAtomObj) {
		//Herausfinden, ob das Atom bereits existiert:
		for (int i = 0; i < lParametersObj.size(); i++) {
			if (lParametersObj.get(i).getName().equals(pAtomObj.getName())) {
				//Das Atom existiert:
				return false;
			}
		}
		//Das Atom existiert nicht:
		lParametersObj.add(pAtomObj);
		return true;
	}
	
	
	
	/**
	 * Diese Methode ersetzt ein Atom in der Liste an Atomen.
	 * 
	 * @param pAtomObj	Atom, durch welches ersetzt werden soll.
	 * 
	 * @return			Gibt an, ob das Atom erfolgreich ersetzt wurde.
	 */
	public boolean overrideAtom(Atom pAtomObj) {
		//Herausfinden, ob das Atom bereits existiert:
		for (int i = 0; i < lParametersObj.size(); i++) {
			if (lParametersObj.get(i).getName().equals(pAtomObj.getName())) {
				//Atom existiert an der Stelle i:
				lParametersObj.set(i, pAtomObj);
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Diese Methode sucht das als Parameter angegebene Atom und gibt dessen
	 * Wert zurueck.
	 * 
	 * @param psName	Bezeichner des Atoms.
	 * @return			Atom.
	 */
	public ReturnValue<Atom> searchAtom(String psName) {
		//Herausfinden, ob das Atom existiert:
		for (int i = 0; i < lParametersObj.size(); i++) {
			if (lParametersObj.get(i).getName().equals(psName)) {
				//Atom gefunden:
				return new ReturnValue<Atom>(lParametersObj.get(i), ReturnValueTypes.SUCCESS);
			}
		}
		//Atom wurde nicht gefunden:
		return new ReturnValue<Atom>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
	}
	
	
	
	/**
	 * Diese Methode ueberpruft, ob es sich bei dem als Parameter angegebenen String um eine Zahl handelt.
	 * 
	 * @param psNumber	String, welcher ueberprueft werden soll.
	 * @return			Gibt an, ob es sich bei dem String um eine Zahl handelt.
	 */
	private boolean isNumber(String psNumber) {
		try {
			double nNumber = Double.parseDouble(psNumber);
		}
		catch (NumberFormatException exceptionObj) {
			return false;
		}
		return true;
	}
}
