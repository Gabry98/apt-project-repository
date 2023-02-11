package io.github.gabry98.app.filmapp.repository;

import java.util.List;
import io.github.gabry98.app.filmapp.model.Report;

public interface ReportRepository {
	
	List<Report> findAll();
	
	void addReport(int id, int actorId, int filmId);
	
	Report findById(int id);
	
	List<Report> findByActorId(int id);
	
	List<Report> findByFilmId(int id);
	
	void updateReportId(int oldId, int newId);
	
	void deleteReportById(int id);
	
	void deleteReportByActorId(int id);
	
	void deleteReportByFilmId(int id);
}
