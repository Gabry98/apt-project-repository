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
@DisplayName("E2E Tests for ReportGUIView.")
class ReportGUIViewE2E extends AssertJSwingJUnitTestCase {

private static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private static final String DB_NAME = "project-db";
	
	private static final String ACTORS_COLLECTION_NAME = "actorCollection";
	private static final String FILMS_COLLECTION_NAME = "filmCollection";
	private static final String REPORTS_COLLECTION_NAME = "reportCollection";
	
	private MongoClient mongoClient;
	
	private FrameFixture reportView;
	
	private static final long TIMEOUT = 5000;
	
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
		addActorToTheDatabase(1,"Marco Rossi");
		addActorToTheDatabase(2,"Andrea Verdi");
		addFilmToTheDatabase(1,"King Kong",1950);
		addFilmToTheDatabase(2,"King Kong",2005);
		addReportToTheDatabase(1,1,1);
		application("io.github.gabry98.app.filmapp.StreamingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-report-collection="+ REPORTS_COLLECTION_NAME
			).start();
		
		reportView = WindowFinder.findFrame("Report View")
				.using(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock());
	}
	
	@AfterEach
	@Override
	protected void onTearDown() {
		mongo.stop();
		reportView.cleanUp();
		mongoClient.close();
	}
	
	private void addActorToTheDatabase(int id, String name) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(ACTORS_COLLECTION_NAME)
			.insertOne(
					new Document()
						.append("id",id)
						.append("name",name));
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
	
	private void addReportToTheDatabase(int reportId, int actorId, int filmId) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(REPORTS_COLLECTION_NAME)
			.insertOne(
					new Document()
						.append("id",reportId)
						.append("actorId",actorId)
						.append("filmId",filmId));
	}
	
	@Test
	@DisplayName("Test when we update a Report correctly.")
	void testUpdateReportCorrectly() {
		testShowInitialReportsCorrectly();
		addReport();
		reportView.textBox("reportId").enterText("3");
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
		assertThat(reportView.list("reportsList").contents()).containsExactly("3,2,2");
		
	}
	
	@Test
	@DisplayName("Test when we update a Report with a duplicated id.")
	void testUpdateReportWithDuplicateId() {
		testShowInitialReportsCorrectly();
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
		assertThat(reportView.list("reportsList").contents()).containsExactly("2,2,2");
		assertThat(reportView.label("errorLabel").text()).isEqualTo("ERROR: Report with id 1 already exists!");
		
	}
	
	@Test
	@DisplayName("Test when we delete a Report by reportId correctly.")
	void testDeleteReportByIdCorrectly() {
		testShowInitialReportsCorrectly();
		addReport();
		reportView.list("reportsList").selectItems(0);
		reportView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(reportView.list("reportsList").contents()).isEmpty();
		
	}
	
	@ParameterizedTest
	@CsvSource({
        "actorId,2",
        "filmId,2"
    })
	@DisplayName("Test when we delete a Report by actorId and filmId correctly.")
	void testDeleteReportByActorIdAndFilmIdCorrectly(String field, String value) {
		addReport();
		reportView.textBox(field).enterText(value);
		reportView.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(reportView.list("reportsList").contents()).isEmpty();
		
	}
	
	@ParameterizedTest
	@CsvSource({
        "reportId,2",
        "actorId,2",
        "filmId,2"
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
		assertThat(reportView.list("findList").contents()).containsExactly("2,2,2");
	}
	
	private void addReport() {
		reportView.textBox("reportId").enterText("2");
		reportView.textBox("actorId").enterText("2");
		reportView.textBox("filmId").enterText("2");
		reportView.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("reportsList must be not empty") {
					@Override
					public boolean test() {
						return !(reportView.list("reportsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(reportView.list("reportsList").contents()).containsExactly("2,2,2");
	}
	
	private void testShowInitialReportsCorrectly() {
		reportView.moveToFront();
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

}
