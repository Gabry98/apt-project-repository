package io.github.gabry98.app.filmapp.repository;

import java.util.List;

import io.github.gabry98.app.filmapp.model.Actor;

public interface ActorRepository {
	
	public List<Actor> findAll();
	
	public void addActor(int id, String name);

	public Actor findById(int id);

	public List<Actor> findByName(String name);

	public void deleteActorById(int id);

	public void updateActorId(int oldId, int newId);

	public void updateActorName(int id, String name);
	
	void deleteActorsByName(String name);
	
}
