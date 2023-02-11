package io.github.gabry98.app.filmapp.model;

public class Report {
	
	private int id;
	private int actorId;
	private int filmId;
	
	public Report(int id, int actorId, int filmId) {
		this.id = id;
		this.actorId = actorId;
		this.filmId = filmId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getActorId() {
		return actorId;
	}
	public void setActorId(int actorId) {
		this.actorId = actorId;
	}
	public int getFilmId() {
		return filmId;
	}
	public void setFilmId(int filmId) {
		this.filmId = filmId;
	}

}
