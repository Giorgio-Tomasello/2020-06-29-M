package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Coppie;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Director> listMoviesDirectors(int anno, Map<Integer, Director> mappaRegisti){
		
		String sql = "SELECT DISTINCT dir.* "
				+ "FROM movies_directors d, movies m, directors dir "
				+ "WHERE m.id = d.movie_id "
				+ "AND d.director_id = dir.id "
				+ "AND m.year = ?";
		
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				mappaRegisti.put(res.getInt("id"), director);
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Coppie> getPesoArchi(int anno, Map<Integer, Director> mappaRegisti){
		
		
		String sql = "SELECT md1.director_id AS d1, md2.director_id AS d2, COUNT(DISTINCT r1.actor_id) AS peso "
				+ "FROM movies_directors md1, movies_directors md2, roles r1, roles r2, movies m1, movies m2 "
				+ "WHERE md1.director_id > md2.director_id "
				+ "AND m1.year = ? "
				+ "AND m2.year = m1.year "
				+ "AND r1.actor_id = r2.actor_id "
				+ "AND r1.movie_id = m1.id "
				+ "AND r2.movie_id = m2.id "
				+ "AND md1.movie_id = m1.id "
				+ "AND md2.movie_id = m2.id "
				+ "GROUP BY md1.director_id, md2.director_id";
		
		List<Coppie> result = new ArrayList<Coppie>();
		
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);

			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(new Coppie(mappaRegisti.get(res.getInt("d1")), 
						mappaRegisti.get(res.getInt("d2")), 
						res.getInt("peso")));
	
				
			}

				conn.close();
				return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
}
