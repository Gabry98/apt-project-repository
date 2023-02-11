package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.InetSocketAddress;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.ReportRepository;
import io.github.gabry98.app.filmapp.repository.mongo.ReportMongoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for Reports Repository")
class ReportMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient client;
	private ReportRepository reportRepository;
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "reportCollection";
	
	@BeforeAll
	public void setUpBeforeAll() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterAll
	public void tearDownAfterAll() {
		server.shutdown();
	}
	
	@BeforeEach
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		reportRepository = new ReportMongoRepository(client,DATABASE_NAME,COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
	}
	
	@AfterEach
	public void tearDown() {
		client.close();
	}
	
	@Test
	@DisplayName("Test Find All Method when the DB is Empty.")
	void testFindAllWithEmptyDatabase() {
		assertThat(reportRepository.findAll()).isEmpty();
	}

	@DisplayName("Test Find All Method when the DB is not Empty.")
	@ParameterizedTest
	@CsvSource({
        "1,1,1",
        "2,2,2"
    })
	void testFindAllWithSomereports(int id, int actorId, int filmId) {
		reportRepository.addReport(id, actorId, filmId);
		List<Report> reports = reportRepository.findAll();
		assertThat(reports).isNotNull();
		assertThat(reports.get(0).getActorId()).isEqualTo(actorId);
		assertThat(reports.get(0).getFilmId()).isEqualTo(filmId);
	}
	
	@Test
	@DisplayName("Test adding an report with a duplicate ID.")
	void testAddReportWithDuplicateId() {
		reportRepository.addReport(1, 1, 1);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(1, 2, 2));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Report with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test adding an report with a duplicate pair of actors and films.")
	void testAddReportWithDuplicatePair() {
		reportRepository.addReport(1, 1, 1);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(2, 1, 1));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Pair with ActorId 1 and FilmId 1 already exists!");
	}
	
	@Test
	@DisplayName("Test adding an report with a different film of existing one.")
	void testAddReportWithDifferentFilm() {
		reportRepository.addReport(1, 1, 1);
		assertDoesNotThrow(() -> reportRepository.addReport(2, 1, 2));
		assertDoesNotThrow(() -> reportRepository.addReport(3, 2, 1));
	}
	
	@Test
	@DisplayName("Test adding a Report with a non positive ID.")
	void testAddReportWithNonPositiveId() {
		IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(-1, 1, 1));
		IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(0, 1, 1));
		assertThat(thrown1.getMessage()).isEqualTo(thrown2.getMessage()).isEqualTo("ERROR: id should be positive!");
	}
	
	@Test
	@DisplayName("Test adding an report with a non positive ID for the Actor.")
	void testAddReportWithNonPositiveActorId() {
		IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(1, -1, 1));
		IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(1, 0, 1));
		assertThat(thrown1.getMessage()).isEqualTo(thrown2.getMessage()).isEqualTo("ERROR: id should be positive!");
	}
	
	@Test
	@DisplayName("Test adding an report with a non positive ID for the Film.")
	void testAddReportWithNonPositiveFilmId() {
		IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(1, 1, -1));
		IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> reportRepository.addReport(1, 1, 0));
		assertThat(thrown1.getMessage()).isEqualTo(thrown2.getMessage()).isEqualTo("ERROR: id should be positive!");
	}
	
	@Test
	@DisplayName("Test the search of Report by correct ID.")
	void testFindByReportIdMethodWithTwoHomonymous() {
		reportRepository.addReport(1, 1, 1);
		Report report = reportRepository.findById(1);
		assertThat(report).isNotNull();
		assertThat(report.getId()).isEqualTo(1);
		assertThat(report.getActorId()).isEqualTo(1);
		assertThat(report.getFilmId()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("Test the search of Report by wrong ID.")
	void testFindByReportIdMethodWithSomeActorAndWrongName() {
		reportRepository.addReport(1, 1, 1);
		Report report = reportRepository.findById(2);
		assertThat(report).isNull();
	}
	
	@Test
	@DisplayName("Test the search of Report by correct Actor ID.")
	void testFindByActorIdMethodWithTwoHomonymous() {
		reportRepository.addReport(1, 1, 1);
		reportRepository.addReport(2, 1, 2);
		List<Report> reports = reportRepository.findByActorId(1);
		assertThat(reports).isNotNull();
		assertThat(reports.get(0).getId()).isEqualTo(1);
		assertThat(reports.get(0).getActorId()).isEqualTo(reports.get(1).getActorId()).isEqualTo(1);
		assertThat(reports.get(0).getFilmId()).isEqualTo(1);
		assertThat(reports.get(1).getId()).isEqualTo(2);
		assertThat(reports.get(1).getFilmId()).isEqualTo(2);
	}
	
	@Test
	@DisplayName("Test the search of Report by wrong Actor ID.")
	void testFindByActorIdMethodWithSomeActorAndWrongName() {
		reportRepository.addReport(1, 1, 1);
		List<Report> reports = reportRepository.findByActorId(2);
		assertThat(reports).isEmpty();
	}
	
	@Test
	@DisplayName("Test the search of Report by correct Film ID.")
	void testFindByFilmIdMethodWithTwoHomonymous() {
		reportRepository.addReport(1, 1, 1);
		reportRepository.addReport(2, 2, 1);
		List<Report> reports = reportRepository.findByFilmId(1);
		assertThat(reports).isNotNull();
		assertThat(reports.get(0).getId()).isEqualTo(1);
		assertThat(reports.get(0).getFilmId()).isEqualTo(reports.get(1).getFilmId()).isEqualTo(1);
		assertThat(reports.get(0).getActorId()).isEqualTo(1);
		assertThat(reports.get(1).getId()).isEqualTo(2);
		assertThat(reports.get(1).getActorId()).isEqualTo(2);
	}
	
	@Test
	@DisplayName("Test the search of Report by wrong Actor ID.")
	void testFindByFilmIdMethodWithSomeActorAndWrongName() {
		reportRepository.addReport(1, 1, 1);
		List<Report> reports = reportRepository.findByFilmId(2);
		assertThat(reports).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct update of the ID of a Report.")
	void testUpdateReportIdCorrectly() {
		reportRepository.addReport(1, 1, 1);
		reportRepository.updateReportId(1,2);
		Report r = reportRepository.findById(2);
		assertThat(r.getActorId()).isEqualTo(1);
		assertThat(r.getFilmId()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("Test the update of the ID of a Report with a value already present.")
	void testUpdateActorIdWrongly() {
		reportRepository.addReport(1, 1, 1);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> reportRepository.updateReportId(1,1));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Report with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test the correct deletion of a Report by ID.")
	void testDeleteReportByIdCorrectly() {
		reportRepository.addReport(1, 1, 1);
		reportRepository.deleteReportById(1);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of a Report by Actor ID.")
	void testDeleteReportByActorIdCorrectly() {
		reportRepository.addReport(1, 1, 1);
		reportRepository.deleteReportByActorId(1);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of a Report by Film ID.")
	void testDeleteReportByFilmIdCorrectly() {
		reportRepository.addReport(1, 1, 1);
		reportRepository.deleteReportByFilmId(1);
		assertThat(reportRepository.findAll()).isEmpty();
	}

}
