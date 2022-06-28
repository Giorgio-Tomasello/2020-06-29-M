package it.polito.tdp.imdb.model;

import java.util.Comparator;

public class Comparatore_registi implements Comparator<Director> {

	
	public int compare(Director a, Director b) {
		
		if(a.getId()>b.getId())
			return 1;
		else return -1;

	}
}
