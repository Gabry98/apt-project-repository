package io.github.gabry98.app.filmapp.view;

import java.util.List;

import io.github.gabry98.app.filmapp.model.Actor;

public interface ActorView {

	void actorAdded(int id, String name);

	void actorDeletedById(int position);

	void actorsDeletedByName(List<Actor> deletedActors);

	void actorUpdated(int oldId, int newId, int position);

	void actorUpdated(int oldId, String name, int position);

	void actorFound(int id, String name);

}
