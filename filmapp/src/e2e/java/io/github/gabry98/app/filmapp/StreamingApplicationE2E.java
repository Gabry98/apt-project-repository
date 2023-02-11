package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;

@RunWith(GUITestRunner.class)
@DisplayName("E2E Tests for Streaming Application.")
class StreamingApplicationE2E extends AssertJSwingJUnitTestCase {
	
	private static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private static final String DB_NAME = "project-db";
	
	private static final String ACTORS_COLLECTION_NAME = "actorCollection";
	private static final String FILMS_COLLECTION_NAME = "filmCollection";
	private static final String REPORTS_COLLECTION_NAME = "reportCollection";
	
	private MongoClient mongoClient;
	
	private FrameFixture actorView,filmView,reportView;
	
	private static final long TIMEOUT = 60000;
	
	@BeforeEach
	public void setup() throws Exception {
		onSetUp();
	}

	@Override
	protected void onSetUp() throws Exception {
		mongo.start();
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		
		mongoClient.getDatabase(DB_NAME).drop();
		
		application("io.github.gabry98.app.filmapp.StreamingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-actor-collection=" + ACTORS_COLLECTION_NAME,
				"--db-film-collection="+ FILMS_COLLECTION_NAME,
				"--db-report-collection="+ REPORTS_COLLECTION_NAME
			).start();

		actorView = WindowFinder.findFrame("Actor View").using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock());
		filmView = WindowFinder.findFrame("Film View").using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock());
		reportView = WindowFinder.findFrame("Report View").using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock());
	}
	
	@AfterEach
	@Override
	protected void onTearDown() {
		mongo.stop();
		actorView.cleanUp();
		reportView.cleanUp();
		filmView.cleanUp();
		mongoClient.close();
	}

	@Test
	@DisplayName("Test when we add an Actor correctly.")
	void testAddActorCorrectly() {
		addActor();
	}

	@Test
	@DisplayName("Test when we add an Actor with duplicate id.")
	void testAddActorWithDuplicateId() {
		addActor();
		actorView.textBox("actorId").enterText("1");
		actorView.textBox("actorName").enterText("Andrea Verdi");
		actorView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("actorsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0)
								&& !actorView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("1,Marco Rossi");
		assertThat(actorView.label("errorLabel").text()).isEqualTo("ERROR: Actor with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we delete an Actor by id correctly.")
	void testDeleteActorByIdCorrectly() {
		addActor();
		actorView.list("actorsList").selectItems(0);
		actorView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(actorView.list("actorsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we delete an Actor by name correctly.")
	void testDeleteActorByNameCorrectly() {
		addActor();
		actorView.textBox("actorName").enterText("Marco Rossi");
		actorView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(actorView.list("actorsList").contents()).isEmpty();
	}
	
	@ParameterizedTest
	@CsvSource({
        "actorId,2,'2,Marco Rossi'",
        "actorName,Andrea Verdi,'1,Andrea Verdi'"
    })
	@DisplayName("Test when we update an Actor by id and name correctly.")
	void testUpdateActorByIdAndNameCorrectly(String field, String value, String list) {
		addActor();
		actorView.textBox(field).enterText(value);
		actorView.list("actorsList").selectItems(0);
		actorView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly(list);
	}
	
	@Test
	@DisplayName("Test when we update an Actor with duplicated id.")
	void testUpdateActorWithDuplicatedId() {
		addActor();
		actorView.textBox("actorId").enterText("1");
		actorView.list("actorsList").selectItems(0);
		actorView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("actorsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0)
								&& !actorView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("1,Marco Rossi");
		assertThat(actorView.label("errorLabel").text()).isEqualTo("ERROR: Actor with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we find all Actors correctly.")
	void testFindAllActorsCorrectly() {
		addActor();
		actorView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("findList").contents()).containsExactly("1,Marco Rossi");
	}
	
	@ParameterizedTest
	@CsvSource({
        "actorId,1",
        "actorName,Marco Rossi"
    })
	@DisplayName("Test when we find an Actor by id and name correctly.")
	void testFindActorByIdAndNameCorrectly(String field, String value) {
		addActor();
		actorView.textBox(field).enterText(value);
		actorView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("findList").contents()).containsExactly("1,Marco Rossi");
	}

	@Test
	@DisplayName("Test when we add a Film correctly.")
	void testAddFilmCorrectly() {
		addFilm();
	}
	
	@Test
	@DisplayName("Test when we add a Film with duplicated id.")
	void testAddFilmWithDuplicatedId() {
		addFilm();
		filmView.textBox("filmId").enterText("1");
		filmView.textBox("filmName").enterText("King Kong");
		filmView.textBox("filmDate").enterText("2005");
		filmView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("filmsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0)
								&& !filmView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly("1,King Kong,1950");
		assertThat(filmView.label("errorLabel").text()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmId,2,'2,King Kong,1950'",
        "filmName,King,'1,King,1950'",
        "filmDate,2005,'1,King Kong,2005'"
    })
	@DisplayName("Test when we update a Film by id, name and date correctly.")
	void testUpdateFilmByIdAndNameAndDateCorrectly(String field, String value, String list) {
		addFilm();
		filmView.textBox(field).enterText(value);
		filmView.list("filmsList").selectItems(0);
		filmView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly(list);
	}
	
	@Test
	@DisplayName("Test when we update a Film by duplicated id.")
	void testUpdateFilmWithDuplicatedId() {
		addFilm();
		filmView.textBox("filmId").enterText("1");
		filmView.list("filmsList").selectItems(0);
		filmView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("filmsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0)
								&& !filmView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly("1,King Kong,1950");
		assertThat(filmView.label("errorLabel").text()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we remove a Film by id correctly.")
	void testRemoveFilmByIdCorrectly() {
		addFilm();
		filmView.list("filmsList").selectItems(0);
		filmView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(filmView.list("filmsList").contents()).isEmpty();
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmName,King Kong",
        "filmDate,1950"
    })
	@DisplayName("Test when we remove a Film by name and date correctly.")
	void testRemoveFilmByNameAndDateCorrectly(String field, String value) {
		addFilm();
		filmView.textBox(field).enterText(value);
		filmView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(filmView.list("filmsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we find all Films correctly.")
	void testFindAllFilmsCorrectly() {
		addFilm();
		filmView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("findList").contents()).containsExactly("1,King Kong,1950");
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmId,1",
        "filmName,King Kong",
        "filmDate,1950"
    })
	@DisplayName("Test when we find a Film by id, name and date correctly.")
	void testFindFilmByIdAndNameAndDateCorrectly(String field, String value) {
		addFilm();
		filmView.textBox(field).enterText(value);
		filmView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("findList").contents()).containsExactly("1,King Kong,1950");
	}

	@Test
	@DisplayName("Test when we add a Report correctly.")
	void testAddReportCorrectly() {
		addReport();
	}
	
	@Test
	@DisplayName("Test when we add a Report with missing actor.")
	void testAddReportWithMissingActor() {
		addFilm();
		filmView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly("1,King Kong,1950");
		
		reportView.textBox("reportId").enterText("1");
		reportView.textBox("actorId").enterText("1");
		reportView.textBox("filmId").enterText("1");
		reportView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !reportView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("reportsList").contents()).isEmpty();
		assertThat(reportView.label("errorLabel").text()).isEqualTo("ERROR: No existing Actor with id 1.");
		
	}
	
	@Test
	@DisplayName("Test when we add a Report with missing film.")
	void testAddReportWithMissingFilm() {
		addActor();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("1,Marco Rossi");
		
		reportView.textBox("reportId").enterText("1");
		reportView.textBox("actorId").enterText("1");
		reportView.textBox("filmId").enterText("1");
		reportView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !reportView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("reportsList").contents()).isEmpty();
		assertThat(reportView.label("errorLabel").text()).isEqualTo("ERROR: No existing Film with id 1.");
		
	}
	
	@Test
	@DisplayName("Test when we update a Report correctly.")
	void testUpdateReportCorrectly() {
		addReport();
		reportView.textBox("reportId").enterText("2");
		reportView.list("reportsList").selectItems(0);
		reportView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("reportsList must be not empty") {
					@Override
					public boolean test() {
						return !(reportView.list("reportsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("reportsList").contents()).containsExactly("2,1,1");
		
	}
	
	@Test
	@DisplayName("Test when we update a Report with a duplicated id.")
	void testUpdateReportWithDuplicateId() {
		addReport();
		reportView.textBox("reportId").enterText("1");
		reportView.list("reportsList").selectItems(0);
		reportView.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("reportsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(reportView.list("reportsList").contents().length == 0)
								&& !reportView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("reportsList").contents()).containsExactly("1,1,1");
		assertThat(reportView.label("errorLabel").text()).isEqualTo("ERROR: Report with id 1 already exists!");
		
	}
	
	@Test
	@DisplayName("Test when we delete a Report by reportId correctly.")
	void testDeleteReportByIdCorrectly() {
		addReport();
		reportView.list("reportsList").selectItems(0);
		reportView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(reportView.list("reportsList").contents()).isEmpty();
		
	}
	
	@ParameterizedTest
	@CsvSource({
        "actorId,1",
        "filmId,1"
    })
	@DisplayName("Test when we delete a Report by actorId and filmId correctly.")
	void testDeleteReportByActorIdAndFilmIdCorrectly(String field, String value) {
		addReport();
		reportView.textBox(field).enterText(value);
		reportView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(reportView.list("reportsList").contents()).isEmpty();
		
	}
	
	@Test
	@DisplayName("Test when we find all Reports correctly.")
	void testFindAllReportsCorrectly() {
		addReport();
		reportView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("reportsList must be not empty") {
					@Override
					public boolean test() {
						return !(reportView.list("reportsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("findList").contents()).containsExactly("1,1,1");
		
	}
	
	@ParameterizedTest
	@CsvSource({
        "reportId,1",
        "actorId,1",
        "filmId,1"
    })
	@DisplayName("Test when we find a Report by reportId, actorId and filmId correctly.")
	void testFindReportByIdAndActorIdAndFilmIdCorrectly(String field, String value) {
		addReport();
		reportView.textBox(field).enterText(value);
		reportView.button(JButtonMatcher.withText("FIND")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(reportView.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("findList").contents()).containsExactly("1,1,1");
	}

	private void addActor() {
		actorView.textBox("actorId").enterText("1");
		actorView.textBox("actorName").enterText("Marco Rossi");
		actorView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("1,Marco Rossi");
	}

	private void addFilm() {
		filmView.textBox("filmId").enterText("1");
		filmView.textBox("filmName").enterText("King Kong");
		filmView.textBox("filmDate").enterText("1950");
		filmView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly("1,King Kong,1950");
	}
	
	private void addReport() {
		actorView.textBox("actorId").enterText("1");
		actorView.textBox("actorName").enterText("Marco Rossi");
		actorView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("actorsList must be not empty") {
					@Override
					public boolean test() {
						return !(actorView.list("actorsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(actorView.list("actorsList").contents()).containsExactly("1,Marco Rossi");

		filmView.textBox("filmId").enterText("1");
		filmView.textBox("filmName").enterText("King Kong");
		filmView.textBox("filmDate").enterText("1950");
		filmView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly("1,King Kong,1950");

		reportView.textBox("reportId").enterText("1");
		reportView.textBox("actorId").enterText("1");
		reportView.textBox("filmId").enterText("1");
		reportView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("reportsList must be not empty") {
					@Override
					public boolean test() {
						return !(reportView.list("reportsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("reportsList").contents()).containsExactly("1,1,1");
	}

}
