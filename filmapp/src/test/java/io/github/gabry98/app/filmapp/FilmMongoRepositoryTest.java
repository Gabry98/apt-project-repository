package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.repository.FilmRepository;
import io.github.gabry98.app.filmapp.repository.mongo.FilmMongoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Tests for Films Repository")
class FilmMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient client;
	private FilmRepository filmRepository;
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "FILMCollection";
	
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
		filmRepository = new FilmMongoRepository(client, DATABASE_NAME, COLLECTION_NAME);
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
		assertThat(filmRepository.findAll()).isEmpty();
	}

	@DisplayName("Test Find All Method when the DB is not Empty.")
	@ParameterizedTest
	@CsvSource({
        "1,King Kong,1950",
        "2,La Dolce Vita,1960"
    })
	void testFindAllWithSomeFilms(int id, String name, int date) {
		filmRepository.addFilm(id, name, date);
		List<Film> Films = filmRepository.findAll();
		assertThat(Films).isNotNull();
		assertThat(Films.get(0).getId()).isEqualTo(id);
		assertThat(Films.get(0).getName()).isEqualTo(name);
		assertThat(Films.get(0).getDate()).isEqualTo(date);
	}
	
	@Test
	@DisplayName("Test adding a Film with a duplicate ID.")
	void testAddFilmWithDuplicateId() {
		filmRepository.addFilm(1, "Marco Rossi", 1930);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.addFilm(1, "King Kong", 1950));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test adding a Film with a non positive ID.")
	void testAddFilmWithNonPositiveId() {
		IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> filmRepository.addFilm(-1, "Drippaghen", 2019));
		IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> filmRepository.addFilm(0, "Drippaghen", 2019));
		assertThat(thrown1.getMessage()).isEqualTo(thrown2.getMessage()).isEqualTo("ERROR: id should be positive!");
	}
	
	@Test
	@DisplayName("Test adding a Film with an empty string for the Name.")
	void testAddFilmWithEmptyStringForName() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.addFilm(1, "", 1930));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: the Name of the Film must not be an empty string!");
	}
	
	@Test
	@DisplayName("Test adding a Film with a wrong value for the date.")
	void testAddFilmWithWrongDate() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.addFilm(1, "King Kong", 1920));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: the Date must be between 1930 and 2022!");
	}
	
	@Test
	@DisplayName("Test the search of Films by correct ID.")
	void testFindByIdMethodWithSomeFilm() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		Film f = filmRepository.findById(1);
		assertThat(f).isNotNull();
		assertThat(f.getId()).isEqualTo(1);
		assertThat(f.getName()).isEqualTo("Stanley Laurel and Oliver Hardy");
	}
	
	@Test
	@DisplayName("Test the search of Films by wrong ID.")
	void testFindByIdMethodWithSomeFilmAndWrongID() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		Film f = filmRepository.findById(2);
		assertThat(f).isNull();
	}
	
	@Test
	@DisplayName("Test the search of Films by correct Name and with two homonymous.")
	void testFindByNameMethodWithTwoHomonymous() {
		filmRepository.addFilm(1, "Bad Boys", 1983);
		filmRepository.addFilm(2, "Bad Boys",1995);
		List<Film> films = filmRepository.findByName("Bad Boys");
		assertThat(films).isNotEmpty();
		int filmsCount = films.size();
		assertThat(filmsCount).isEqualTo(2);
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(1).getId()).isEqualTo(2);
		assertThat(films.get(0).getName()).isEqualTo(films.get(1).getName()).isEqualTo("Bad Boys");
	}
	
	@Test
	@DisplayName("Test the search of Films correctly with two equal dates.")
	void testFindByNameMethodWithTwoEqualDates() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.addFilm(2, "Charlie Chaplin", 1930);
		List<Film> films = filmRepository.findByDate(1930);
		assertThat(films).isNotEmpty();
		int filmsCount = films.size();
		assertThat(filmsCount).isEqualTo(2);
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(1).getId()).isEqualTo(2);
		assertThat(films.get(0).getDate()).isEqualTo(films.get(1).getDate()).isEqualTo(1930);
	}
	
	@Test
	@DisplayName("Test the search of Films by wrong Name.")
	void testFindByIdMethodWithSomeFilmAndWrongName() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		List<Film> f = filmRepository.findByName("Fantozzi");
		assertThat(f).isEmpty();
	}
	
	@Test
	@DisplayName("Test the search of Films by wrong Date.")
	void testFindByIdMethodWithSomeFilmAndWrongDate() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		List<Film> f = filmRepository.findByDate(1940);
		assertThat(f).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct update of the ID of an Film.")
	void testUpdateFilmIdCorrectly() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.updateFilmId(1,2);
		Film f = filmRepository.findById(2);
		assertThat(f.getName()).isEqualTo("Stanley Laurel and Oliver Hardy");
		assertThat(f.getDate()).isEqualTo(1930);
	}
	
	@Test
	@DisplayName("Test the update of the ID of a Film with a value already present.")
	void testUpdateFilmIdWrongly() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.updateFilmId(1,1));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: Film with id 1 already exists!");
	}
	
	@Test
	@DisplayName("Test the correct update of the Name of a Film.")
	void testUpdateFilmNameCorrectly() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.updateFilmName(1,"Alberto Sordi e Mauro Zambuto");
		Film f = filmRepository.findById(1);
		assertThat(f.getName()).isEqualTo("Alberto Sordi e Mauro Zambuto");
		assertThat(f.getDate()).isEqualTo(1930);
	}
	
	@Test
	@DisplayName("Test the update of the Name of a Film with an empty string.")
	void testUpdateFilmNameWithEmptyString() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.updateFilmName(1,""));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: the Name of the Film must not be an empty string!");
	}
	
	@Test
	@DisplayName("Test the update of the Name of a Film with a string which already exists.")
	void testUpdateFilmNameWithAlreadyExistingString() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.updateFilmName(1,"Stanley Laurel and Oliver Hardy"));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: you choose the same current name of the Film!");
	}
	
	@Test
	@DisplayName("Test the correct update of the Date of a Film.")
	void testUpdateFilmDateCorrectly() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.updateFilmDate(1,1935);
		Film f = filmRepository.findById(1);
		assertThat(f.getName()).isEqualTo("Stanley Laurel and Oliver Hardy");
		assertThat(f.getDate()).isEqualTo(1935);
	}
	
	@DisplayName("Test the update of the Name of a Film with an empty string.")
	@ParameterizedTest
	@CsvSource({
        "1920",
        "2023"
    })
	void testUpdateFilmDateWithWrongValue(int date) {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.updateFilmDate(1,date));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: the Date must be between 1930 and 2022!");
	}
	
	@Test
	@DisplayName("Test the update of the Name of a Film with a string which already exists.")
	void testUpdateFilmDateWithAlreadyExistingValue() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> filmRepository.updateFilmDate(1,1930));
		assertThat(thrown.getMessage()).isEqualTo("ERROR: you choose the same current date of the Film!");
	}
	
	@Test
	@DisplayName("Test the correct deletion of a Film by ID.")
	void testDeleteFilmByIdCorrectly() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.deleteFilmById(1);
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of some Films by a specific name.")
	void testDeleteFilmsByNameCorrectly() {
		filmRepository.addFilm(1, "Bad Boys", 1983);
		filmRepository.addFilm(2, "Bad Boys",1995);
		filmRepository.deleteFilmsByName("Bad Boys");
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of a Film by a specific name.")
	void testDeleteOneFilmByNameCorrectly() {
		filmRepository.addFilm(1, "La Bella Stagione", 2022);
		filmRepository.addFilm(2, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.deleteFilmsByName("La Bella Stagione");
		List<Film> films = filmRepository.findAll();
		int filmsCount = films.size();
		assertThat(filmsCount).isEqualTo(1);
		assertThat(films.get(0).getId()).isEqualTo(2);
		assertThat(films.get(0).getName()).isEqualTo("Stanley Laurel and Oliver Hardy");
	}
	
	@Test
	@DisplayName("Test the correct deletion of some Films by a specific date.")
	void testDeleteFilmsByDateCorrectly() {
		filmRepository.addFilm(1, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.addFilm(2, "Charlie Chaplin", 1930);
		filmRepository.deleteFilmsByDate(1930);
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test the correct deletion of a Film by a specific date.")
	void testDeleteOneFilmByDateCorrectly() {
		filmRepository.addFilm(1, "La Bella Stagione", 2022);
		filmRepository.addFilm(2, "Stanley Laurel and Oliver Hardy", 1930);
		filmRepository.deleteFilmsByDate(2022);
		List<Film> films = filmRepository.findAll();
		int filmsCount = films.size();
		assertThat(filmsCount).isEqualTo(1);
		assertThat(films.get(0).getId()).isEqualTo(2);
		assertThat(films.get(0).getName()).isEqualTo("Stanley Laurel and Oliver Hardy");
	}

}
