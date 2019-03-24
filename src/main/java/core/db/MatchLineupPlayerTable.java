package core.db;

import core.model.match.MatchLineupPlayer;
import core.model.player.IMatchRoleID;
import core.util.HOLogger;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;



public final class MatchLineupPlayerTable extends AbstractTable {

	/** tablename **/
	public final static String TABLENAME = "MATCHLINEUPPLAYER";											
	
	protected MatchLineupPlayerTable(JDBCAdapter  adapter){
		super(TABLENAME,adapter);
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[16];
		columns[0]= new ColumnDescriptor("MatchID",Types.INTEGER,false);
		columns[1]= new ColumnDescriptor("TeamID",Types.INTEGER,false);
		columns[2]= new ColumnDescriptor("SpielerID",Types.INTEGER,false);
		columns[3]= new ColumnDescriptor("RoleID",Types.INTEGER,false);
		columns[4]= new ColumnDescriptor("Taktik",Types.INTEGER,false);
		columns[5]= new ColumnDescriptor("PositionCode",Types.INTEGER,false);
		columns[6]= new ColumnDescriptor("VName",Types.VARCHAR,false,255);
		columns[7]= new ColumnDescriptor("NickName",Types.VARCHAR,false,255);
		columns[8]= new ColumnDescriptor("Name",Types.VARCHAR,false,255);
		columns[9]= new ColumnDescriptor("Rating",Types.REAL,false);
		columns[10]= new ColumnDescriptor("HoPosCode",Types.INTEGER,false);
		columns[11]= new ColumnDescriptor("STATUS",Types.INTEGER,false);
		columns[12]= new ColumnDescriptor("FIELDPOS",Types.INTEGER,false);
		columns[13]= new ColumnDescriptor("RatingStarsEndOfMatch", Types.REAL, false);
		columns[14]= new ColumnDescriptor("StartPosition", Types.INTEGER, false);
		columns[15]= new ColumnDescriptor("StartBehaviour", Types.INTEGER, false);
	}

	@Override
	protected String[] getCreateIndizeStatements(){
		return new String[]{
			"CREATE INDEX iMATCHLINEUPPLAYER_1 ON "+getTableName()+"("+columns[2].getColumnName()+")",
			"CREATE INDEX iMATCHLINEUPPLAYER_2 ON "+getTableName()+"("+columns[0].getColumnName()+","+columns[1].getColumnName()+")"
		};
	}
	
	/**
	 * Gibt eine Liste an Ratings zurück, auf denen der Player gespielt hat: 0 = Max 1 = Min 2 =
	 * Durchschnitt 3 = posid
	 */
	Vector<float[]> getAlleBewertungen(int spielerid) {
		final Vector<float[]> bewertung = new Vector<float[]>();

		//Alle Möglichen Kombos durchlaufen
		for (byte i = 0; i < IMatchRoleID.substFW1; i++) { // Blaghaid changed the high end
			final float[] temp = getBewertungen4PlayerUndPosition(spielerid, i);

			//Min ein Wert für die Pos gefunden -> Max > 0
			if (temp[0] > 0) {
				//Erste Wert statt aktuellen wert mit der Posid füllen
				temp[3] = i;

				bewertung.add(temp);
			}
		}

		return bewertung;
	}
	
	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den Player, sowie die
	 * Anzahl der Bewertungen zurück // Match
	 */
	float[] getBewertungen4Player(int spielerid) {
		//Max, Min, Durchschnitt
		final float[] bewertungen = { 0f, 0f, 0f, 0f };

		try {
			final String sql = "SELECT MatchID, Rating FROM "+getTableName()+" WHERE SpielerID=" + spielerid;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			int i = 0;

			while (rs.next()) {
				float rating = rs.getFloat("Rating");

				if (rating > -1) {
					bewertungen[0] = Math.max(bewertungen[0], rating);

					if (bewertungen[1] == 0) {
						bewertungen[1] = rating;
					}

					bewertungen[1] = Math.min(bewertungen[1], rating);
					bewertungen[2] += rating;

					//HOLogger.instance().log(getClass(),rs.getInt("MatchID") + " : " + rating);

					i++;
				}
			}

			if (i > 0) {
				bewertungen[2] = (bewertungen[2] / i);
			}

			bewertungen[3] = i;

			//HOLogger.instance().log(getClass(),"Ratings     : " + i + " - " + bewertungen[0] + " / " + bewertungen[1] + " / " + bewertungen[2] + " / / " + bewertungen[3]);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getBewertungen4Player : " + e);
		}

		return bewertungen;
	}
	
	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den Player, sowie die
	 * Anzahl der Bewertungen zurück // Match
	 *
	 * @param spielerid Spielerid
	 * @param position Usere positionscodierung mit taktik
	 */
	float[] getBewertungen4PlayerUndPosition(int spielerid, byte position) {
		//Max, Min, Durchschnitt
		final float[] bewertungen = { 0f, 0f, 0f, 0f };

		try {
			final String sql = "SELECT MatchID, Rating FROM "+getTableName()+" WHERE SpielerID=" + spielerid + " AND HoPosCode=" + position;
			final ResultSet rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			int i = 0;

			while (rs.next()) {
				float rating = rs.getFloat("Rating");

				if (rating > -1) {
					bewertungen[0] = Math.max(bewertungen[0], rating);

					if (bewertungen[1] == 0) {
						bewertungen[1] = rating;
					}

					bewertungen[1] = Math.min(bewertungen[1], rating);
					bewertungen[2] += rating;

					//HOLogger.instance().log(getClass(),rs.getInt("MatchID") + " : " + rating);

					i++;
				}
			}

			if (i > 0) {
				bewertungen[2] = (bewertungen[2] / i);
			}

			bewertungen[3] = i;

			//HOLogger.instance().log(getClass(),"Ratings Pos : " + i + " - " + bewertungen[0] + " / " + bewertungen[1] + " / " + bewertungen[2] + " / / " + bewertungen[3]);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getBewertungen4PlayerUndPosition : " + e);
		}

		return bewertungen;
	}
	
