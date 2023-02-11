package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.net.InetSocketAddress;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.mongo.ActorMongoRepository;
import io.github.gabry98.app.filmapp.repository.mongo.FilmMongoRepository;
import io.github.gabry98.app.filmapp.repository.mongo.ReportMongoRepository;
import io.github.gabry98.app.filmapp.view.gui.ReportGUIView;

@RunWith(GUITestRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Integration Tests for ReportGUIView.")
class ReportGUIViewIT extends AssertJSwingJUnitTestCase {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	private ReportGUIView reportView;
	private ReportMongoRepository reportRepository;
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "reportCollection";
	
	@Mock
	private ActorMongoRepository actorRepository;
	
	@Mock
	private FilmMongoRepository filmRepository;

	private StreamingController streamingController;
	
	private AutoCloseable closeable;
	
	private static final long TIMEOUT = 60000;
	
	@BeforeAll
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@BeforeEach
	public void setup() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		onSetUp();
	}
	
	@AfterEach
	public void tearDownClient() throws Exception {
		closeable.close();
		onTearDown();
	}
	
	@AfterAll
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		reportRepository = new ReportMongoRepository(mongoClient,DATABASE_NAME,COLLECTION_NAME);
		
		for(Report report : reportRepository.findAll()) {
			reportRepository.deleteReportById(report.getId());
		}
		
		GuiActionRunner.execute(() -> {
			reportView = new ReportGUIView();
			streamingController = new StreamingController(actorRepository, filmRepository, reportRepository);
			reportView.setStreamingController(streamingController);
			return reportView;
		});
		
		window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(), reportView);
		window.show(new Dimension(450,300));
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	@DisplayName("Test when we find all reports in the ReportRepository.")
	void testFindAllReports() {
		reportRepository.addReport(1,1,1);
		reportRepository.addReport(2,2,2);
		streamingController.searchAllReports(reportView);
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).contains("1,1,1","2,2,2");
	}
	
	@Test
	@DisplayName("Test when we find a report by id in the ReportRepository.")
	void testFindReportById() {
		reportRepository.addReport(1,1,1);
		reportRepository.addReport(2,2,2);
		streamingController.searchReportById(reportView,1);
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).containsExactly("1,1,1");
	}
	
	@Test
	@DisplayName("Test when we find a report by actorId in the ReportRepository.")
	void testFindReportByActorId() {
		reportRepository.addReport(1,1,1);
		reportRepository.addReport(2,2,2);
		streamingController.searchReportsByActorId(reportView,2);
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).containsExactly("2,2,2");
	}
	
	@Test
	@DisplayName("Test when we find a report by filmId in the ReportRepository.")
	void testFindReportByFilmId() {
		reportRepository.addReport(1,1,1);
		reportRepository.addReport(2,2,1);
		streamingController.searchReportsByFilmId(reportView,1);
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("findList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).containsExactly("1,1,1","2,2,1");
	}
	
	@Test
	@DisplayName("Test when we add a report with success.")
	void testAddReportSuccess() {
		when(actorRepository.findById(1)).thenReturn(new Actor(1,"Marco Rossi"));
		when(filmRepository.findById(1)).thenReturn(new Film(1,"King Kong",1950));
		window.textBox("reportId").enterText("1");
		window.textBox("actorId").enterText("1");
		window.textBox("filmId").enterText("1");
		window.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("findList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("reportsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("reportsList").contents()).containsExactly("1,1,1");
	}
	
	@Test
	@DisplayName("Test when we add a report with an error.")
	void testAddReportError() {
		when(actorRepository.findById(1)).thenReturn(new Actor(1,"Marco Rossi"));
		when(filmRepository.findById(1)).thenReturn(new Film(1,"King Kong",1950));
		GuiActionRunner.execute(() -> reportRepository.addReport(1, 1, 1));
		window.textBox("reportId").enterText("1");
		window.textBox("actorId").enterText("1");
		window.textBox("filmId").enterText("1");
		window.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("reportsList").contents()).isEmpty();
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Report with id 1 already exists!");
	}
	
	@ParameterizedTest
	@CsvSource({
        "1",
        "2"
    })
	@DisplayName("Test when we update a report with success and with an error.")
	void testUpdateReportWithSuccessAndWithError(String value) {
		when(actorRepository.findById(2)).thenReturn(new Actor(2,"Marco Rossi"));
		when(filmRepository.findById(2)).thenReturn(new Film(2,"King Kong",1950));
		window.textBox("reportId").enterText("1");
		window.textBox("actorId").enterText("2");
		window.textBox("filmId").enterText("2");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("reportId").enterText(value);
		window.list("reportsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		
		if(value.equals("1")) {
			pause(
					new Condition("errorLabel must be not empty") {
						@Override
						public boolean test() {
							return !window.label("errorLabel").text().trim().isEmpty();
						}
					}
				,timeout(TIMEOUT));
			assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Report with id 1 already exists!");
		}
		
		if(value.equals("2")) {
			pause(
					new Condition("reportsList must be not empty") {
						@Override
						public boolean test() {
							return !(window.list("reportsList").contents().length == 0);
						}
					}
				,timeout(TIMEOUT));
			assertThat(window.list("reportsList").contents()).containsExactly("2,2,2");
		}
	}
	
	@ParameterizedTest
	@CsvSource({
        "UPDATE",
        "REMOVE"
    })
	@DisplayName("Test when we remove a report by id with success and with an error.")
	void testRemoveReportByIdAndWithError(String button) {
		when(actorRepository.findById(2)).thenReturn(new Actor(2,"Marco Rossi"));
		when(filmRepository.findById(2)).thenReturn(new Film(2,"King Kong",1950));
		window.textBox("reportId").enterText("1");
		window.textBox("actorId").enterText("2");
		window.textBox("filmId").enterText("2");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("reportId").enterText("1");
		window.list("reportsList").selectItems(0);
		window.button(JButtonMatcher.withText(button)).click();
		
		if(button.equals("REMOVE")) {
			assertThat(window.list("reportsList").contents()).isEmpty();
		}
		
		if(button.equals("UPDATE")) {
			pause(
					new Condition("errorLabel must be not empty") {
						@Override
						public boolean test() {
							return !window.label("errorLabel").text().trim().isEmpty();
						}
					}
				,timeout(TIMEOUT));
			assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Report with id 1 already exists!");
		}
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmId",
        "actorId"
    })
	@DisplayName("Test when we remove a report by actorId and filmId with success.")
	void testRemoveReportByActorAndFilmId(String field) {
		window.textBox("reportId").enterText("1");
		window.textBox("actorId").enterText("2");
		window.textBox("filmId").enterText("2");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox(field).enterText("2");
		window.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(window.list("reportsList").contents()).isEmpty();
	}

}
