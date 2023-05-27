package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private List<Album> allAlbums;
	private SimpleDirectedWeightedGraph<Album,DefaultWeightedEdge> graph;
	private ItunesDAO dao;
	
	//variabii globali per la ricorsione
	private List<Album> bestPath;
	private int bestScore;
	
	public Model() {
		this.allAlbums=new ArrayList<>();
		this.graph=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao=new ItunesDAO();
	}
	
	public List<BilancioAlbum> getAdiacenti(Album root){
		List<Album> successori=Graphs.successorListOf(this.graph, root);// non posso usare neighbor list of perchè mi drebbe sia gli entranti che gli uscenti
		List<BilancioAlbum> bilancioSuccessori=new ArrayList<>();
		
		for (Album a :successori) {
			bilancioSuccessori.add(new BilancioAlbum(a,getBilancio(a)));
		}
		Collections.sort(bilancioSuccessori);
		return bilancioSuccessori;	
		}
	
	private int getBilancio(Album a) {
		int bilancio=0;
		List<DefaultWeightedEdge> edgesIN=new ArrayList<>(this.graph.incomingEdgesOf(a));
		List<DefaultWeightedEdge> edgesOUT=new ArrayList<>(this.graph.outgoingEdgesOf(a));
		
		for (DefaultWeightedEdge edge:edgesIN) {
			bilancio+=this.graph.getEdgeWeight(edge);
		}
		for (DefaultWeightedEdge edge:edgesOUT) {
			bilancio-=this.graph.getEdgeWeight(edge);
		}
		return bilancio;
	}
	

	
	public void buildGraph(int n) {
		clearGraph(); //ogni volta devo creare un grafo nuovo!
		loadNodes(n);//carico i nodi!
		
		Graphs.addAllVertices(this.graph, this.allAlbums); //aggiungo vertici al grafo
		System.out.println(this.graph.vertexSet().size());
		
		for(Album a1:this.allAlbums) {
			for(Album a2:this.allAlbums) {
				int peso=a1.getNumSongs()-a2.getNumSongs();
				
				if (peso>0) {
					Graphs.addEdgeWithVertices(this.graph, a2, a1,peso);
					// nel caso in cui il peso sia minore non ci preoccupiamo, 
					//tanto saranno considerate le coppie inverse!
				}
			}
			
		}
		System.out.println(this.graph.edgeSet().size());
			
	}
	
	public void loadNodes(int n) {
		if(this.allAlbums.isEmpty()) {
			this.allAlbums= dao.getFilteredAlbums(n);//aggiungo tutti gli album!	
		}
	}
	
	public List<Album> getPath(Album source, Album target, int threshold){
		this.bestPath=new ArrayList<>();
		this.bestScore=0;
		List<Album> parziale=new ArrayList<>();
		parziale.add(source);
		
		ricorsione(parziale,target, threshold);
			
		return this.bestPath;
		
	}
	
	private void ricorsione(List<Album> parziale, Album target,int threshold) {
		Album current=parziale.get(parziale.size()-1);//prendo l'ultimo album presente
		if (current.equals(target)) {
			//se l'ultimo album inserito è la nostra destinazione-->termiine algoritmo
			//controllo se la soluzione è migliore della best salvata!
			if(getScore(parziale)>this.bestScore) {
				this.bestScore=getScore(parziale);
				this.bestPath=new ArrayList<>(parziale);
				
			}
			return;
		}
		//continuo ad aggiungere elementi in parziale
		List<Album> successors=Graphs.successorListOf(this.graph, current);
		for (Album a:successors) {
			if(this.graph.getEdgeWeight(this.graph.getEdge(current, a))>=threshold) {
				parziale.add(a);
				ricorsione(parziale,target,threshold);
				parziale.remove(a);
			}
		}
	}
	
	private int getScore(List<Album> lista) {
		Album source=lista.get(0);
		int score=0;
		              //prende tutti tranne il primo
		for (Album a: lista.subList(1, lista.size()-1)) {
			if( getBilancio(a)>getBilancio(source)) {
				score++;
			}	
		}
		return score;
		
	}
	
	public List<Album> getVertices(){
		List<Album> allVertices=new ArrayList<>(this.graph.vertexSet());
		Collections.sort(allVertices);
		return allVertices;
	}
	
	
	public int getNumVertici() {
		return this.graph.vertexSet().size();
	}
	
	public int getNumEdges() {
		return this.graph.edgeSet().size();
		
		
	}
	public void clearGraph() {
		this.allAlbums=new ArrayList<>();
		this.graph=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
	}
	
}
