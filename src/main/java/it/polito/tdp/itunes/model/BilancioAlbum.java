package it.polito.tdp.itunes.model;

import java.util.Objects;

public class BilancioAlbum implements Comparable<BilancioAlbum>{
	
	Album album;
	int bilancio;
	
	public Album getAlbum() {
		return album;
	}
	public void setAlbum(Album album) {
		this.album = album;
	}
	public int getBilancio() {
		return bilancio;
	}
	public void setBilancio(int bilancio) {
		this.bilancio = bilancio;
	}
	@Override
	public int hashCode() {
		return Objects.hash(album);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BilancioAlbum other = (BilancioAlbum) obj;
		return Objects.equals(album, other.album);
	}
	public BilancioAlbum(Album album, int bilancio) {
		super();
		this.album = album;
		this.bilancio = bilancio;
	}
	@Override
	public int compareTo(BilancioAlbum o) {
		// TODO Auto-generated method stub
		return o.bilancio-this.bilancio;
	}
	@Override
	public String toString() {
		return  album + ", bilancio:" + bilancio;
	}
	
	
	

}
