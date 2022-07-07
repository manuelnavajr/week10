package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.exception.DbException;
import projects.exception.ProjectService;
import projects.dao.DbConnection;
import projects.entity.Project;

public class ProjectsApp {
	
	private Scanner scannner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	public static void main(String[] args) {
		DbConnection.getConnection(); 		
	}
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
			);
	// @formatter:on
	public static void main1(String[] args) {
		new ProjectsApp().processUserSelections();
	}
	private void processUserSelections() {
	boolean done = false;
	
	while(!done) {
		try {
			int selection = getUserSelection();
			
			switch(selection) {
			case -1:
			done = exitMenu();
			break;
			
			case 1: 
			createProject();
			break;
			
			case 2:
				listProjects();
				break;
				
			case 3:
				selectProject();
				break;
			
			default:
				System.out.println("/n" + selection + "is not a valid selection. Try again. " );
				break;
			}
				}
		catch(Exception e) {
			System.out.println("/nError: " + e + "Try again.");
			}
		}
	}
	private void createProject() {
		String projectName = getStringInput("Enter the name of project");
		BigDecimal totalHours = getDecimalInput("Enter the total hours");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		String notes = getStringInput("Enter the project notes");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
	
		Project project = new Project();
		
		project.setNotes(notes);
		project.setDifficulty(difficulty);
		project.setActualHours(totalHours);
		project.setEstimatedHours(estimatedHours);
		project.setProjectName(projectName);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("Your project is created: "+ dbProject);
	}
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid decimal");
		}
	}
	private boolean exitMenu() {
		System.out.println("Exiting the menu");
		return true;
	}
	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("Enter a selection");
		
		return Objects.isNull(input) ? -1 : input;
	}
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid number.");}
	
	}
	
	private String getStringInput(String prompt) {
		System.out.println(prompt + ":");
		Scanner scanner = null;
		@SuppressWarnings("null")
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}
	private void printOperations() {
	System.out.println("/n These are the available selections. Press the enter key to quit: ");
	
	operations.forEach(line -> System.out.println("  " + line));
	
	if(Objects.isNull(curProject)) {
		System.out.println("/nYou are not working with a project");
	}
	else {
		System.out.println("/nYou are working with a project: + curProject");
	}
}
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
	
	}
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("/nProjects:");
		
		projects.forEach(project -> System.out.println("  " 
		+ project.getProjectId() + ":" + project.getProjectName()));
		
	}
	
	}
