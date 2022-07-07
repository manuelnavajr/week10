package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jdk.jfr.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;

public class ProjectDao extends DaoBase {

	private static final String Category_Table = "category";
	private static final String material_table = "material";
	private static final String project_table = "project";
	private static final String project_category_table = "project_category";
	private static final String step_table = "step";
	
	
	public Project insertProject(Project project) {
		
		String sql = ""
				+ "INSERT INTO " + project_table + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes"
				+ "VALUES "
				+ "(?, ?, ?, ?, ?,)";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(),BigDecimal.class);
				setParameter(stmt, 3,project.getActualHours(),BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, project_table);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
				
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
				
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	
}


	private void rollbackTransaction(Connection conn) {
		// TODO Auto-generated method stub
		
	}


	private void startTransaction(Connection conn) {
		// TODO Auto-generated method stub
		
	}


	private void commitTransaction(Connection conn) {
		// TODO Auto-generated method stub
		
	}


	private Integer getLastInsertId(Connection conn, String projectTable) {
		// TODO Auto-generated method stub
		return null;
	}


	private void setParameter(PreparedStatement stmt, int i, BigDecimal actualHours, Class<BigDecimal> class1) {
		// TODO Auto-generated method stub
		
	}


	private void setParameter(PreparedStatement stmt, int i, Integer difficulty, Class<Integer> class1) {
		// TODO Auto-generated method stub
		
	}


	private void setParameter(PreparedStatement stmt, int i, String notes, Class<String> class1) {
		// TODO Auto-generated method stub
	}	
	public List<Project> fetchAllProjects(){
		String sql = "SELECT * FROM " + project_table + "ORDER BY project_name";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				try(ResultSet rs = stmt.executeQuery()){
					List<Project> projects = new LinkedList<>();
					
					while(rs.next()) {
						projects.add(extract(rs, Project.class));
						
					}
					return projects;
				}
	}
			catch (Exception e) {
				rollBackTransaction(conn);
				throw new DbException(e);
				}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}
	public Optional<Project> fetchProjectById(Integer projectId){
		String sql = "SELECT * FROM " + project_table + "WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try {
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter (stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				if(Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getSteps().addAll(fetchCategoriesForProject(conn, projectId));
				}
				
				commitTransaction(conn);
				return Optional.ofNullable(project);
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		private List<Category> fetchCategoriesForProject(Connection conn
				Integer projectId) throws SQLException{
			String sql = ""
					+ "SELECT c.* FROM" + CATEGORY_TABLE + "c"
					+ "JOIN " + PROJECT_CATEGORY_TABLE + "pc USING (category_id"
					+ "WHERE project_id = ?";
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				
				try(ResultSet rs = stmt.executeQuery()){
					List<Category> categories = new LinkedList<>();
					
					while(rs.next()) {
						categories.add(extract(rs, Category.class));
					}
				}
			}
			private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException{
				String sql = "SELECT * FROM " + step_table + "WHERE project_id = ?";
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt,1,projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						List<Step> steps = new LinkedList<>();
						
						while(rs.next()) {
							steps.add(extract(rs, Step.class));
						}
						return steps;
					}
				}
			}
			private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId)
			throws SQLException{
				String sql = "SELECT * FROM " + MATERIAL_TABLE + "WHERE project_id = ?";
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						List<Material> materials = new LinkedList<>();
						
						while(rs.next()) {
							materials.add(extract(rs, Material.class));
						}
						return materials;
					}
				}
			}
		}
	}
}