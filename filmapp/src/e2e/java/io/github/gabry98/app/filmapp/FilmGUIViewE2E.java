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
import org.bson.Document;
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
@DisplayName("E2E Tests for FilmGUIView.")
class FilmGUIViewE2E extends AssertJSwingJUnitTestCase {

	private static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private static final String DB_NAME = "project-db";
	
	private static final String FILMS_COLLECTION_NAME = "filmCollection";
	
	private MongoClient mongoClient;
	
	private FrameFixture filmView;
	
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
		addFilmToTheDatabase(1,"King Kong",1950);
		application("io.github.gabry98.app.filmapp.StreamingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-film-collection=" + FILMS_COLLECTION_NAME
			).start();

		filmView = WindowFinder.findFrame("Film View")
				.using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock());
	}
	
	private void addFilmToTheDatabase(int id, String name, int date) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(FILMS_COLLECTION_NAME)
			.insertOne(
					new Document()
						.append("id",id)
						.append("name",name)
						.append("date",date));
	}

	@AfterEach
	@Override
	protected void onTearDown() {
		mongo.stop();
		filmView.cleanUp();
		mongoClient.close();
	}
	
	@Test
	@DisplayName("Test when we add a Film with duplicated id.")
	void testAddFilmWithDuplicatedId() {
		testShowInitialFilmsCorrectly();
		filmView.textBox("filmId").enterText("1");
		filmView.textBox("filmName").enterText("King Kong");
		filmView.textBox("filmDate").enterText("2005");
		filmView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !filmView.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.label("errorLabel").text()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmId,3,'3,King Kong,2005'",
        "filmName,King,'2,King,2005'",
        "filmDate,2000,'2,King Kong,2000'"
    })
	@DisplayName("Test when we update a Film by id, name and date correctly.")
	void testUpdateFilmByIdAndNameAndDateCorrectly(String field, String value, String list) {
		testShowInitialFilmsCorrectly();
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
		testShowInitialFilmsCorrectly();
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
		assertThat(filmView.list("filmsList").contents()).containsExactly("2,King Kong,2005");
		assertThat(filmView.label("errorLabel").text()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we remove a Film by id correctly.")
	void testRemoveFilmByIdCorrectly() {
		testShowInitialFilmsCorrectly();
		addFilm();
		filmView.list("filmsList").selectItems(0);
		filmView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(filmView.list("filmsList").contents()).isEmpty();
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmName,King Kong",
        "filmDate,2005"
    })
	@DisplayName("Test when we remove a Film by name and date correctly.")
	void testRemoveFilmByNameAndDateCorrectly(String field, String value) {
		testShowInitialFilmsCorrectly();
		addFilm();
		filmView.textBox(field).enterText(value);
		filmView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(filmView.list("filmsList").contents()).isEmpty();
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmId,2",
        "filmName,King Kong",
        "filmDate,2005"
    })
	@DisplayName("Test when we find a Film by id, name and date correctly.")
	void testFindFilmByIdAndNameAndDateCorrectly(String field, String value) {
		testShowInitialFilmsCorrectly();
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
		
		if(field.equals("filmName")) {
			assertThat(filmView.list("findList").contents()).containsExactly("1,King Kong,1950","2,King Kong,2005");
		} else {
			assertThat(filmView.list("findList").contents()).containsExactly("2,King Kong,2005");
		}
	}
	
	private void addFilm() {
		filmView.textBox("filmId").enterText("2");
		filmView.textBox("filmName").enterText("King Kong");
		filmView.textBox("filmDate").enterText("2005");
		filmView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(filmView.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(filmView.list("filmsList").contents()).containsExactly("2,King Kong,2005");
	}
	
	private void testShowInitialFilmsCorrectly() {
		filmView.moveToFront();
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

}
