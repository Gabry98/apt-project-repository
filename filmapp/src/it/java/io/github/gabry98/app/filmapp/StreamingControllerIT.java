package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.mongo.ActorMongoRepository;
import io.github.gabry98.app.filmapp.repository.mongo.FilmMongoRepository;
import io.github.gabry98.app.filmapp.repository.mongo.ReportMongoRepository;
import io.github.gabry98.app.filmapp.view.ActorView;
import io.github.gabry98.app.filmapp.view.FilmView;
import io.github.gabry98.app.filmapp.view.ReportView;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Integration Tests for Streaming Controller.")
@Testcontainers
class StreamingControllerIT {

	@Container
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	private static final String DATABASE_NAME = "project-db";
	private static final String ACTORS_COLLECTION_NAME = "actorCollection";
	private static final String FILMS_COLLECTION_NAME = "filmCollection";
	private static final String REPORTS_COLLECTION_NAME = "reportCollection";
	
	private MongoClient client;
	private ActorMongoRepository actorRepository;
	private FilmMongoRepository filmRepository;
	private ReportMongoRepository reportRepository;
	
	@Mock
	private ActorView actorView;
	
	@Mock
	private FilmView filmView;
	
	@Mock
	private ReportView reportView;
	
	@InjectMocks
	private StreamingController streamingController;
	
	private AutoCloseable closeable;
	
