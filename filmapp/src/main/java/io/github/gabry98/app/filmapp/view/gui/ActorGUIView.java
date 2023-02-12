package io.github.gabry98.app.filmapp.view.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.gabry98.app.filmapp.controller.StreamingController;
import io.github.gabry98.app.filmapp.jacoco.Generated;
import io.github.gabry98.app.filmapp.model.Actor;
import io.github.gabry98.app.filmapp.view.ActorView;

public class ActorGUIView extends JFrame implements ActorView {

	private static final Logger LOGGER = LogManager.getLogger(ActorGUIView.class);
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField actorId;
	private JTextField actorName;
	private JList<String> actorsList;
	private JList<String> findList;
	private DefaultListModel<String> actorsModel;
	private DefaultListModel<String> findModel;
	private transient StreamingController streamingController;
	private JScrollPane scrollActorsListPane;
	private JScrollPane scrollFindPane;
	private JLabel errorLabel;
	private JButton actorAdd;
	private JButton actorUpdate;
	private JButton actorRemove;
	private JButton actorFind;

	/**
	 * Launch the application.
	 */
	@Generated
	public static void main(String[] args) {
		EventQueue.invokeLater(ActorGUIView::run);
	}
	
	@Generated
	private static void run() {
		try {
			ActorGUIView frame = new ActorGUIView();
			frame.setVisible(true);
		} catch (Exception e) {
			LOGGER.error("An Exception has been thrown during initialization of the View: {}", e.getMessage());
		}
	}

