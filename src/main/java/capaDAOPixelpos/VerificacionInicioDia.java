package capaDAOPixelpos;

import java.sql.*;
import java.util.Date;

import capaConexion.ConexionBaseDatos;

import java.text.*;

public class VerificacionInicioDia {

	public static int verificarInicioDia(String dsnTienda) {
		// TODO Auto-generated method stub
		//Obteniendo el dia de apertura
		int respuesta = 1;
		try {
			ConexionBaseDatos conexion = new ConexionBaseDatos();
			Connection con = conexion.obtenerConexionBDTienda(dsnTienda);
			
			String sqlEmployee = "UPDATE DBA.employee SET STARTWORK= ?, ENDWORK=?, ISCLOCKEDIN=4, STARTTRANS=?, ENDTRANS=0, PUNCHINDEX=? WHERE EMPNUM=777";
			String sqlPunchClock = "INSERT INTO DBA.PUNCHCLOCK(UNIQUEID,PunchIndex,TYPEPUNCH,PUNCHIN,TRANSTART,PAYRATE,JOBTYPE,EmpNUM,StoreNum,POSDetailStart," + 
					"POSDetailEnd,OPENDATE,UpdateStatus,ShiftIndex,QUANVOID,VOIDSALES,OriginalPunchIN,NumNoSale,MealTime,RevCenter,IsPaid," + 
					"ShiftRuleId,TillBalance) VALUES (?,?,4,?,?,0,2001,777,0,?,0,?,1,0,0,0,?,0,1,999,1,0,0)";
			String sqlUserPunch = "SELECT * FROM dba.PUNCHCLOCK a WHERE a.OPENDATE = ? AND a.PUNCHOUT IS NULL AND a.EmpNUM = 777";
			
			PreparedStatement Employee = null, PunchClock = null , UserPunch = null;
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date parsedDate = dateFormat.parse("1899-12-29");
			java.sql.Date Inicialts = new java.sql.Date(parsedDate.getTime());
			Statement state = con.createStatement();
			String consulta = "select DBA.PixOpenDate() as Opendate";
			ResultSet rs;
			rs = state.executeQuery(consulta);
			rs.next();
			java.sql.Date DiaApertura = rs.getDate("OpenDate");
			System.out.println("Día de apertura como Date: "+DiaApertura);
			rs.close();
			
			Timestamp Inicialts2 = new java.sql.Timestamp(DiaApertura.getTime());
			UserPunch = con.prepareStatement(sqlUserPunch);
			UserPunch.setDate(1, DiaApertura);
			rs = UserPunch.executeQuery();
			
			if (DiaApertura.equals(Inicialts)) {
				// Se cierra la conexión a la BD y se retorna el valor de -1 con el fin de interpretar el error y arrojar un 
				//mensaje al usuario
				con.close();
				return(-1);
			}else if (rs.next()) {
				//EL usuario internet  ya está ingresado en el sistema
				rs.close();
			}else {
				
				
				//Obteniendo PUNCHINDEX
				CallableStatement proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
				proc.registerOutParameter(1, Types.INTEGER);
				proc.setString(2, "GETNEXT_PUNCHCLOCK");
				proc.execute();
				int intPUNCHINDEX = proc.getInt(1);
				proc.close();
								
				//Obteniendo PunchClockUNQ
				proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
				proc.registerOutParameter(1, Types.INTEGER);
				proc.setString(2, "GETNEXT_PunchClockUNQ");
				proc.execute();
				int intPunchClockUNQ = proc.getInt(1);
				proc.close();
								
				String StrMaxTransact = "SELECT max(HowPaidLink) TranStart FROM dba.Howpaid";
				int MaxTransact = 0;
				rs = state.executeQuery(StrMaxTransact);
				rs.next();
				MaxTransact = rs.getInt("TranStart");
				rs.close();
								
				String StrPosDetailStart = "SELECT max(UNIQUEID) AS MaxPosDetail FROM dba.POSDETAIL";
				int PosDetailStart = 0;
				rs = state.executeQuery(StrPosDetailStart);
				rs.next();
				PosDetailStart = rs.getInt("MaxPosDetail");
				rs.close();
								
				ts = new Timestamp(System.currentTimeMillis());
				
				
				//Actualizando tabla Employee para el usuario Internet Memcode = 777
				Employee = con.prepareStatement(sqlEmployee);
				Employee.setTimestamp(1, ts);//STARTWORK
				Employee.setTimestamp(2, ts);//ENDWORK
				Employee.setInt(3, MaxTransact);//STARTTRANS
				Employee.setInt(4, intPUNCHINDEX);//PUNCHINDEX
				
				Employee.execute();
				Employee.close();
				
				//Insertando registro en la tabla PUNCHCLOCK para el usuario Internet Memcode = 777
				PunchClock = con.prepareStatement(sqlPunchClock);
				PunchClock.setInt(1, intPunchClockUNQ);//UNIQUEID
				PunchClock.setInt(2, intPUNCHINDEX);//PunchIndex
				PunchClock.setTimestamp(3, ts);//PUNCHIN
				PunchClock.setInt(4, MaxTransact);//TRANSTART
				PunchClock.setInt(5, PosDetailStart);//POSDetailStart
				PunchClock.setDate(6, DiaApertura);//OPENDATE
				PunchClock.setTimestamp(7, ts);//OriginalPunchIN
				
				PunchClock.execute();
				PunchClock.close();
			}
			
			System.out.println("-----Listo-----");
			con.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			
		}
		return(respuesta);
	}

}