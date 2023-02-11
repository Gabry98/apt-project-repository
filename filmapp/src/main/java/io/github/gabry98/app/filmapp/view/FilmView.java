package io.github.gabry98.app.filmapp.view;

import java.util.List;

import io.github.gabry98.app.filmapp.model.Film;

public interface FilmView {

	void filmAdded(int id, String name, int date);

	void filmDeletedById(int id, int position);

	void filmsDeletedByName(List<Film> deletedFilms);

	void filmsDeletedByDate(List<Film> deletedFilms);

	void filmUpdatedById(int oldId, int newId, int position);

	void filmUpdatedByName(int id, String name, int position);

	void filmUpdatedByDate(int id, int date, int position);

	void filmFound(int id, String name, int date);

}