	/**
	 * Deletes the given player based on teamID and matchID.
	 * He is only deleted from the role set in the player object.
	 * 
	 * @author blaghaid
	 */
	void deleteMatchLineupPlayer(MatchLineupPlayer player, int matchID, int teamID) {
		if (player != null) {
			final String[] where = { "MatchID" , "TeamID", "RoleID", "SpielerID"};
			final String[] werte = { "" + matchID, "" + teamID, "" + player.getId(), "" + player.getSpielerId()};			
			delete(where, werte);			
		}
	}
	
	/**
	 * Deletes all players from the given match
	 * 
	 * @author blaghaid
	 */
	void deleteMatchLineupPlayers(int matchID) {
		final String[] where = { "MatchID"};
		final String[] werte = { "" + matchID};			
		delete(where, werte);			
	}
	
	/**
	 * Updates a match lineup based on the given inputs
	 *
	 * @author blaghaid
	 */
	void updateMatchLineupPlayer(MatchLineupPlayer player, int matchID, int teamID) {
		// As some weirdness may be present in the db (like old role IDs), we do a delete and
		// insert rather than an update. It is not desired to end up with the same player in both
		// and old style and new style position.
		
		if (player != null) {

			// First, delete the player(s) present on the player's position. Hopefully the player given.
			
			final String[] where = { "MatchID" , "TeamID", "RoleID"};
			final String[] werte = { "" + matchID, "" + teamID, "" + player.getId()};			
			delete(where, werte);	

			// Second, check if the player is still around in the lineup.
			
			try {
				String sql = null;
				int roleId;

				sql = "SELECT * FROM "+getTableName()+" WHERE MatchID = " + matchID + " AND TeamID = " + teamID + " AND SpielerID = " + player.getSpielerId();
				ResultSet rs = adapter.executeQuery(sql);
				
				rs.beforeFirst();
				
				while (rs.next()) {
					
					roleId = rs.getInt("RoleID"); 
					
					if ((roleId >= 0) && (roleId < IMatchRoleID.setPieces)) {
						// We got an old roleId
						deleteMatchLineupPlayer(new MatchLineupPlayer(roleId, 0, player.getSpielerId(), 0, "", 0), matchID, teamID);
					}
				}
				
				// And we store the given player after doing our deletes
				storeMatchLineupPlayer(player, matchID, teamID);
				
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.updateMatchLineupPlayer Retrieval Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}	
	
	@SuppressWarnings("deprecation")
	void storeMatchLineupPlayer(MatchLineupPlayer player, int matchID, int teamID) {
		if (player != null) {
			
			// Need to check for spieler, there may now be multiple players with -1 role.
			// Should we delete here, anyways? Isn't that for update?
			
			final String[] where = { "MatchID" , "TeamID", "RoleID", "SpielerID"};
			final String[] werte = { "" + matchID, "" + teamID, "" + player.getId(), "" + player.getSpielerId()};			
			delete(where, werte);			
			String sql = null;

			//saven
			try {
				//insert vorbereiten
				sql = "INSERT INTO "+getTableName()+" ( MatchID, TeamID, SpielerID, RoleID, Taktik, PositionCode, VName, NickName, Name, Rating, HoPosCode, STATUS, FIELDPOS, RatingStarsEndOfMatch, StartPosition, StartBehaviour ) VALUES(";
				sql
					+= (matchID
						+ ","
						+ teamID
						+ ","
						+ player.getSpielerId()
						+ ","
						+ player.getId()
						+ ", "
						+ player.getTaktik()
						+ ", "
						+ player.getPositionCode()
						+ ", '"
						+ DBManager.insertEscapeSequences(player.getSpielerVName())
						+ "', '"
						+ DBManager.insertEscapeSequences(player.getNickName())
						+ "', '"
						+ DBManager.insertEscapeSequences(player.getSpielerName())
						+ "',"
						+ player.getRating()
						+ ", "
						+ player.getPosition()
						+ ", 0" // Status
						+ ","
						+ player.getPositionCode()
						+ ","
						+ player.getRatingStarsEndOfMatch()
						+ ","
						+ player.getStartPosition()
						+ ","
						+ player.getStartBehavior()
						+ " )");
				adapter.executeUpdate(sql);
			} catch (Exception e) {
				HOLogger.instance().log(getClass(),"DB.storeMatchLineupPlayer Error" + e);
				HOLogger.instance().log(getClass(),e);
			}
		}
	}	

	Vector<MatchLineupPlayer> getMatchLineupPlayers(int matchID, int teamID) {
		MatchLineupPlayer player = null;
		final Vector<MatchLineupPlayer> vec = new Vector<MatchLineupPlayer>();
		String sql;
		ResultSet rs;
		int roleID;
		int behavior;
		int spielerID;
		int startPos;
		int startBeh;
		double rating;
		double ratingStarsEndOfMatch;
		String vname;
		String name;
		String nickName;
		int positionsCode;

		try {
			sql = "SELECT * FROM "+getTableName()+" WHERE MatchID = " + matchID + " AND TeamID = " + teamID;
			rs = adapter.executeQuery(sql);

			rs.beforeFirst();

			while (rs.next()) {
				roleID = rs.getInt("RoleID");
				behavior = rs.getInt("Taktik");
				spielerID = rs.getInt("SpielerID");
				rating = rs.getDouble("Rating");
				ratingStarsEndOfMatch = rs.getDouble("RatingStarsEndOfMatch");
				vname = DBManager.deleteEscapeSequences(rs.getString("VName"));
				nickName = DBManager.deleteEscapeSequences(rs.getString("NickName"));
				name = DBManager.deleteEscapeSequences(rs.getString("Name"));
				positionsCode = rs.getInt("PositionCode");
				startPos = rs.getInt("StartPosition");
				startBeh = rs.getInt("StartBehaviour");
				
				switch (behavior) {
					case IMatchRoleID.OLD_EXTRA_DEFENDER :
						roleID = IMatchRoleID.middleCentralDefender;
						behavior = IMatchRoleID.NORMAL;
						break;
					case IMatchRoleID.OLD_EXTRA_MIDFIELD :
						roleID = IMatchRoleID.centralInnerMidfield;
						behavior = IMatchRoleID.NORMAL;
						break;
					case IMatchRoleID.OLD_EXTRA_FORWARD :
						roleID = IMatchRoleID.centralForward;
						behavior = IMatchRoleID.NORMAL;
				}

				roleID = convertOldRoleToNew(roleID);
				
				// Position code and field position was removed from constructor below.
				player = new MatchLineupPlayer(roleID, behavior, spielerID, rating, vname, nickName, name, rs.getInt("STATUS"), ratingStarsEndOfMatch, startPos, startBeh);
				vec.add(player);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DB.getMatchLineupTeam Error" + e);
		}

		return vec;
	}
		
		// Helpers
		
		 public int convertOldRoleToNew(int roleID) {
				if(IMatchRoleID.oldKeeper.contains(roleID)) return IMatchRoleID.keeper;
				else if (IMatchRoleID.oldRightBack.contains(roleID)) return IMatchRoleID.rightBack;
				else if (IMatchRoleID.oldLeftCentralDefender.contains(roleID))return IMatchRoleID.leftCentralDefender;
		    		else if (IMatchRoleID.oldRightCentralDefender.contains(roleID))	return IMatchRoleID.rightCentralDefender;
		    		else if (IMatchRoleID.oldLeftBack.contains(roleID))	return IMatchRoleID.leftBack;
		    		else if (IMatchRoleID.oldRightWinger.contains(roleID)) return IMatchRoleID.rightWinger;
		    		else if (IMatchRoleID.oldRightInnerMidfielder.contains(roleID))	return IMatchRoleID.rightInnerMidfield;
		    		else if (IMatchRoleID.oldLeftInnerMidfielder.contains(roleID))return IMatchRoleID.leftInnerMidfield;
		    		else if (IMatchRoleID.oldLeftWinger.contains(roleID))return IMatchRoleID.leftWinger;
		    		else if (IMatchRoleID.oldRightForward.contains(roleID))return IMatchRoleID.rightForward;
		    		else if (IMatchRoleID.oldLeftForward.contains(roleID))return IMatchRoleID.leftForward;
		    		else if (IMatchRoleID.oldSubstKeeper.contains(roleID))return IMatchRoleID.substGK1;
		    		else if (IMatchRoleID.oldSubstDefender.contains(roleID))return IMatchRoleID.substCD1;
		    		else if (IMatchRoleID.oldSubstMidfielder.contains(roleID))return IMatchRoleID.substIM1;
		    		else if (IMatchRoleID.oldSubstWinger.contains(roleID))return IMatchRoleID.substWI1;
		    		else if (IMatchRoleID.oldSubstForward.contains(roleID))	return IMatchRoleID.substFW1;
		    		else return roleID;
		    	}


}
