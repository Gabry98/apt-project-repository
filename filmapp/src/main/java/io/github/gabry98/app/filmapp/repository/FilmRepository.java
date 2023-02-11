package io.github.gabry98.app.filmapp.repository;

import java.util.List;

import io.github.gabry98.app.filmapp.model.Film;

public interface FilmRepository {
	
	public List<Film> findAll();
	
	public void addFilm(int id, String name, int date);

	public Film findById(int id);

	public List<Film> findByName(String name);
	
	public void updateFilmId(int oldId, int newId);

	public void updateFilmName(int id, String name);
	
	public void updateFilmDate(int id, int date);
	
	List<Film> findByDate(int date);
	
	void deleteFilmsByName(String name);
	
	void deleteFilmsByDate(int date);
	
	public void deleteFilmById(int id);
}