	/**
	 * Create the frame.
	 */
	public ActorGUIView() {
		this.setTitle("Actor View");
		this.setName("Actor View");
		findModel = new DefaultListModel<>();
		actorsModel = new DefaultListModel<>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel idLabel = new JLabel("Id:");
		idLabel.setBounds(10, 11, 46, 14);
		contentPane.add(idLabel);
		
		errorLabel = new JLabel("");
		errorLabel.setName("errorLabel");
		errorLabel.setForeground(new Color(255, 0, 0));
		errorLabel.setBounds(10, 230, 414, 20);
		contentPane.add(errorLabel);

		actorAdd = new JButton("ADD");
		actorAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkActorAdd();
			}
		});
		actorAdd.setEnabled(false);
		actorAdd.setBounds(113, 131, 86, 23);
		contentPane.add(actorAdd);
		
		actorUpdate = new JButton("UPDATE");
		actorUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkActorUpdate();
			}
		});
		actorUpdate.setEnabled(false);
		actorUpdate.setBounds(10, 158, 89, 23);
		contentPane.add(actorUpdate);
		
		actorRemove = new JButton("REMOVE");
		actorRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkActorRemove();
			}
		});
		actorRemove.setEnabled(false);
		actorRemove.setBounds(110, 196, 89, 23);
		contentPane.add(actorRemove);
		
		actorFind = new JButton("FIND");
		actorFind.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkActorFind();
			}
		});
		actorFind.setBounds(211, 158, 89, 23);
		contentPane.add(actorFind);
		
		actorId = new JTextField();
		actorId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndActorEmpty = !actorId.getText().trim().isEmpty() && !actorName.getText().trim().isEmpty();
				boolean idNotEmptyAndActorEmpty = !actorId.getText().trim().isEmpty() && actorName.getText().trim().isEmpty();
				boolean actorNotEmptyAndIdEmpty = actorId.getText().trim().isEmpty() && !actorName.getText().trim().isEmpty();
				boolean noSelection = actorsList.isSelectionEmpty();
				checkActorId(idAndActorEmpty, idNotEmptyAndActorEmpty, actorNotEmptyAndIdEmpty, noSelection);
			}
		});
		actorId.setName("actorId");
		actorId.setBounds(32, 8, 86, 20);
		contentPane.add(actorId);
		actorId.setColumns(10);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(128, 11, 46, 14);
		contentPane.add(nameLabel);
		
		actorName = new JTextField();
		actorName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean idAndNameEmpty = !actorName.getText().trim().isEmpty() && !actorId.getText().trim().isEmpty();
				boolean actorNotEmptyAndIdEmpty = !actorName.getText().trim().isEmpty() && actorId.getText().trim().isEmpty();
				boolean idNotEmptyAndActorEmpty = actorName.getText().trim().isEmpty() && !actorId.getText().trim().isEmpty();
				boolean noSelection = actorsList.isSelectionEmpty();
				checkActorName(idAndNameEmpty, idNotEmptyAndActorEmpty, actorNotEmptyAndIdEmpty, noSelection);
			}
		});
		actorName.setName("actorName");
		actorName.setBounds(165, 8, 259, 20);
		contentPane.add(actorName);
		actorName.setColumns(10);
		
		actorsList = new JList<>();
		actorsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean noSelection = actorsList.isSelectionEmpty();
				boolean idNotEmpty = !actorId.getText().trim().isEmpty();
				boolean actorNotEmpty = !actorName.getText().trim().isEmpty();
				checkActorsList(noSelection, idNotEmpty, actorNotEmpty);
			}
		});
		actorsList.setName("actorsList");
		actorsList.setBounds(10, 113, 414, -70);
		actorsList.setBackground(new Color(192, 192, 192));
		actorsList.setForeground(new Color(0, 128, 0));
		actorsList.setModel(actorsModel);
		
		findList = new JList<>();
		findList.setName("findList");
		findList.setBounds(221, 185, 203, 37);
		findList.setBackground(new Color(192, 192, 192));
		findList.setForeground(new Color(0, 128, 0));
		findList.setModel(findModel);
		
		scrollActorsListPane = new JScrollPane();
		scrollActorsListPane.setBounds(10, 34, 414, 79);
		scrollActorsListPane.setBackground(new Color(192, 192, 192));
		findList.setForeground(new Color(0, 128, 0));
		scrollActorsListPane.setViewportView(actorsList);
		contentPane.add(scrollActorsListPane);
		
		scrollFindPane = new JScrollPane();
		scrollFindPane.setBounds(221, 185, 203, 37);
		scrollFindPane.setBackground(new Color(192, 192, 192));
		scrollFindPane.setViewportView(findList);
		contentPane.add(scrollFindPane);
	}

	@Override
	public void actorAdded(int id, String name) {
		SwingUtilities.invokeLater(() -> actorsModel.addElement(id+","+name));
	}

	@Override
	public void actorDeletedById(int position) {
		SwingUtilities.invokeLater(() -> actorsModel.removeElementAt(position));
	}

	@Override
	public void actorsDeletedByName(List<Actor> deletedActors) {
		SwingUtilities.invokeLater(() -> deletedActors.forEach(a -> actorsModel.removeElement(a.getId()+","+a.getName())));
	}

	@Override
	public void actorUpdated(int oldId, int newId, int position) {
		SwingUtilities.invokeLater(() -> {
			String element = actorsModel.get(position);
			actorsModel.removeElementAt(position);
			actorsModel.insertElementAt(newId+","+element.split(",")[1], position);
		});
	}

	@Override
	public void actorUpdated(int oldId, String name, int position) {
		SwingUtilities.invokeLater(() -> {
			String element = actorsModel.getElementAt(position);
			actorsModel.removeElementAt(position);
			actorsModel.insertElementAt(element.split(",")[0]+","+name, position);
		});
	}

	@Override
	public void actorFound(int id, String name) {
		SwingUtilities.invokeLater(() -> findModel.addElement(id+","+name));
	}

	public void setStreamingController(StreamingController streamingController) {
		SwingUtilities.invokeLater(() -> this.streamingController = streamingController);
	}
	
	protected void checkActorAdd() {
		try {
			int id = Integer.parseInt(actorId.getText().trim());
			String name = actorName.getText();
			streamingController.addActor(this, id, name);
			errorLabel.setText("");
		} catch(Exception exc) {
			errorLabel.setText(exc.getMessage());
		}
		actorId.setText("");
		actorName.setText("");
		actorAdd.setEnabled(false);
	}
	
	protected void checkActorUpdate() {
		String element = actorsModel.getElementAt(actorsList.getSelectedIndex());
		
		try {
			if(!actorName.getText().trim().isEmpty()) {
				int elementId = Integer.parseInt(element.split(",")[0]);
				streamingController.updateActor(this, elementId, actorName.getText(), actorsList.getSelectedIndex());
			} else {
				int elementId = Integer.parseInt(element.split(",")[0]);
				streamingController.updateActor(this, elementId, Integer.parseInt(actorId.getText().trim()), 
						actorsList.getSelectedIndex());
			}
		} catch(Exception exc) {
			errorLabel.setText(exc.getMessage());
		}
		
		actorId.setText("");
		actorName.setText("");
		actorUpdate.setEnabled(false);
	}
	
	protected void checkActorRemove() {
		if(!actorName.getText().trim().isEmpty()) {
			streamingController.deleteActor(this, actorName.getText());
		} else {
			String element = actorsModel.getElementAt(actorsList.getSelectedIndex());
			int elementId = Integer.parseInt(element.split(",")[0]);
			streamingController.deleteActor(this, elementId, actorsList.getSelectedIndex());
		}
		
		actorId.setText("");
		actorName.setText("");
		actorRemove.setEnabled(false);
	}
	
	protected void checkActorFind() {
		findModel.removeAllElements();
		if(actorId.getText().trim().isEmpty() && actorName.getText().trim().isEmpty()) {
			streamingController.searchAllActors(this);
		} else if(!actorId.getText().trim().isEmpty()) {
				streamingController.searchActorById(this, Integer.parseInt(actorId.getText().trim()));
				actorId.setText("");
		} else {
				streamingController.searchActorsByName(this, actorName.getText());
				actorName.setText("");
		}
	}
	
	protected void checkActorId(boolean idAndActorEmpty, boolean idNotEmptyAndActorEmpty, 
			boolean actorNotEmptyAndIdEmpty, boolean noSelection) {
		actorAdd.setEnabled(idAndActorEmpty);
		actorUpdate.setEnabled((actorNotEmptyAndIdEmpty || idNotEmptyAndActorEmpty) && !noSelection);
	}
	
	protected void checkActorName(boolean idAndNameEmpty, boolean idNotEmptyAndActorEmpty, 
			boolean actorNotEmptyAndIdEmpty, boolean noSelection) {
		actorAdd.setEnabled(idAndNameEmpty);
		actorUpdate.setEnabled((actorNotEmptyAndIdEmpty || idNotEmptyAndActorEmpty) && !noSelection);
		actorRemove.setEnabled(actorNotEmptyAndIdEmpty);
	}
	
	protected void checkActorsList(boolean noSelection, boolean idNotEmpty, boolean actorNotEmpty) {
		actorUpdate.setEnabled(!(!idNotEmpty && !actorNotEmpty));
		actorRemove.setEnabled(!noSelection);
	}
}