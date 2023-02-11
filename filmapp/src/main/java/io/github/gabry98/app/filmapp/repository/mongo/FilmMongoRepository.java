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
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.repository.FilmRepository;

public class FilmMongoRepository implements FilmRepository {
	private MongoCollection<Document> filmCollection;
	
	private static final Logger LOGGER = LogManager.getLogger(FilmMongoRepository.class);
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";
	private static final String DATE_KEY = "date";
	
	public FilmMongoRepository(MongoClient client, String databaseName, String collectionName) {
		filmCollection = client
				.getDatabase(databaseName)
				.getCollection(collectionName);
		LOGGER.info("Successfully initialized Film Repository");
	}

	@Override
	public List<Film> findAll() {
		LOGGER.info("Sending the list of the films in repository...");
		return StreamSupport.stream(filmCollection.find().spliterator(), false)
				.map(d -> new Film(d.getInteger(ID_KEY), d.getString(NAME_KEY), d.getInteger(DATE_KEY)))
				.toList();
	}

	@Override
	public void addFilm(int id, String name, int date) {
		LOGGER.info("Checking the correctness for data to add...");
		checkFilmId(id);
		checkFilmName(name);
		checkFilmDate(date);
		LOGGER.info("Inserting a new Film inside the repository...");
		filmCollection.insertOne(
				new Document()
				.append(ID_KEY, id)
				.append(NAME_KEY, name)
				.append(DATE_KEY, date));
	}

	private void checkFilmId(int id) {
		if(id <= 0) {
			throw new IllegalArgumentException("ERROR: id should be positive!");
		}
		Document d = filmCollection.find(Filters.eq(ID_KEY, id)).first();
		if(d != null) {
			throw new IllegalArgumentException("ERROR: Film with id "+id+" already exists!");
		}
	}
	
	private void checkFilmName(String name) {
		if(name.equals("")) {
			throw new IllegalArgumentException("ERROR: the Name of the Film must not be an empty string!");
		}
	}
	
	private void checkFilmDate(int date) {
		if(date<1930 || date>2022) {
			throw new IllegalArgumentException("ERROR: the Date must be between 1930 and 2022!");
		}
	}

	@Override
	public Film findById(int id) {
		LOGGER.info("Searching a Film by ID...");
		Document d = filmCollection.find(Filters.eq(ID_KEY, id)).first();
		return d != null ? fromDocumentToFilm(d) : null;
	}

	private Film fromDocumentToFilm(Document d) {
		LOGGER.info("Converting a Document to a Film...");
		return new Film(d.getInteger(ID_KEY), d.getString(NAME_KEY), d.getInteger(DATE_KEY));
	}

	@Override
	public List<Film> findByName(String name) {
		LOGGER.info("Searching a Film by Name...");
		return StreamSupport.stream(filmCollection.find(Filters.eq(NAME_KEY, name)).spliterator(), false)
				.map(d -> new Film(d.getInteger(ID_KEY), d.getString(NAME_KEY), d.getInteger(DATE_KEY)))
				.toList();
	}
	
	@Override
	public List<Film> findByDate(int date) {
		LOGGER.info("Searching a Film by Date...");
		return StreamSupport.stream(filmCollection.find(Filters.eq(DATE_KEY, date)).spliterator(), false)
				.map(d -> new Film(d.getInteger(ID_KEY), d.getString(NAME_KEY), d.getInteger(DATE_KEY)))
				.toList();
	}

	@Override
	public void deleteFilmById(int id) {
		LOGGER.info("Deleting Film with ID {}...", id);
		filmCollection.deleteOne(Filters.eq(ID_KEY, id));
		LOGGER.info("Successfully deleted Film with ID {}.", id);
	}
	
	@Override
	public void deleteFilmsByName(String name) {
		LOGGER.info("Deleting Films with Name {}...", name);
		filmCollection.deleteMany(Filters.eq(NAME_KEY, name));
		LOGGER.info("Successfully deleted Films with Name {}.", name);
	}
	
	@Override
	public void deleteFilmsByDate(int date) {
		LOGGER.info("Deleting Films with Date {}...", date);
		filmCollection.deleteMany(Filters.eq(DATE_KEY, date));
		LOGGER.info("Successfully deleted Films with Date {}.", date);
	}

	@Override
	public void updateFilmId(int oldId, int newId) {
		LOGGER.info("Checking if ID already exists...");
		checkFilmId(newId);
		LOGGER.info("Updating Film with ID {}...", oldId);
		filmCollection.updateOne(Filters.eq(ID_KEY, oldId), Updates.set(ID_KEY, newId));
		LOGGER.info("Successfully changed ID {} with ID {}", oldId, newId);
	}

	@Override
	public void updateFilmName(int id, String name) {
		LOGGER.info("Checking if Name is correct...");
		checkFilmName(name);
		LOGGER.info("Checking if the new Name is not equal to the existing one...");
		Document d = filmCollection.find(Filters.eq(ID_KEY, id)).first();
		if (d.get(NAME_KEY).equals(name)) {
			throw new IllegalArgumentException("ERROR: you choose the same current name of the Film!");
		}
		LOGGER.info("Updating the Name of the Film with ID {} with {}", id, name);
		filmCollection.updateOne(Filters.eq(ID_KEY, id), Updates.set(NAME_KEY, name));
		LOGGER.info("Successfully Updated the Name of the Film with ID {} with {}", id, name);
	}
	
	@Override
	public void updateFilmDate(int id, int date) {
		LOGGER.info("Checking if Date is correct...");
		checkFilmDate(date);
		LOGGER.info("Checking if the new Date is not equal to the existing one...");
		Document d = filmCollection.find(Filters.eq(ID_KEY, id)).first();
		if (d.get(DATE_KEY).equals(date)) {
			throw new IllegalArgumentException("ERROR: you choose the same current date of the Film!");
		}
		LOGGER.info("Updating the Date of the Film with ID {} with {}", id, date);
		filmCollection.updateOne(Filters.eq(ID_KEY, id), Updates.set(DATE_KEY, date));
		LOGGER.info("Successfully Updated the Date of the Film with ID {} with {}", id, date);
	}
}
