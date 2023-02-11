package io.github.gabry98.app.filmapp.repository.mongo;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.ReportRepository;

public class ReportMongoRepository implements ReportRepository {
	private MongoCollection<Document> reportCollection;
	
	private static final Logger LOGGER = LogManager.getLogger(ReportMongoRepository.class);
	private static final String REPORT_ID_KEY = "id";
	private static final String ACTOR_ID_KEY = "actorId";
	private static final String FILM_ID_KEY = "filmId";
	private static final String ID_EXCEPTION_ERROR = "ERROR: id should be positive!";
	
	public ReportMongoRepository(MongoClient client, String databaseName, String collectionName) {
		reportCollection = client
				.getDatabase(databaseName)
				.getCollection(collectionName);
		LOGGER.info("Successfully initialized Report Repository");
	}
	
	@Override
	public List<Report> findAll() {
		LOGGER.info("Sending the list of the reports in repository...");
		return StreamSupport.stream(reportCollection.find().spliterator(), false)
				.map(d -> new Report(d.getInteger(REPORT_ID_KEY), d.getInteger(ACTOR_ID_KEY), d.getInteger(FILM_ID_KEY)))
				.toList();
	}
	
	@Override
	public void addReport(int id, int actorId, int filmId) {
		LOGGER.info("Checking the correctness for data to add...");
		checkReportId(id);
		checkActorId(actorId);
		checkFilmId(filmId);
		checkPair(actorId, filmId);
		LOGGER.info("Inserting a new Report inside the repository...");
		reportCollection.insertOne(
				new Document()
				.append(REPORT_ID_KEY, id)
				.append(ACTOR_ID_KEY, actorId)
				.append(FILM_ID_KEY, filmId));
	}
	
	private void checkPair(int actorId, int filmId) {
		List<Report> reports = findAll();
			
		Optional<Report> alreadyExists = reports.stream().filter(r -> r.getActorId() == actorId && r.getFilmId() == filmId).findFirst();
			
		if(!alreadyExists.isEmpty()) {
			throw new IllegalArgumentException("ERROR: Pair with ActorId "+actorId+" and FilmId "+filmId+" already exists!");
		}
	}

	private void checkReportId(int id) {
		if(id <= 0) {
			throw new IllegalArgumentException(ID_EXCEPTION_ERROR);
		}
		Document d = reportCollection.find(Filters.eq(REPORT_ID_KEY, id)).first();
		if(d != null) {
			throw new IllegalArgumentException("ERROR: Report with id "+id+" already exists!");
		}
	}
	
	private void checkActorId(int id) {
		if(id <= 0) {
			throw new IllegalArgumentException(ID_EXCEPTION_ERROR);
		}
	}
	
	private void checkFilmId(int id) {
		if(id <= 0) {
			throw new IllegalArgumentException(ID_EXCEPTION_ERROR);
		}
	}
	
	@Override
	public Report findById(int id) {
		LOGGER.info("Searching a Report by ID...");
		Document d = reportCollection.find(Filters.eq(REPORT_ID_KEY, id)).first();
		return d != null ? fromDocumentToReport(d) : null;
	}

	private Report fromDocumentToReport(Document d) {
		LOGGER.info("Converting a Document to a Report...");
		return new Report(d.getInteger(REPORT_ID_KEY), d.getInteger(ACTOR_ID_KEY), d.getInteger(FILM_ID_KEY));
	}
	
	@Override
	public List<Report> findByActorId(int id) {
		LOGGER.info("Searching a Report by Actor ID...");
		return StreamSupport.stream(reportCollection.find(Filters.eq(ACTOR_ID_KEY, id)).spliterator(), false)
				.map(d -> new Report(d.getInteger(REPORT_ID_KEY), d.getInteger(ACTOR_ID_KEY), d.getInteger(FILM_ID_KEY)))
				.toList();
	}
	
	@Override
	public List<Report> findByFilmId(int id) {
		LOGGER.info("Searching a Report by Film ID...");
		return StreamSupport.stream(reportCollection.find(Filters.eq(FILM_ID_KEY, id)).spliterator(), false)
				.map(d -> new Report(d.getInteger(REPORT_ID_KEY), d.getInteger(ACTOR_ID_KEY), d.getInteger(FILM_ID_KEY)))
				.toList();
	}
	
	@Override
	public void updateReportId(int oldId, int newId) {
		LOGGER.info("Checking if ID already exists...");
		checkReportId(newId);
		LOGGER.info("Updating Report with ID {}...", oldId);
		reportCollection.updateOne(Filters.eq(REPORT_ID_KEY, oldId), Updates.set(REPORT_ID_KEY, newId));
		LOGGER.info("Successfully changed ID {} with ID {}", oldId, newId);
	}
	
	@Override
	public void deleteReportById(int id) {
		LOGGER.info("Deleting Report with ID {}...", id);
		reportCollection.deleteOne(Filters.eq(REPORT_ID_KEY, id));
		LOGGER.info("Successfully deleted Report with ID {}.", id);
	}
	
	@Override
	public void deleteReportByActorId(int id) {
		LOGGER.info("Deleting Reports with Actor ID {}...", id);
		reportCollection.deleteMany(Filters.eq(ACTOR_ID_KEY, id));
		LOGGER.info("Successfully deleted Reports with Actor ID {}.", id);
	}
	
	@Override
	public void deleteReportByFilmId(int id) {
		LOGGER.info("Deleting Reports with Film ID {}...", id);
		reportCollection.deleteMany(Filters.eq(FILM_ID_KEY, id));
		LOGGER.info("Successfully deleted Reports with Film ID {}.", id);
	}

}
