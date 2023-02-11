package io.github.gabry98.app.filmapp.view;

import java.util.List;

import io.github.gabry98.app.filmapp.model.Report;

public interface ReportView {

	void reportAdded(int id, int actorId, int filmId);

	void reportDeletedById(int id, int position);

	void reportsDeletedByActorId(List<Report> deletedReports);

	void reportsDeletedByFilmId(List<Report> deletedReports);

	void reportUpdatedById(int oldId, int newId, int position);

	void reportFound(int id, int actorId, int filmId);

}
