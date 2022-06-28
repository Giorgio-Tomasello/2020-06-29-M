package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;



public class Model {
	
	private ImdbDAO dao;
	private Graph<Director, DefaultWeightedEdge> grafo;
	List<Director> listaRegisti; 
	Map<Integer, Director> mappaRegisti = new TreeMap<Integer, Director>();
	List<Coppie> listaCoppie; 
	private int maxPersone;
	
	ArrayList<Director> best;
	
	public Model() {
		
		dao = new ImdbDAO();
	}
	
	public String creaGrafo(int anno){
		
		//creazione grafo
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiunta vertici
		listaRegisti = new LinkedList<Director>(dao.listMoviesDirectors(anno, mappaRegisti));
		Graphs.addAllVertices(this.grafo, listaRegisti);
		
		//aggiunta archi
		listaCoppie = new LinkedList<Coppie>(dao.getPesoArchi(anno, mappaRegisti));
		for(Coppie c : listaCoppie)
			Graphs.addEdge(this.grafo, c.getD1(), c.getD2(), c.getPeso());
			
		
			return "GRAFO CREATO \n#VERTICI: "+ this.grafo.vertexSet().size() + 
					"\nARCHI: " + this.grafo.edgeSet().size();
		
	}

	public List<Director> getListaRegisti() {
		Collections.sort(listaRegisti, new Comparatore_registi() );
		return listaRegisti;
	}
	
	public String getVicini(Director d) {
		String output = "\n";
		if(Graphs.neighborListOf(this.grafo, d).size()==0) {output += "Il regista selezionato non presenta archi nel grafo" ;}
		else {
		List<Director> vicini = new LinkedList<Director>(Graphs.neighborListOf(this.grafo, d));
		
		
		for(Coppie c : listaCoppie)
			for(Director direttore : vicini)
				if(c.getD1().equals(d) && c.getD2().equals(direttore)  ||  c.getD1().equals(direttore) && c.getD2().equals(d))
					output += direttore.getId() + " " + direttore.getLastName() + " " 
							+ direttore.firstName + " " +
							"# attori in comune: " + c.getPeso()+"\n";
		
		//for(Coppie c : listaCoppie)
		//	if(c.getD1().equals(d) && c.getD2().equals(vicini.get(1))  ||  c.getD1().equals(vicini.get(1)) && c.getD2().equals(d))
		//		output += vicini.get(1).getId() + " " + vicini.get(1).getLastName() + " " + vicini.get(1).firstName + " " + c.getPeso();
			
		}
		
		return output;
		
	}
	
	
	
	public List<Director> getRicorsione(int max, Director d){
		
		
		this.maxPersone=0;
		this.best = new ArrayList<Director>();
		
		List<Director> parziale= new ArrayList<Director>();
		
		parziale.add(d);
		recursive(parziale,0, max, d );  
		

		return best;
	}

	private void recursive(List<Director> parziale, int sommaAtt, int max, Director d) {
	
			if(parziale.size() > best.size())
				{
				this.best=new ArrayList<Director>(parziale);
				this.maxPersone = sommaAtt;
				}
				
					for(Director p : Graphs.neighborListOf(this.grafo, d)) {
						
							if(!parziale.contains(p)) {
								DefaultWeightedEdge e = this.grafo.getEdge(d, p);
								int peso = (int) this.grafo.getEdgeWeight(e);

							if((peso + sommaAtt) <= max) {
								parziale.add(p);
								recursive(parziale,peso+sommaAtt ,max, d);
								parziale.remove(p);
							}
							
		
				}}
			
			
		
	}	

}