	@BeforeEach
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getMappedPort(27017))
		);
		
		actorRepository = new ActorMongoRepository(client,DATABASE_NAME,ACTORS_COLLECTION_NAME);
		filmRepository = new FilmMongoRepository(client,DATABASE_NAME,FILMS_COLLECTION_NAME);
		reportRepository = new ReportMongoRepository(client,DATABASE_NAME,REPORTS_COLLECTION_NAME);
		streamingController = new StreamingController(actorRepository, filmRepository, reportRepository);
		
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		closeable.close();
		client.close();
	}

	@Test
	@DisplayName("Test when we add an Actor to the Actor Repository.")
	void testAddActor() {
		streamingController.addActor(actorView, 1, "Marco Rossi");
		streamingController.addActor(actorView, 2, "Andrea Verdi");
		List<Actor> actors = actorRepository.findAll();
		assertThat(actors.get(0).getId()).isEqualTo(1);
		assertThat(actors.get(1).getId()).isEqualTo(2);
		assertThat(actors.get(0).getName()).isEqualTo("Marco Rossi");
		assertThat(actors.get(1).getName()).isEqualTo("Andrea Verdi");
	}
	
	@Test
	@DisplayName("Test when we remove an Actor by id from the Actor Repository.")
	void testRemoveActorById() {
		actorRepository.addActor(1, "Marco Rossi");
		streamingController.deleteActor(actorView, 1, 0);
		assertThat(actorRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove all Actors with specific name from the Actor Repository.")
	void testRemoveActorsByName() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Marco Rossi");
		streamingController.deleteActor(actorView, "Marco Rossi");
		assertThat(actorRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we update an Actor by id into the Actor Repository.")
	void testUpdateActorById() {
		actorRepository.addActor(1, "Marco Rossi");
		streamingController.updateActor(actorView, 1, 2, 0);
		List<Actor> actors = actorRepository.findAll();
		assertThat(actors.get(0).getId()).isEqualTo(2);
		assertThat(actors.get(0).getName()).isEqualTo("Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we update an Actor by name into the Actor Repository.")
	void testUpdateActorByName() {
		actorRepository.addActor(1, "Marco Rossi");
		streamingController.updateActor(actorView, 1, "Andrea Verdi", 0);
		List<Actor> actors = actorRepository.findAll();
		assertThat(actors.get(0).getId()).isEqualTo(1);
		assertThat(actors.get(0).getName()).isEqualTo("Andrea Verdi");
	}
	
	@Test
	@DisplayName("Test when we find an Actor by id from the Actor Repository.")
	void testFindActorById() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Andrea Verdi");
		streamingController.searchActorById(actorView, 1);
		verify(actorView).actorFound(1,"Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we find all Actor with specific name from the Actor Repository.")
	void testFindActorsByName() {
		actorRepository.addActor(1, "Marco Rossi");
		actorRepository.addActor(2, "Marco Rossi");
		streamingController.searchActorsByName(actorView, "Marco Rossi");
		verify(actorView).actorFound(1,"Marco Rossi");
		verify(actorView).actorFound(2,"Marco Rossi");
	}
	
	@Test
	@DisplayName("Test when we add a Film to the Film Repository.")
	void testAddFilm() {
		streamingController.addFilm(filmView, 1, "King Kong", 1950);
		streamingController.addFilm(filmView, 2, "King Kong", 2005);
		List<Film> films = filmRepository.findAll();
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(0).getName()).isEqualTo("King Kong");
		assertThat(films.get(0).getDate()).isEqualTo(1950);
		assertThat(films.get(1).getId()).isEqualTo(2);
		assertThat(films.get(1).getName()).isEqualTo("King Kong");
		assertThat(films.get(1).getDate()).isEqualTo(2005);
	}
	
	@Test
	@DisplayName("Test when we remove a Film by id from the Film Repository.")
	void testRemoveFilmById() {
		filmRepository.addFilm(1, "King Kong", 1950);
		streamingController.deleteFilmById(filmView, 1, 0);
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Film by name from the Film Repository.")
	void testRemoveFilmByName() {
		filmRepository.addFilm(1, "King Kong", 1950);
		streamingController.addFilm(filmView, 2, "King Kong", 2005);
		streamingController.deleteFilmsByName(filmView, "King Kong");
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Film by date from the Film Repository.")
	void testRemoveFilmByDate() {
		filmRepository.addFilm(1, "King Kong", 1950);
		streamingController.deleteFilmsByDate(filmView, 1950);
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we update a Film by id into the Film Repository.")
	void testUpdateFilmById() {
		filmRepository.addFilm(1, "King Kong", 1950);
		streamingController.updateFilmById(filmView, 1, 2, 0);
		List<Film> films = filmRepository.findAll();
		assertThat(films.get(0).getId()).isEqualTo(2);
		assertThat(films.get(0).getName()).isEqualTo("King Kong");
		assertThat(films.get(0).getDate()).isEqualTo(1950);
	}
	
	@Test
	@DisplayName("Test when we update a Film by name into the Film Repository.")
	void testUpdateFilmByName() {
		filmRepository.addFilm(1, "King Kong", 1950);
		streamingController.updateFilmByName(filmView, 1, "King", 0);
		List<Film> films = filmRepository.findAll();
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(0).getName()).isEqualTo("King");
		assertThat(films.get(0).getDate()).isEqualTo(1950);
	}
	
	@Test
	@DisplayName("Test when we update a Film by date into the Film Repository.")
	void testUpdateFilmByDate() {
		filmRepository.addFilm(1, "King Kong", 1950);
		streamingController.updateFilmDate(filmView, 1, 2005, 0);
		List<Film> films = filmRepository.findAll();
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(0).getName()).isEqualTo("King Kong");
		assertThat(films.get(0).getDate()).isEqualTo(2005);
	}
	
	@Test
	@DisplayName("Test when we find a Film by id into the Film Repository.")
	void testFindFilmById() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "King Kong", 2005);
		streamingController.searchFilmById(filmView, 1);
		verify(filmView).filmFound(1, "King Kong", 1950);
	}
	
	@Test
	@DisplayName("Test when we find a Film by date into the Film Repository.")
	void testFindFilmByDate() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "King Kong", 2005);
		streamingController.searchFilmsByDate(filmView, 1950);
		verify(filmView).filmFound(1, "King Kong", 1950);
	}
	
	@Test
	@DisplayName("Test when we find a Film by name into the Film Repository.")
	void testFindFilmByName() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "King Kong", 2005);
		streamingController.searchFilmsByName(filmView, "King Kong");
		verify(filmView).filmFound(1, "King Kong", 1950);
		verify(filmView).filmFound(2, "King Kong", 2005);
	}
	
	@Test
	@DisplayName("Test when we add a Report to the Report Repository.")
	void testAddReport() {
		reportRepository.addReport(1, 1, 1);
		List<Report> reports = reportRepository.findAll();
		assertThat(reports).hasSize(1);
		assertThat(reports.get(0).getId()).isEqualTo(1);
		assertThat(reports.get(0).getActorId()).isEqualTo(1);
		assertThat(reports.get(0).getFilmId()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("Test when we remove a Report by id from the Report Repository.")
	void testRemoveReportById() {
		reportRepository.addReport(1, 2, 3);
		streamingController.deleteReportById(reportView, 1, 0);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Report by actorId from the Report Repository.")
	void testRemoveReportByActorId() {
		reportRepository.addReport(1, 2, 3);
		streamingController.deleteReportByActorId(reportView, 2);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Report by filmId from the Report Repository.")
	void testRemoveReportByFilmId() {
		reportRepository.addReport(1, 2, 3);
		streamingController.deleteReportByFilmId(reportView, 3);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we update a Report by id from the Report Repository.")
	void testUpdateReportById() {
		reportRepository.addReport(1, 2, 3);
		streamingController.updateReportId(reportView, 1, 2, 0);
		List<Report> reports = reportRepository.findAll();
		assertThat(reports.get(0).getId()).isEqualTo(2);
		assertThat(reports.get(0).getActorId()).isEqualTo(2);
		assertThat(reports.get(0).getFilmId()).isEqualTo(3);
	}
	
	@Test
	@DisplayName("Test when we find a Report by id from the Report Repository.")
	void testFindReportById() {
		reportRepository.addReport(1, 2, 3);
		streamingController.searchReportById(reportView, 1);
		verify(reportView).reportFound(1, 2, 3);
	}
	
	@Test
	@DisplayName("Test when we find a Report by actorId from the Report Repository.")
	void testFindReportByActorId() {
		reportRepository.addReport(1, 2, 3);
		streamingController.searchReportsByActorId(reportView, 2);
		verify(reportView).reportFound(1, 2, 3);
	}
	
	@Test
	@DisplayName("Test when we find a Report by filmId from the Report Repository.")
	void testFindReportByFilmId() {
		reportRepository.addReport(1, 2, 3);
		streamingController.searchReportsByFilmId(reportView, 3);
		verify(reportView).reportFound(1, 2, 3);
	}

}
