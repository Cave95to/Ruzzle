package it.polito.tdp.ruzzle.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ruzzle.db.DizionarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model {
	private final int SIZE = 4;
	private Board board ;
	private List<String> dizionario ;
	private StringProperty statusText ;

	public Model() {
		this.statusText = new SimpleStringProperty() ;
		
		this.board = new Board(SIZE);
		DizionarioDAO dao = new DizionarioDAO() ;
		this.dizionario = dao.listParola() ;
		statusText.set(String.format("%d parole lette", this.dizionario.size())) ;
	
	}
	
	public void reset() {
		this.board.reset() ;
		this.statusText.set("Board Reset");
	}

	public Board getBoard() {
		return this.board;
	}

	public final StringProperty statusTextProperty() {
		return this.statusText;
	}
	

	public final String getStatusText() {
		return this.statusTextProperty().get();
	}
	

	public final void setStatusText(final String statusText) {
		this.statusTextProperty().set(statusText);
	}

	public List<Pos> trovaParola(String parola) {
		
		// cerchiamo se dentro matrice c'è lettera iniziale di parola, per ogni corrispondenza proviamo la ricerca
		for(Pos p : board.getPositions()) {
			
			// dalla board prendiamo la stringproperty in posizione p, con il get diventa string, estraiamo il carattere
			if(board.getCellValueProperty(p).get().charAt(0) == parola.charAt(0)) {
				// se c'è lettera iniziale ha senso cercare
				
				List<Pos> percorso = new ArrayList<>();
				// aggiungiamo la casella da cui partiamo
				percorso.add(p);
				
				//iniziamo ricorsione
				if(cerca(parola, 1, percorso))
					return percorso;
				
			}
		}
		
		return null;
	}

	private boolean cerca(String parola, int livello, List<Pos> percorso) {
		
		// caso terminale
		if(livello == parola.length())
			return true;
		
		// salviamo posizione ultima lettera aggiunta
		Pos ultima = percorso.get(percorso.size()-1);
		// estraiamo tutti i vicini
		List<Pos> adiacenti = board.getAdjacencies(ultima);
		
		// per tutti i vicini
		for(Pos p : adiacenti) {
			if(!percorso.contains(p) && // se non abbiamo gia usato la casella in questione e se lettera è quella cercata
				parola.charAt(livello)==board.getCellValueProperty(p).get().charAt(0)) { // sempre 0 perche 1 sola lettera
				
				percorso.add(p);
				
				// per uscire subito da ricorsione, se troviamo la parola via, non serve fare backtracking e andare avanti
				if(cerca(parola, livello+1, percorso))
					return true;
				
				percorso.remove(percorso.size()-1);
			}
			
		}
		return false;
	}

	public List<String> trovaTutte() {
		
		// lista di tutte le parole che troveremo nella matrice
		List<String> tutte = new ArrayList<>();
		
		for (String parola : this.dizionario) {
			if(parola.length()>1) { // regola ruzzle ameno 2 lettere
				if(this.trovaParola(parola.toUpperCase())!= null) { // dentro ruzzle sono tutte maiuscole
					tutte.add(parola);
				}
			}
		}
		return tutte;
	}
	

}
