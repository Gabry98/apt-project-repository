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

import io.github.gabry98.app.filmapp.model.Report;
import io.github.gabry98.app.filmapp.repository.mongo.ReportMongoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Integration Tests for Report Repository.")
@Testcontainers
class ReportMongoRepositoryIT {

	@Container
	public static final MongoDBContainer mongo = 
		new MongoDBContainer("mongo:4.4.3");
	
	private static final String DATABASE_NAME = "project-db";
	private static final String COLLECTION_NAME = "reportCollection";
	
	private MongoClient client;
	private ReportMongoRepository reportRepository;
	
	@BeforeEach
	public void setup() {
		
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getMappedPort(27017))
		);
		
		reportRepository = new ReportMongoRepository(client,DATABASE_NAME,COLLECTION_NAME);
		
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		client.close();
	}
	
	@Test
	@DisplayName("Test when we add a Report.")
	void testAddReport() {
		reportRepository.addReport(1,1,1);
		List<Report> reports = reportRepository.findAll();
		assertThat(reports.get(0).getId()).isEqualTo(1);
		assertThat(reports.get(0).getActorId()).isEqualTo(1);
		assertThat(reports.get(0).getFilmId()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("Test when we remove a Report by id.")
	void testRemoveReportById() {
		reportRepository.addReport(1,2,3);
		reportRepository.deleteReportById(1);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Report by actorId.")
	void testRemoveReportByActorId() {
		reportRepository.addReport(1,2,3);
		reportRepository.deleteReportByActorId(2);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we remove a Report by filmId.")
	void testRemoveReportByFilmId() {
		reportRepository.addReport(1,2,3);
		reportRepository.deleteReportByFilmId(3);
		assertThat(reportRepository.findAll()).isEmpty();
	}
	
	@Test
	@DisplayName("Test when we update a Report by id.")
	void testUpdateReportById() {
		reportRepository.addReport(1,2,3);
		reportRepository.updateReportId(1, 2);
		Report r = reportRepository.findById(2);
		assertThat(r.getId()).isEqualTo(2);
		assertThat(r.getActorId()).isEqualTo(2);
		assertThat(r.getFilmId()).isEqualTo(3);
	}
	
	@Test
	@DisplayName("Test when we find a Report by actorId.")
	void testFindReportByActorId() {
		reportRepository.addReport(1,2,3);
		reportRepository.addReport(4,5,6);
		List<Report> reports = reportRepository.findByActorId(2);
		assertThat(reports).hasSize(1);
		assertThat(reports.get(0).getId()).isEqualTo(1);
		assertThat(reports.get(0).getActorId()).isEqualTo(2);
		assertThat(reports.get(0).getFilmId()).isEqualTo(3);
	}
	
	@Test
	@DisplayName("Test when we find a Report by filmId.")
	void testFindReportByFilmId() {
		reportRepository.addReport(1,2,3);
		reportRepository.addReport(4,5,6);
		List<Report> reports = reportRepository.findByFilmId(6);
		assertThat(reports).hasSize(1);
		assertThat(reports.get(0).getId()).isEqualTo(4);
		assertThat(reports.get(0).getActorId()).isEqualTo(5);
		assertThat(reports.get(0).getFilmId()).isEqualTo(6);
	}

}
