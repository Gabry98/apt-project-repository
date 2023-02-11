package io.github.gabry98.app.filmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import io.github.gabry98.app.filmapp.model.Film;
import io.github.gabry98.app.filmapp.repository.mongo.FilmMongoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Integration Tests for Film Repository.")
@Testcontainers
class FilmMongoRepositoryIT {

	@Container
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "filmCollection";
	
	private MongoClient client;
	private FilmMongoRepository filmRepository;
	
	@BeforeEach
	public void setup() {
		
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getMappedPort(27017))
		);
		
		filmRepository = new FilmMongoRepository(client,DATABASE_NAME,COLLECTION_NAME);
		
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		client.close();
	}
	
	@Test
	@DisplayName("Test when we add a Film.")
	void testAddFilm() {
		filmRepository.addFilm(1, "King Kong", 1950);
		List<Film> films = filmRepository.findAll();
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(0).getName()).isEqualTo("King Kong");
		assertThat(films.get(0).getDate()).isEqualTo(1950);
	}
	
	@Test
	@DisplayName("Test when we remove a Film by id.")
	void testRemoveFilmById() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.deleteFilmById(1);
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Film by name.")
	void testRemoveFilmByName() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "King Kong", 2005);
		filmRepository.deleteFilmsByName("King Kong");
		assertThat(filmRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Film by date.")
	void testRemoveFilmByDate() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.addFilm(2, "King Kong", 2005);
		filmRepository.deleteFilmsByDate(1950);
		List<Film> films = filmRepository.findAll();
		assertThat(films).hasSize(1);
		assertThat(films.get(0).getId()).isEqualTo(2);
		assertThat(films.get(0).getName()).isEqualTo("King Kong");
		assertThat(films.get(0).getDate()).isEqualTo(2005);
	}
	
	@Test
	@DisplayName("Test when we update a Film by id.")
	void testUpdateFilmById() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.updateFilmId(1, 2);
		Film f = filmRepository.findById(2);
		assertThat(f.getId()).isEqualTo(2);
		assertThat(f.getName()).isEqualTo("King Kong");
		assertThat(f.getDate()).isEqualTo(1950);
	}
	
	@Test
	@DisplayName("Test when we update a Film by name.")
	void testUpdateFilmByName() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.updateFilmName(1, "King");
		List<Film> films = filmRepository.findByName("King");
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(0).getName()).isEqualTo("King");
		assertThat(films.get(0).getDate()).isEqualTo(1950);
	}
	
	@Test
	@DisplayName("Test when we update a Film by date.")
	void testUpdateFilmByDate() {
		filmRepository.addFilm(1, "King Kong", 1950);
		filmRepository.updateFilmDate(1, 2005);
		List<Film> films = filmRepository.findByDate(2005);
		assertThat(films.get(0).getId()).isEqualTo(1);
		assertThat(films.get(0).getName()).isEqualTo("King Kong");
		assertThat(films.get(0).getDate()).isEqualTo(2005);
	}

}
