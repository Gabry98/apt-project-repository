package io.github.gabry98.app.filmapp;

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
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.repository.mongo.FilmMongoRepository;
import io.github.gabry98.app.filmapp.view.gui.FilmGUIView;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

@RunWith(GUITestRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Integration Tests for FilmGUIView.")
class FilmGUIViewIT extends AssertJSwingJUnitTestCase {
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	private FilmGUIView filmView;
	private FilmMongoRepository filmRepository;
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "reportCollection";

	private StreamingController streamingController;
	
	private static final long TIMEOUT = 60000;
	
	@BeforeAll
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@BeforeEach
	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		filmRepository = new FilmMongoRepository(mongoClient,DATABASE_NAME,COLLECTION_NAME);
		
		for(Film film : filmRepository.findAll()) {
			filmRepository.deleteFilmById(film.getId());
		}
		
		GuiActionRunner.execute(() -> {
			filmView = new FilmGUIView();
			streamingController = new StreamingController(null, filmRepository, null);
			filmView.setStreamingController(streamingController);
			return filmView;
		});
		
		window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock(), filmView);
		window.show(new Dimension(450,300));
	}
	
	@AfterEach
	public void tearDownClient() throws Exception {
		onTearDown();
	}
	
	@AfterAll
	public static void shutdownServer() {
		server.shutdown();
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	@DisplayName("Test when we find all films in the FilmRepository.")
	void testFindAllFilms() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "Bad Boys", 2002);
		streamingController.searchAllFilms(filmView);
		pause(
			new Condition("findList must be not empty") {
				@Override
				public boolean test() {
					return !(window.list("findList").contents().length == 0);
				}
			}
		,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).contains("1,King Kong,1950","2,Bad Boys,2002");
	}
	
	@Test
	@DisplayName("Test when we find a film by id in the FilmRepository.")
	void testFindFilmById() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "Bad Boys", 2002);
		streamingController.searchFilmById(filmView, 1);
		pause(
			new Condition("findList must be not empty") {
				@Override
				public boolean test() {
					return !(window.list("findList").contents().length == 0);
				}
			}
		,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).contains("1,King Kong,1950");
	}
	
	@Test
	@DisplayName("Test when we find a film by name in the FilmRepository.")
	void testFindFilmByName() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "Bad Boys", 2002);
		streamingController.searchFilmsByName(filmView, "Bad Boys");
		pause(
			new Condition("findList must be not empty") {
				@Override
				public boolean test() {
					return !(window.list("findList").contents().length == 0);
				}
			}
		,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).contains("2,Bad Boys,2002");
	}
	
	@Test
	@DisplayName("Test when we find a film by date in the FilmRepository.")
	void testFindFilmByDate() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "Bad Boys", 2002);
		streamingController.searchFilmsByDate(filmView, 1950);
		pause(
			new Condition("findList must be not empty") {
				@Override
				public boolean test() {
					return !(window.list("findList").contents().length == 0);
				}
			}
		,timeout(TIMEOUT));
		assertThat(window.list("findList").contents()).contains("1,King Kong,1950");
	}

	@Test
	@DisplayName("Test when we add a film with success.")
	void testAddFilmWithSuccess() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("filmsList").contents()).containsExactly("1,King Kong,1950");
	}
	
	@Test
	@DisplayName("Test when we add a film with error.")
	void testAddFilmWithError() {
		filmRepository.addFilm(1, "King Kong", 1950);
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		pause(
				new Condition("errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("filmsList").contents()).isEmpty();
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test when we delete a film from the view by id with success.")
	void testDeleteFilmByIdSuccess() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(window.list("filmsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we delete a film from the view by name with success.")
	void testDeleteFilmByNameSuccess() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmName").enterText("King Kong");
		window.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(window.list("filmsList").contents()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we delete a film from the view by date with success.")
	void testDeleteFilmByDateSuccess() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("REMOVE")).click();
		assertThat(window.list("filmsList").contents()).isEmpty();
	}
	
	@ParameterizedTest
	@CsvSource({
        "filmId,2,'2,King Kong,1950'",
        "filmName,King,'1,King,1950'",
        "filmDate,2005,'1,King Kong,2005'"
    })
	@DisplayName("Test when we update a film from the view by fields with success.")
	void testUpdateFilmByFieldsSuccess(String field, String value, String list) {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox(field).enterText(value);
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("filmsList must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("filmsList").contents().length == 0);
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("filmsList").contents()).containsExactly(list);
	}
	
	@Test
	@DisplayName("Test when we update a film from the view by id with an error.")
	void testUpdateFilmByIdError() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		streamingController.addFilm(filmView, 2, "Bad Boys", 2002);
		window.textBox("filmId").enterText("2");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("filmsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("filmsList").contents().length == 0)
								&& !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("filmsList").contents()).containsExactly("1,King Kong,1950","2,Bad Boys,2002");
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: Film with id 2 already exists!");
	}

	@Test
	@DisplayName("Test when we update a film from the view by name with an error.")
	void testUpdateFilmByNameError() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmName").enterText("King Kong");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("filmsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("filmsList").contents().length == 0)
								&& !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("filmsList").contents()).containsExactly("1,King Kong,1950");
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: you choose the same current name of the Film!");
	}

	@Test
	@DisplayName("Test when we update a film from the view by date with an error.")
	void testUpdateFilmByDateError() {
		window.textBox("filmId").enterText("1");
		window.textBox("filmName").enterText("King Kong");
		window.textBox("filmDate").enterText("1950");
		window.button(JButtonMatcher.withText("ADD")).click();
		window.textBox("filmDate").enterText("1950");
		window.list("filmsList").selectItems(0);
		window.button(JButtonMatcher.withText("UPDATE")).click();
		pause(
				new Condition("filmsList and errorLabel must be not empty") {
					@Override
					public boolean test() {
						return !(window.list("filmsList").contents().length == 0)
								&& !window.label("errorLabel").text().trim().isEmpty();
					}
				}
			,timeout(TIMEOUT));
		assertThat(window.list("filmsList").contents()).containsExactly("1,King Kong,1950");
		assertThat(window.label("errorLabel").text()).isEqualTo("ERROR: you choose the same current date of the Film!");
	}

}
