package io.github.gabry98.app.filmapp.repository.mongo;

import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.repository.ActorRepository;

public class ActorMongoRepository implements ActorRepository {

	private MongoCollection<Document> actorCollection;
	
	private static final Logger LOGGER = LogManager.getLogger(ActorMongoRepository.class);
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";
	
	public ActorMongoRepository(MongoClient client, String databaseName, String collectionName) {
		actorCollection = client
				.getDatabase(databaseName)
				.getCollection(collectionName);
		LOGGER.info("Successfully initialized Actor Repository");
	}

	@Override
	public List<Actor> findAll() {
		LOGGER.info("Sending the list of the actors in repository...");
		return StreamSupport.stream(actorCollection.find().spliterator(), false)
				.map(d -> new Actor(d.getInteger(ID_KEY), d.getString(NAME_KEY)))
				.toList();
	}

	@Override
	public void addActor(int id, String name) {
		LOGGER.info("Checking the correctness for data to add...");
		checkActorId(id);
		checkActorName(name);
		LOGGER.info("Inserting a new Actor inside the repository...");
		actorCollection.insertOne(
				new Document()
				.append(ID_KEY, id)
				.append(NAME_KEY, name));
	}

	private void checkActorId(int id) {
		if(id <= 0) {
			throw new IllegalArgumentException("ERROR: id should be positive!");
		}
		Document d = actorCollection.find(Filters.eq(ID_KEY, id)).first();
		if(d != null) {
			throw new IllegalArgumentException("ERROR: Actor with id "+id+" already exists!");
		}
	}
	
	private void checkActorName(String name) {
		if(name.equals("")) {
			throw new IllegalArgumentException("ERROR: the Name of the actor must not be an empty string!");
		}
	}

	@Override
	public Actor findById(int id) {
		LOGGER.info("Searching an Actor by ID...");
		Document d = actorCollection.find(Filters.eq(ID_KEY, id)).first();
		return d != null ? fromDocumentToActor(d) : null;
	}

	private Actor fromDocumentToActor(Document d) {
		LOGGER.info("Converting a Document to an Actor...");
		return new Actor(d.getInteger(ID_KEY), d.getString(NAME_KEY));
	}

	@Override
	public List<Actor> findByName(String name) {
		LOGGER.info("Searching an Actor by Name...");
		return StreamSupport.stream(actorCollection.find(Filters.eq(NAME_KEY, name)).spliterator(), false)
				.map(d -> new Actor(d.getInteger(ID_KEY), d.getString(NAME_KEY)))
				.toList();
	}

	@Override
	public void deleteActorById(int id) {
		LOGGER.info("Deleting Actor with ID {}...",id);
		actorCollection.deleteOne(Filters.eq(ID_KEY, id));
		LOGGER.info("Successfully deleted Actor with ID {}.",id);
	}
	
	@Override
	public void deleteActorsByName(String name) {
		LOGGER.info("Deleting Actors with Name {}...",name);
		actorCollection.deleteMany(Filters.eq(NAME_KEY, name));
		LOGGER.info("Successfully deleted Actors with Name {}.", name);
	}

	@Override
	public void updateActorId(int oldId, int newId) {
		LOGGER.info("Checking if ID already exists...");
		checkActorId(newId);
		LOGGER.info("Updating Actor with ID {}...",oldId);
		actorCollection.updateOne(Filters.eq(ID_KEY, oldId), Updates.set(ID_KEY, newId));
		LOGGER.info("Successfully changed ID {} with ID {}", oldId, newId);
	}

	@Override
	public void updateActorName(int id, String name) {
		LOGGER.info("Checking if Name is correct...");
		checkActorName(name);
		LOGGER.info("Checking if the new Name is not equal to the existing one...");
		Document d = actorCollection.find(Filters.eq(ID_KEY, id)).first();
		if (d.get(NAME_KEY).equals(name)) {
			throw new IllegalArgumentException("ERROR: you choose the same current name of the Actor!");
		}
		LOGGER.info("Updating the Name of the Actor with ID {} with {}", id, name);
		actorCollection.updateOne(Filters.eq(ID_KEY, id), Updates.set(NAME_KEY, name));
		LOGGER.info("Successfully Updated the Name of the Actor with ID {} with {}", id, name);
	}

}
