package capaDAOPixelpos;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import capaConexion.ConexionBaseDatos;
import capaModeloWeb.Cliente;
import capaModeloWeb.DetallePedidoPixel;
import capaModeloWeb.PosDetailPixel;

import java.text.*;

/**
 * Clase Main que contiene la implementación de la inserción del pedido al sistema tienda.
 */
public class Main  {
	
	public Main()
	{
		
	}
	
	/**
	 * Definición de variables staticas de la clase main
	 */
	private ArrayList<PosDetailPixel> listPosdetail = new ArrayList<PosDetailPixel>();
	private double GlobalTotal = 0,GlobalNetTotal = 0,GlobalTax1 = 0,GlobalEfecty = 0;
	
	/**
	 * Método que se encarga de retornar un 
	 * @param a
	 * @return
	 */
	public  String PriceLetter(int a){
		String Prices[]={"PRICEMODE","PRICEA","PRICEB","PRICEC","PRICED","PRICEE","PRICEF","PRICEG","PRICEH","PRICEI","PRICEJ"};
		String Letter = null;
		
		if(a >=0 && a < Prices.length){
			Letter = Prices[a];
		}else{
			Letter = Prices[1];
		}
		return Letter;
	}
	
	public  void PosDetail(Connection con,ArrayList<capaModeloWeb.DetallePedidoPixel> pruebaPedido,int idEmployee,int NumFactura,java.sql.Date OpenDate){
		
		try {
			double Tax1 = 0, Quan=0, CostEach=0, OrigCostEach=0, NetCostEach=0;
			int i=0,j=0,l=0;
			int ApplyTax1 = 0,idPosDetail = 0 ,prodnum=0,recpos = 0,MasterItem = 0,QuestionId = -1, ProdType=0, StoreNum=0;
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			PreparedStatement Tax = null,StorenumAndPrice = null,NetandOrigCost = null;
			CallableStatement AutoInc = null;
			ResultSet rsTax = null,rsStorenumAndPrice = null,rsNetandOrigCost = null;
			PosDetailPixel temporal = null;
			String LineDes = null;
			String Question[] = {"QUESTION1","QUESTION2","QUESTION3","QUESTION4","QUESTION5"};
			String SqlPrice = "SELECT "+PriceLetter(0)+" FROM dba.Product WHERE PRODNUM = ?";
			String SqlStorenumAndPrice = "SELECT  a.pricemode, a.OPTIONINDEX FROM DBA.forcedchoices a , dba.questions b , dba.product c "
					+ "where a.optionindex = b.optionindex and a.choice = c.prodnum and a.optionindex = (SELECT "+Question[0]+" FROM dba.Product "
					+ "WHERE ISACTIVE = 1 AND "+Question[0]+" > 0 AND PRODNUM = ? ) AND c.PRODNUM = ? AND a.IsActive = 1 order by a.sequence asc";
			
			
			for(capaModeloWeb.DetallePedidoPixel cadaDetallePedido:pruebaPedido){
				ts = new Timestamp(System.currentTimeMillis());
				prodnum = cadaDetallePedido.getIdproductoext();
				Quan = cadaDetallePedido.getCantidad();
				
				if(prodnum != 0){
					//Obteniendo idPosDetail
					AutoInc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
					AutoInc.registerOutParameter(1, Types.INTEGER);
					AutoInc.setString(2, "GETNEXT_POSDETAIL");
					AutoInc.execute();
					idPosDetail = AutoInc.getInt(1);
					AutoInc.close();
					
					//Impuesto
					Tax = con.prepareStatement("SELECT USEITEMCAT FROM dba.Product WHERE PRODNUM = ?");
					Tax.setInt(1, prodnum);
					rsTax = Tax.executeQuery();
					rsTax.next();
					ApplyTax1 = rsTax.getInt(1);
					Tax.close();
					rsTax.close();
				}
				
				if(recpos == 0){
					MasterItem = idPosDetail;
				}else if (prodnum == 0) {
					MasterItem = 0;
				}else if (MasterItem == 0 && recpos != 0) {
					MasterItem = idPosDetail;
				}
				
				if (idPosDetail == MasterItem) {
					QuestionId = 0;
				}else if (prodnum == 2002) {
					QuestionId = MasterItem;
				}else {
					QuestionId = MasterItem;
				}
				
				if(prodnum == 2002){
					if (l == 0) {
						LineDes = "Dividir 1 de 2";
						l++;
					}else if (l == 1) {
						LineDes = "Dividir 2 de 2";
						l++;
					}else if (l == 2) {
						LineDes = "Fin de Division";
						l = 0;
					}
				}
							
				listPosdetail.add(new PosDetailPixel(idPosDetail,NumFactura,prodnum,idEmployee,idEmployee,CostEach,
						Quan,ts,recpos,ProdType,ApplyTax1,StoreNum,OpenDate,LineDes,MasterItem,QuestionId,OrigCostEach,NetCostEach));
				
				if(prodnum != 0){
					recpos++;				
				}
				
				QuestionId = -1;
				
			}
			
			for (i=0 ; i < listPosdetail.size() ;i++) {
				if (listPosdetail.get(i).getIdproductoext() != 0 & listPosdetail.get(i).getIdproductoext() != 2002) {
					for (j = i+1; j < listPosdetail.size() ;j++) {
						if(listPosdetail.get(j).getIdproductoext() == 0) {
							j=listPosdetail.size();
						}else if (listPosdetail.get(j).getIdproductoext() != 2002) {
							for (l = 0; l < Question.length; l++) {
								SqlStorenumAndPrice = "SELECT  a.pricemode, a.OPTIONINDEX FROM DBA.forcedchoices a , dba.questions b , dba.product c "
										+ "where a.optionindex = b.optionindex and a.choice = c.prodnum and a.optionindex = (SELECT "+Question[l]+" FROM dba.Product "
										+ "WHERE ISACTIVE = 1 AND "+Question[l]+" > 0 AND PRODNUM = ? ) AND c.PRODNUM = ? AND a.IsActive = 1 order by a.sequence asc";
								StorenumAndPrice = con.prepareStatement(SqlStorenumAndPrice);
								StorenumAndPrice.setInt(1, listPosdetail.get(i).getIdproductoext());
								StorenumAndPrice.setInt(2, listPosdetail.get(j).getIdproductoext());
								rsStorenumAndPrice = StorenumAndPrice.executeQuery();
								
								while (rsStorenumAndPrice.next()) {
									SqlPrice = "SELECT "+PriceLetter(rsStorenumAndPrice.getInt(1))+" FROM dba.Product WHERE PRODNUM = ?";
									NetandOrigCost = con.prepareStatement(SqlPrice);
									NetandOrigCost.setInt(1, listPosdetail.get(j).getIdproductoext());
									rsNetandOrigCost = NetandOrigCost.executeQuery();
									rsNetandOrigCost.next();
									NetCostEach = rsNetandOrigCost.getDouble(1);
									OrigCostEach = NetCostEach;
									NetandOrigCost.close();
									rsNetandOrigCost.close();
									
									temporal = listPosdetail.get(j);
									temporal.setStorenum(rsStorenumAndPrice.getInt(2));
									temporal.setNetcosteach(NetCostEach);
									temporal.setOrigcostech(OrigCostEach);
									listPosdetail.set(j, temporal);
									
									NetCostEach = 0;
									OrigCostEach = 0;
									
								}
								StorenumAndPrice.close();
								rsStorenumAndPrice.close();
							}
						}
					}
					if (listPosdetail.get(i).getStorenum() == 0 ) {
						SqlPrice = "SELECT PRICEA FROM dba.Product WHERE PRODNUM = ?";
						NetandOrigCost = con.prepareStatement(SqlPrice);
						NetandOrigCost.setInt(1, listPosdetail.get(i).getIdproductoext());
						rsNetandOrigCost = NetandOrigCost.executeQuery();
						rsNetandOrigCost.next();
						NetCostEach = rsNetandOrigCost.getDouble(1);
						OrigCostEach = NetCostEach;
						NetandOrigCost.close();
						rsNetandOrigCost.close();
						
						temporal = listPosdetail.get(i);
						temporal.setNetcosteach(NetCostEach);
						temporal.setOrigcostech(OrigCostEach);
						listPosdetail.set(i, temporal);
						
						NetCostEach = 0;
						OrigCostEach = 0;
					}
				}
			}
			
			for (i=0 ; i < listPosdetail.size() ;i++) {
				if(listPosdetail.get(i).getIdproductoext() != 0 & listPosdetail.get(i).getIdproductoext() != 2002){
					if(listPosdetail.get(i).getApplytax1() == 1){
						Tax1 = 0.08;
					}else{
						Tax1 = 0;
					}
					CostEach = listPosdetail.get(i).getOrigcostech()/(1+Tax1);
					temporal = listPosdetail.get(i);
					temporal.setCosteach(CostEach);
					listPosdetail.set(i, temporal);
					
					CostEach = 0;
				}
			}						
		} catch (SQLException e) {
			e.getMessage();
		}	
	}
	
	public void PosHeader(Connection con,int idEmployee,int NumFactura,int MemCode,int MethodNum,double Change,
			java.sql.Timestamp Inits,java.sql.Date OpenDate) {
		try {
			int i=0,intPunchIndex=0,idHowpaid=0;
			double FinalTotal = 0,Tax1Able = 0,NetTotal=0,Tax1=0;
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			CallableStatement proc = null;
			PreparedStatement PosHeader = null,PunchIndex = null,Howpaid = null;
			ResultSet rs = null;
			String sqlPosHeader =  "INSERT INTO DBA.POSHEADER (TRANSACT,TABLENUM,TIMESTART,TIMEEND,NUMCUST,TAX1,TAX2,TAX3,TAX4,TAX5,"
					 +"TAX1ABLE,TAX2ABLE,TAX3ABLE,TAX4ABLE,TAX5ABLE,NETTOTAL,WHOSTART,WHOCLOSE,ISSPLIT,SALETYPEINDEX,EXP,STATNUM,STATUS,FINALTOTAL,"
					 +"PUNCHINDEX,Gratuity,OPENDATE,MemCode,TotalPoints,PointsApplied,UpdateStatus,ISDelivery,ScheduleDate,Tax1Exempt,Tax2Exempt,"
					 +"Tax3Exempt,Tax4Exempt,Tax5Exempt,MEMRATE,MealTime,IsInternet,RevCenter,PunchIdxStart,StatNumStart,SecNum,GratAmount,ShipTo, EnforcedGrat) "
					 +"VALUES(?,30001,?,?,1,?,0,0,0,0,?,0,0,0,0,?,?,?,1,1005,1,1,3,?,?,0,?,?,0,0,1,1,?,0,0,0,0,0,1,1,0,999,?,1,0,0,0,0)";
			String SqlHowpaid = "INSERT INTO DBA.Howpaid(HowPaidLink,TRANSDATE,EMPNUM,TENDER,METHODNUM,CHANGE,AUTHORIZED,MEMCODE,ExchangeRate,"
					 +"TRANSACT,PayType,OPENDATE,PUNCHINDEX,UpdateStatus,Settled,Status,Approved,STATNUM,IsPayInOut,MealTime,RevCenter,Voided,VoidedLink)"
					 +"VALUES(?,?,?,?,?,?,199,?,1,?,114,?,?,1,1,3,1,1,0,1,999,0,0)";
			
			//Obteniendo PunchIndex
			PunchIndex = con.prepareStatement("SELECT a.PunchIndex FROM DBA.PUNCHCLOCK a WHERE  a.EmpNUM = 777 AND a.OPENDATE = ?");
			PunchIndex.setDate(1, OpenDate);
			rs = PunchIndex.executeQuery();
			rs.next();
			intPunchIndex = rs.getInt(1);
			PunchIndex.close();
			rs.close();
			 
			for (i=0 ; i < listPosdetail.size() ; i++) {
				if (listPosdetail.get(i).getIdproductoext() != 0 & listPosdetail.get(i).getIdproductoext() != 2002) {
					FinalTotal += listPosdetail.get(i).getOrigcostech()*listPosdetail.get(i).getCantidad();
					NetTotal += listPosdetail.get(i).getCosteach()*listPosdetail.get(i).getCantidad();
					if (listPosdetail.get(i).getApplytax1() == 1) {
						Tax1Able += listPosdetail.get(i).getCosteach()*listPosdetail.get(i).getCantidad();
					}
				}
			}
			
			NetTotal = Math.round(NetTotal);
			Tax1 = FinalTotal-NetTotal;
			GlobalTotal = FinalTotal;
			GlobalNetTotal = NetTotal;
			GlobalTax1 = Tax1;
			
			PosHeader = con.prepareStatement(sqlPosHeader);
			PosHeader.setInt(1, NumFactura);//TRANSACT
			PosHeader.setTimestamp(2, ts);//TIMESTART
			PosHeader.setTimestamp(3, ts);//TIMEEND
			PosHeader.setDouble(4, Tax1);//TAX1
			PosHeader.setDouble(5, Tax1Able);//TAX1ABLE
			PosHeader.setDouble(6, NetTotal);//NETTOTTAL
			PosHeader.setInt(7, idEmployee);//WHOSTART
			PosHeader.setInt(8, idEmployee);//WHOCLOSE
			PosHeader.setDouble(9, FinalTotal);//FINALTOTAL 
			PosHeader.setInt(10, intPunchIndex);//PUNCHINDEX
			PosHeader.setDate(11, OpenDate);//OPENDATE
			PosHeader.setInt(12, MemCode);//Memcode
			PosHeader.setTimestamp(13, Inits);//ScheduleDate
			PosHeader.setInt(14, intPunchIndex);//PunchIdxStart
			 
			PosHeader.execute();
			PosHeader.close();
			
			//Insertando nueva transacciÃ³n en DBA.Howpaid
			ts = new Timestamp(System.currentTimeMillis());
			 
			proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
			proc.registerOutParameter(1, Types.INTEGER);
			proc.setString(2, "GETNEXT_HowPaid");
			proc.execute();
			idHowpaid = proc.getInt(1);
			proc.close();
			 
			Howpaid = con.prepareStatement(SqlHowpaid);
			Howpaid.setInt(1, idHowpaid);//HowPaidLink
			Howpaid.setTimestamp(2, ts);//TRANSDATE
			Howpaid.setInt(3, idEmployee);//EMPNUM
			Howpaid.setDouble(4, FinalTotal);//TENDER
			Howpaid.setInt(5, MethodNum);//METHODNUM
			Howpaid.setDouble(6, Change-FinalTotal);//CHANGE
			Howpaid.setInt(7, MemCode);//MEMCODE
			Howpaid.setInt(8, NumFactura);//TRANSACT
			Howpaid.setDate(9, OpenDate);//OPENDATE
			Howpaid.setInt(10, intPunchIndex);//PUNCHINDEX
			 
			Howpaid.execute();
			Howpaid.close();
			 
		} catch (SQLException e) {
			e.getMessage();			
		}
	}

	public void RunPosDetail(Connection con) {
		try {
			PreparedStatement AdPosDetail = null;
			String SqlPosDetail = "INSERT INTO DBA.POSDETAIL(UNIQUEID,TRANSACT,PRODNUM,WHOORDER,WHOAUTH,"
					+"COSTEACH,QUAN,TIMEORD,PRINTLOC,SEATNUM,Minutes,NOTAX,HOWORDERED,STATUS,NEXTPOS,"
					+"PRIORPOS,RECPOS,PRODTYPE,ApplyTax1,Applytax2,Applytax3,Applytax4,Applytax5,"
					+"ReduceInventory,StoreNum,STATNUM,RecipeCostEach,OpenDate,"
					+"MealTime,LineDes,REVCENTER,MasterItem,QuestionId,OrigCostEach,NetCostEach,UpdateStatus) "
					+"VALUES (?,?,?,?,?,"
					+"?,?,?,1,0,0,0,16,?,0,"
					+"0,?,?,?,0,0,0,0,"
					+"1,?,1,0,?,"
					+"1,?,999,?,?,?,?,1)";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select max(a.status) as resul from dba.posdetail a\r\n" + 
					"where a.OpenDate = dba.PixOpenDate() and \r\n" + 
					"a.statnum = 1 and\r\n" + 
					"a.PRODTYPE != 101 and\r\n" + 
					"a.status  not in (5000000) ");
			int status = 0; 
			while (rs.next())
			{
				status =  rs.getInt("resul");
				System.out.println(status);
			}
			status += 4;
			
			for(PosDetailPixel RunList: listPosdetail){
				if (RunList.getIdproductoext() != 0) {
					//Insertando en tabla PosDetail
					AdPosDetail = con.prepareStatement(SqlPosDetail);
					AdPosDetail.setInt(1, RunList.getIdposdetail());//UNIQUEID
					AdPosDetail.setInt(2, RunList.getNumfactura());//TRANSACT
					AdPosDetail.setInt(3, RunList.getIdproductoext());//PRODNUM
					AdPosDetail.setInt(4, RunList.getWhooder());//WHOORDER
					AdPosDetail.setInt(5, RunList.getWhoauth());//WHOAUTH
					AdPosDetail.setDouble(6, RunList.getCosteach());//COSTEACH
					AdPosDetail.setDouble(7, RunList.getCantidad());//QUAN
					AdPosDetail.setTimestamp(8, RunList.getTimeord());//TIMEORD
					AdPosDetail.setInt(9, status);//TIMEORD
					AdPosDetail.setInt(10, RunList.getRecpos());//RECPOS
					AdPosDetail.setInt(11, RunList.getProdtype());//PRODTYPE
					AdPosDetail.setInt(12, RunList.getApplytax1());//ApplyTax1
					AdPosDetail.setInt(13, RunList.getStorenum());//StoreNum
					AdPosDetail.setDate(14, RunList.getOpendate());//OpenDate
					AdPosDetail.setString(15, RunList.getLinedes());//LineDes
					AdPosDetail.setInt(16, RunList.getMasteritem());//MasterItem
					AdPosDetail.setInt(17, RunList.getQuestionid());//QuestionId
					AdPosDetail.setDouble(18, RunList.getOrigcostech());//OrigCostEach
					AdPosDetail.setDouble(19, RunList.getNetcosteach());//NetCostEach
					
					AdPosDetail.execute();
					AdPosDetail.close();
					status += 4;
				}
			}
			
			
		} catch (SQLException e) {
			e.getMessage();
		}
	}
	
	public static void Recipe(Connection con,ArrayList<capaModeloWeb.DetallePedidoPixel> pruebaPedido,int NumFactura){
		try {
			int ProdNum=0,InvenNum=0,idAdjustInventory=0;
			double Quan=0,CalcQuan=0,PosUsed=0,Units=0;
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			PreparedStatement Receta = null,StockLevels=null,UpdateStock=null,AdjustInventory=null;
			CallableStatement AutoInc = null;
			ResultSet rs = null,rsStockLevels=null;
			//Simplificar Querys
			String SqlStockLevels = "SELECT INVENNUM,UNITS,POSUSED,StartInv FROM DBA.StockLevels WHERE INVENNUM = ?";
			String SqlUpdateStock = "UPDATE dba.StockLevels SET UNITS = ? , POSUSED = ? WHERE INVENNUM = ?";
			String RecetaSQL = "SELECT a.UNIQUEID,a.PRODNUM,b.DESCRIPT,c.INVENNUM,c.DESCRIPT,a.USAGE,c.UNITDES "
					+ "FROM DBA.RECIPE a ,dba.Product b , dba.inventory c WHERE  a.PRODNUM = b.PRODNUM "
					+ "AND  a.INVENNUM = c.INVENNUM AND a.IsActive = 1 AND a.PRODNUM = ? ORDER BY a.INVENNUM asc";
			String SqlAdjustInventory = "INSERT INTO DBA.AdjustInventory (ADJUSTNUM,INVENNUM,ADJUSTUNITS,WAREHOUSENUM,"
					+ "WHOADJUST,EMPREF,Reason,AdjustType,SupplyNum,AdjustTime,WareHouseTO,STATE,PurPrice,ADJUSTCOST,"
					+ "PURID,TransNum) VALUES (?,?,?,1001,0,0,'POS Used',1,0,?,1001,1,0,0,1005,?)";
			
			for (capaModeloWeb.DetallePedidoPixel cadaDetallePedido:pruebaPedido) {
				ProdNum = cadaDetallePedido.getIdproductoext();
				Quan = cadaDetallePedido.getCantidad();
				if (ProdNum != 0 & ProdNum != 2002) {
					Receta = con.prepareStatement(RecetaSQL);
					Receta.setInt(1, ProdNum);
					rs = Receta.executeQuery();
					while(rs.next()){
						InvenNum = rs.getInt(4);
						CalcQuan = Quan*rs.getDouble(6);
						StockLevels = con.prepareStatement(SqlStockLevels);
						StockLevels.setInt(1, InvenNum);
						rsStockLevels = StockLevels.executeQuery();
						while (rsStockLevels.next()) {
							PosUsed = CalcQuan+rsStockLevels.getDouble(3);
							Units = rsStockLevels.getDouble(4)-PosUsed;
							UpdateStock = con.prepareStatement(SqlUpdateStock);
							UpdateStock.setDouble(1, Units);
							UpdateStock.setDouble(2, PosUsed);
							UpdateStock.setInt(3, InvenNum);
							UpdateStock.executeQuery();
							UpdateStock.close();
							
							//Obteniendo idAdjustInventory
							ts = new Timestamp(System.currentTimeMillis());
							AutoInc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
							AutoInc.registerOutParameter(1, Types.INTEGER);
							AutoInc.setString(2, "GETNEXT_StockLevel");
							AutoInc.execute();
							idAdjustInventory = AutoInc.getInt(1);
							AutoInc.close();
							
							AdjustInventory = con.prepareStatement(SqlAdjustInventory);
							AdjustInventory.setInt(1, idAdjustInventory);
							AdjustInventory.setInt(2, InvenNum);
							AdjustInventory.setDouble(3, CalcQuan);
							AdjustInventory.setTimestamp(4, ts);
							AdjustInventory.setInt(5, NumFactura);
							
							AdjustInventory.execute();
							AdjustInventory.close();
						}
						StockLevels.close();
						rsStockLevels.close();
					}
					Receta.close();
					rs.close();
					CalcQuan = 0;
				}
				ProdNum = 0;
				Quan = 0;
			}
		} catch (SQLException e) {
			e.getMessage();
		}
	}
	
	public void Invoice(Connection con,int Numfactura,int idEmployee,int MemCode, int MethodPay) {
		try {
			int Invoice1 = 0;
			String SqlStrInvoice = "Select DBA.StrInvoice(?,?,?,?)";
			String SqlStrComanda = "Select DBA.Comanda(?,?,?,?)";
			String SqlInvoice = "INSERT INTO DBA.MsgMgr(MsgNum,MsgTime,MsgType,MsgPrm,Data) VALUES (?,?,5,2002,?)";
			String StrInvoice = "";
			String Central = null;
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			PreparedStatement Invoice = null,MsgMgr = null;
			CallableStatement proc = null;
			ResultSet rsInvoice = null;
			
			//Imprimiendo comanda
			Central = CentralZoneComanda(con);
			
			//String Invoice
			Invoice = con.prepareStatement(SqlStrComanda);
			Invoice.setInt(1, Numfactura);
			Invoice.setInt(2, idEmployee);
			Invoice.setInt(3, MemCode);
			Invoice.setString(4, Central);
			
			rsInvoice = Invoice.executeQuery();
			rsInvoice.next();
			StrInvoice = rsInvoice.getString(1);
			
			System.out.println(StrInvoice);
			Invoice.close();
			rsInvoice.close();
			
			//Insertando Comanda
			proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
			proc.registerOutParameter(1, Types.INTEGER);
			proc.setString(2, "GetNext_MsgMgr");
			proc.execute();
			Invoice1 = proc.getInt(1);
			proc.close();
			
			ts = new Timestamp(System.currentTimeMillis());
			MsgMgr = con.prepareStatement(SqlInvoice);
			MsgMgr.setInt(1, Invoice1);
			MsgMgr.setTimestamp(2, ts);
			MsgMgr.setString(3, StrInvoice);
			
			MsgMgr.execute();
			MsgMgr.close();
			
			//Imprimiendo Factura
			Central = CentralZone(con, MethodPay);
			
			//String Invoice
			Invoice = con.prepareStatement(SqlStrInvoice);
			Invoice.setInt(1, Numfactura);
			Invoice.setInt(2, idEmployee);
			Invoice.setInt(3, MemCode);
			Invoice.setString(4, Central);
			
			rsInvoice = Invoice.executeQuery();
			rsInvoice.next();
			StrInvoice = rsInvoice.getString(1);
			
			System.out.println(StrInvoice);
			Invoice.close();
			rsInvoice.close();
			
			for (int i = 0; i < 2; i++) {
				//Insertando Factura
				proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
				proc.registerOutParameter(1, Types.INTEGER);
				proc.setString(2, "GetNext_MsgMgr");
				proc.execute();
				Invoice1 = proc.getInt(1);
				proc.close();
				
				ts = new Timestamp(System.currentTimeMillis());
				MsgMgr = con.prepareStatement(SqlInvoice);
				MsgMgr.setInt(1, Invoice1);
				MsgMgr.setTimestamp(2, ts);
				MsgMgr.setString(3, StrInvoice);
				
				MsgMgr.execute();
				MsgMgr.close();
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.getMessage();
		}
	}

	public String CentralZone(Connection con,int MethodPay){
		int i=0,j=0,PrintZero=0;
		double Quan = 0,Price=0;
		String CentralZone = "",SqlCentral = "SELECT DESCRIPT,PrintZero FROM DBA.Product WHERE PRODNUM = ?";
		String Specialty = "",StrMethodPay = "",SqlMethodPay = "SELECT DESCRIPT FROM dba.MethodPay WHERE METHODNUM = ?";
		NumberFormat Formatter = NumberFormat.getInstance(Locale.ENGLISH);
		StringBuilder StrPrice = null,StrQuan = null,TempChain = new StringBuilder(),FinalChain = new StringBuilder();
		PreparedStatement Central = null, Pay = null;
		ResultSet rsCentral = null, rsPay = null;
		
		for (i=0 ; i < listPosdetail.size() ; i++) {
			if (listPosdetail.get(i).getIdproductoext() != 0 & listPosdetail.get(i).getIdproductoext() != 2002) {
				try {
					Central = con.prepareStatement(SqlCentral);
					Central.setInt(1, listPosdetail.get(i).getIdproductoext());
					rsCentral = Central.executeQuery();
					rsCentral.next();
					Specialty = rsCentral.getString(1);
					PrintZero = rsCentral.getInt(2);
					
					if (PrintZero == 1 || listPosdetail.get(i).getOrigcostech() > 0) {
						Price = Math.round(listPosdetail.get(i).getOrigcostech()*listPosdetail.get(i).getCantidad());
						Quan = listPosdetail.get(i).getCantidad();
						
						Formatter.setMinimumFractionDigits(0);
						StrPrice = new StringBuilder(Formatter.format(Price));
						StrPrice.insert(0, "$ ");
						
						if ((Quan - Math.floor(Quan)) == 0) {
							Formatter.setMinimumFractionDigits(0);
						}else {
							Formatter.setMinimumFractionDigits(2);
						}
						
						StrQuan = new StringBuilder(Formatter.format(Quan));
						
						for (j = StrQuan.length(); j < 5; j++) {
							StrQuan = StrQuan.insert(0, " ");
						}
						
						if (Specialty.length() > 21) {
							Specialty = Specialty.substring(0, 21); 
						}
						
						TempChain = TempChain.append(StrQuan+" "+Specialty);
						
						for (j = TempChain.length(); j < (40 - StrPrice.length()); j++) {
							TempChain.insert(j, " ");
						}
						
						FinalChain.append(TempChain);
						FinalChain.append(StrPrice + "\r\n");
						TempChain = new StringBuilder();
					}
					Central.close();
					rsCentral.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		
		try {
			Pay = con.prepareStatement(SqlMethodPay);
			Pay.setInt(1, MethodPay);
			rsPay = Pay.executeQuery();
			rsPay.next();
			StrMethodPay = rsPay.getString(1);
			Pay.close();
			rsPay.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		if (MethodPay != 1001) {
			StrMethodPay = "^W" + StrMethodPay;			
		}
		
		Formatter.setMinimumFractionDigits(0);
		CentralZone = CentralZone + FinalChain.toString();
		CentralZone = CentralZone + "^L\r\n";
		CentralZone = CentralZone + "            Neto Total:      $"+Formatter.format(GlobalNetTotal)+"\r\n";
		CentralZone = CentralZone + "            INC 8%           $"+Formatter.format(GlobalTax1)+"\r\n";
		CentralZone = CentralZone + "            ================="+"\r\n";
		CentralZone = CentralZone + "            Sub-Total:       $"+Formatter.format(GlobalTotal)+"\r\n";
		CentralZone = CentralZone + "            ================="+"\r\n\r\n";
		CentralZone = CentralZone + "^C^W   TOTAL $"+Formatter.format(GlobalTotal)+"\r\n";
		CentralZone = CentralZone + "            Cambio:          $"+Formatter.format(GlobalEfecty-GlobalTotal)+"\r\n\r\n";
		CentralZone = CentralZone + "^L\r\n";
		CentralZone = CentralZone + StrMethodPay+" $"+Formatter.format(GlobalEfecty)+"\r\n";
		CentralZone = CentralZone + "^L\r\n";
		
		return CentralZone;
	}
	
	public String CentralZoneComanda(Connection con){
		int i=0,j=0;
		double Quan = 0;
		String CentralZoneComanda = "",SqlCentral = "SELECT PRINTDES FROM DBA.Product WHERE PRODNUM = ?";
		String Specialty = "";
		NumberFormat Formatter = NumberFormat.getInstance(Locale.ENGLISH);
		StringBuilder StrQuan = null,TempChain = new StringBuilder(),FinalChain = new StringBuilder();
		PreparedStatement Central = null;
		ResultSet rsCentral = null;
		
		for (i=0 ; i < listPosdetail.size() ; i++) {
			if (listPosdetail.get(i).getIdproductoext() != 0 & listPosdetail.get(i).getIdproductoext() != 2002) {
				try {
					Central = con.prepareStatement(SqlCentral);
					Central.setInt(1, listPosdetail.get(i).getIdproductoext());
					rsCentral = Central.executeQuery();
					rsCentral.next();
					Specialty = rsCentral.getString(1);
					
					Quan = listPosdetail.get(i).getCantidad();
					
					if ((Quan - Math.floor(Quan)) == 0) {
						Formatter.setMinimumFractionDigits(0);
					}else {
						Formatter.setMinimumFractionDigits(2);
					}
					
					StrQuan = new StringBuilder(Formatter.format(Quan));
															
					if (Specialty.length() > 21) {
						Specialty = Specialty.substring(0, 21); 
					}
					
					TempChain = TempChain.append("^W"+StrQuan+" "+Specialty);
					
					FinalChain.append(TempChain);
					FinalChain.append("\r\n");
					TempChain = new StringBuilder();
					
					Central.close();
					rsCentral.close();
				} catch (SQLException e) {
					e.getMessage();
				}
			}else if (listPosdetail.get(i).getIdproductoext() == 0) {
				FinalChain.append("=======================================\r\n");
			}else if (listPosdetail.get(i).getIdproductoext() == 2002) {
				if (j == 0) {
					FinalChain.append("^WDividir 1 de 2 \r\n");
					j++;
				}else if (j == 1) {
					FinalChain.append("^WDividir 2 de 2 \r\n");
					j++;
				}else if (j == 2) {
					FinalChain.append("^WFin Division \r\n");
					j = 0;
				}
			}
		}
		
		Formatter.setMinimumFractionDigits(0);
		CentralZoneComanda = CentralZoneComanda + FinalChain.toString();
		CentralZoneComanda = CentralZoneComanda + "^L\r\n";
		
		return CentralZoneComanda;
	}

	public boolean validarSiExistePedido(int idpedido, String dsnODBC)
	{
		ConexionBaseDatos conexion = new ConexionBaseDatos();
		Connection con = conexion.obtenerConexionBDTienda(dsnODBC);
		
		try
		{
			Statement state = con.createStatement();
			String consulta = "select * from DBA.pedidos_contact_center where idpedido = " + idpedido;
			ResultSet rs = state.executeQuery(consulta);
			while(rs.next())
			{
				return(true);
			}
			state.close();
			con.close();
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return(false);
		}
		return(false);
	}
	
	public boolean InsertarPedidoControl(int idpedido,int numfactura, String dsnODBC)
	{
		ConexionBaseDatos conexion = new ConexionBaseDatos();
		Connection con = conexion.obtenerConexionBDTienda(dsnODBC);
		
		try
		{
			Statement state = con.createStatement();
			String insertar = "insert into DBA.pedidos_contact_center (idpedido,transact) values ( " + idpedido + ", " + numfactura + ")";
			state.executeUpdate(insertar);
			state.close();
			con.close();
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return(false);
		}
		return(true);
	}
	
	/**
	 * Método que se encarga de devolver un valor booleano, indicando si el cliente se encuntra si o no en la base de datos
	 * @param memcode Memcode el cliente a buscar
	 * @param dsnODBC String de conexion para la tienda a la cual se tomará el pedido.
	 * @return Se retorna un valor booleano con el resultado de la validacion, true indica que el cliente si existe de lo contrario
	 * se retorna el valor de false.
	 */
	public boolean validaExistenciaCliente(int memcode, String dsnODBC)
	{
		ConexionBaseDatos conexion = new ConexionBaseDatos();
		Connection con = conexion.obtenerConexionBDTienda(dsnODBC);
		
		try
		{
			Statement state = con.createStatement();
			String consulta = "select * from dba.member where memcode = " + memcode;
			ResultSet rs = state.executeQuery(consulta);
			while(rs.next())
			{
				return(true);
			}
			state.close();
			con.close();
			return(false);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return(false);
		}
		
	}
	
	/**
	 * Método que se encarga de recibir los parámetros del pedido y de controlar la inserción del pedido en la tienda
	 * invocando los métodos qeu se encargan de sto.
	 * @param pruebaPedido Se recibe un arrayList con objetos de tipo DetallePedidoPixel que contienen el detalle del
	 * pedido con las respectivas homologaciones.
	 * @param dsnODBC Se recibe el nombre de DataSourceName de la tienda configurado en la estación tienda para realizar
	 * la conexión al sistema POS.
	 * @param MemCode  El código que identifica al cliente en el sistema POS de la tienda.
	 * @param MiembroEnviado Se recibe un objeto de la clase tienda con toda la informacion del cliente del pedido
	 * @param indicadorAct Se envía un valor booleano que indica si el cliente fue actualizado o no.
	 * @param valorFormaPago Se recibe el valor con el que se pagará el pedido.
	 * @param idpedido El número del pedido en el sistema de Contact Center
	 * @return Se retorna un valor intero con el número de transact o número de factura que arroja el sistema de pedidos
	 */
	public int main(ArrayList<capaModeloWeb.DetallePedidoPixel> pruebaPedido, String dsnODBC, int MemCode, Cliente MiembroEnviado, boolean indicadorAct, double valorFormaPago, int idpedido, int idformapagotienda) {
				
		int NumFactura = 0; 
		
			 //ESTO LO HEMOS UTILIZADO PARA LAS PRUEBAS UNITARIOS
			 
			//Realizamos una validacion llamando método para validar si un pedido ya está o no
			
			 //Class.forName("sybase.jdbc.sqlanywhere.IDriver");
			 //Connection con = DriverManager.getConnection("jdbc:sqlanywhere:dsn=PixelPC;uid=admin;pwd=xxx");//local
			 
			 // ESTO LO ESTABAMOS USANDO EN LAS PRUEBAS INTEGRALES
			 //DriverManager.registerDriver( (Driver)
			 //		 Class.forName( "sybase.jdbc.sqlanywhere.IDriver" ).newInstance() );
			 //Connection con = DriverManager.getConnection("jdbc:sqlanywhere:dsn=PixelSqlbase;uid=admin;pwd=xxx");//SystemPos
			 
			
			 
			 // ESTA ES LA FORMA PARï¿½METRICA
			 ConexionBaseDatos conexion = new ConexionBaseDatos();
			 Connection con = conexion.obtenerConexionBDTienda(dsnODBC);
			 
			 //Realizamos verificación de apertura de día e ingreso del usuario
			 int respVerDia = VerificacionInicioDia.verificarInicioDia(dsnODBC);
			 //Si el valor retornado en respVerDia es igual a -1 es porque el día no se ha aperturado
			 if(respVerDia == -1)
			 {
				 return(-1);
			 }
			 
			 //Validamos si el pedido ya se comenzó a insertar en cuyo caso no lo inserta
			 boolean respPedidoFueInsertado = validarSiExistePedido(idpedido,dsnODBC);
			 
			 if(respPedidoFueInsertado)
			 {
				 return(0);
			 }
			 
			 // Se valida según parámetros si el cliente efectivamente exista en la base de datos y que no sea un cliente nuevo
			 if (indicadorAct == true)
			 {
			 	boolean resExisteCliente = validaExistenciaCliente(MemCode,dsnODBC);
				 
				 if(!resExisteCliente)
				 {
					 return(0);
				 }
			 }
			 //Obteniendo el dia de apertura
			 java.sql.Date DiaApertura;
			 Statement state;
			 try
			 {
				 state = con.createStatement();
			 }catch(Exception e)
			 {
				 System.out.println("Problema creando el statement " + e.getMessage());
				 return(0);
			 }
			 
			 ResultSet rs;
			 
			 // Se obtiene el día de apertura del pedido
			 try{
				 state = con.createStatement();
				 String consulta = "select DBA.PixOpenDate() as Opendate";
				 rs = state.executeQuery(consulta);
				 rs.next();
				 DiaApertura = rs.getDate("OpenDate");
				 rs.close();
				 System.out.println("Día De Apertura: "+DiaApertura);
			 }catch(Exception e)
			 {
				 System.out.println("PROBLEMA EN obtener fecha de apertura " + e.getMessage());
				 return(0);
			 }
			 
			 		 
			 
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			 Date parsedDate = new Date();
			 try
			 {
				 parsedDate = dateFormat.parse("1899-12-30 00:00:00.000");
			 }catch(Exception e)
			 {
				 System.out.println("Problemas en parseo de fecha parseDate "+ e.getMessage());
			 }
			 Timestamp Inicialts = new java.sql.Timestamp(parsedDate.getTime());
			 System.out.println("Fecha Inicial Sistema: "+Inicialts);
			 
			 Timestamp ts = new Timestamp(System.currentTimeMillis());
			 System.out.println("Timestamp: "+ts);
			 
			 // Se define objeto para ser usado más abajo.
			 CallableStatement proc;
			 try
			 {
				//Obteniendo el numero de factura de acuerdo a los consecutivos que maneja el sistema
				 proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
				 proc.registerOutParameter(1, Types.INTEGER);
				 proc.setString(2, "GETNEXT_POSHEADER");
				 proc.execute();
				 NumFactura = proc.getInt(1);
				 proc.close();
				 System.out.println("NumFactura: "+NumFactura);
			 }catch (Exception e)
			 {
				 System.out.println("Problema obteniendo la siguiente secuencia de factura en el POS " + e.getMessage());
			 }
			 
			
			 
				//MemCode: Codigo del miembro o cliente
//				Cliente(int idcliente, String telefono, String nombres, String apellidos, String nombreCompania,
//						String direccion, String municipio, int idMunicipio, float latitud, float lontitud, String zonaDireccion,
//						String observacion, String tienda, int idtienda) 
				
			    // Se prepara la sentencia para realizar la inserción del cliente en caso de que sea necesario 
				PreparedStatement Member = null;
				String SqlInsertMember = "INSERT INTO DBA.Member(MEMCODE,FIRSTNAME,LASTNAME,ADRESS1,ADRESS2,CITY,PROV,STARTDATE,"
						+"EXPDATE,ANNIVER,HOMETELE,LASTVISIT,MEMDIS,CARDNUM,GROUPNUM,ISACTIVE,Country,"
						+"UpdateStatus,Directions,CompanyName,Type,HasBioReg,NoSolicit)"
						+"VALUES (?,?,?,?,?,?,'Antioquia',?,?,?,?,?,0,?,0,1,'',1,?,?,0,0,0)";
				String SqlUpdateMember = "UPDATE DBA.Member SET FIRSTNAME=?,LASTNAME=?,ADRESS1=?,ADRESS2=?,CITY=?,"
						+ "Directions=?,CompanyName=?,HOMETELE=? WHERE MEMCODE=?";
				
				
				
				//Si existe Memcode>0 y Bool = False **no hacer nada
				//Si existe Memcode>0 y Bool = True **Actualizar
				//No existe Memcode = 0 y ignora Bool **Crea
				
				try{
					if (MemCode == 0) {
						parsedDate = dateFormat.parse("2040-12-31 00:00:00.000");
						Timestamp LastDayTS = new java.sql.Timestamp(parsedDate.getTime());
										
						proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
						proc.registerOutParameter(1, Types.INTEGER);
						proc.setString(2, "GETNEXT_MEMBER");
						proc.execute();
						MemCode = proc.getInt(1);
						proc.close();
						
						Member = con.prepareStatement(SqlInsertMember);
						Member.setInt(1, MemCode);
						Member.setString(2, MiembroEnviado.getNombres());
						Member.setString(3, MiembroEnviado.getApellidos());
						Member.setString(4, MiembroEnviado.getDireccion());
						Member.setString(5, MiembroEnviado.getZonaDireccion());//revisar
						Member.setString(6, MiembroEnviado.getMunicipio());
						Member.setTimestamp(7, ts);
						Member.setTimestamp(8, LastDayTS);
						Member.setTimestamp(9, ts);
						Member.setString(10, MiembroEnviado.getTelefono());
						Member.setTimestamp(11, ts);
						Member.setString(12, Integer.toString(MemCode));
						Member.setString(13, MiembroEnviado.getObservacion());
						Member.setString(14, MiembroEnviado.getNombreCompania());
						
						Member.execute();
						Member.close();
						
						MiembroEnviado.setMemcode(MemCode);
						
					}else if (indicadorAct == true) {
						Member = con.prepareStatement(SqlUpdateMember);
						Member.setString(1, MiembroEnviado.getNombres());
						Member.setString(2, MiembroEnviado.getApellidos());
						Member.setString(3, MiembroEnviado.getDireccion());
						Member.setString(4, MiembroEnviado.getZonaDireccion());//revisar
						Member.setString(5, MiembroEnviado.getMunicipio());
						Member.setString(6, MiembroEnviado.getObservacion());
						Member.setString(7, MiembroEnviado.getNombreCompania());
						Member.setString(8, MiembroEnviado.getTelefono());
						Member.setInt(9, MemCode);
						
						Member.execute();
						Member.close();
					}
					
				}catch(Exception e)
				{
					System.out.println("Se tuvieron inconvenientes en la actualización/insercion del cliente " +  e.getMessage());
					return(0);
				}
				
			 // Se define forma de pago. Por el momento quemada en efectivo.			 
			 //MethodNum: Efectivo o Tarjeta
				
			 int MethodNum = idformapagotienda;
			 
			 //Cambio
			 GlobalEfecty = valorFormaPago;
			 
			 
			 // Se realiza inserción en tabla Posdetail
			 //Preparando ArraList PosDetail
			 PosDetail(con,pruebaPedido,777,NumFactura,DiaApertura);
			 
			 //Insertando nueva transacciÃ³n en dba.PosHeader y en DBA.Howpaid
			 PosHeader(con,777,NumFactura,MemCode,MethodNum,GlobalEfecty,Inicialts,DiaApertura);
			 
			 //Insertando nueva transaccion en dba.Tabinfo
			 
			 try
			 {
				 ts = new Timestamp(System.currentTimeMillis());
				 System.out.println("Timestamp #2: "+ts);
				 
				 String SqlTabInfo = "INSERT INTO DBA.TABINFO(TRANSACT,EMPNUM,TIMESTART,TABLENUM,InUse,Course)"+
						 "VALUES (?,?,?,?,?,?)";
				 
				 PreparedStatement TabInfo = con.prepareStatement(SqlTabInfo);
				 TabInfo.setInt(1, NumFactura);
				 TabInfo.setInt(2, 777);
				 TabInfo.setTimestamp(3, ts);
				 TabInfo.setInt(4, 30001);
				 TabInfo.setInt(5, 1);
				 TabInfo.setInt(6, 0);
				 
				 TabInfo.execute();
				 TabInfo.close();
			 }catch(Exception e)
			 {
				 System.out.println("Problemas insertando en tabla TAB INFO " + e.getMessage());
			 }
			 
			 
			 //Insertando nueva transacciÃ³n en dba.PosHDelivery
			 
			 try 
			 {
				 ts = new Timestamp(System.currentTimeMillis());
				 System.out.println("Timestamp #3: "+ts);
				 
				 String SqlPosHDelivery = "INSERT INTO DBA.PosHDelivery(Transact,MemCode,OpenDate,DeliveryStatus,UpdateStatus,SNum)"
						 +"VALUES (?,?,?,?,?,?)";
				 
				 PreparedStatement PosHDelivery = con.prepareStatement(SqlPosHDelivery);
				 PosHDelivery.setInt(1, NumFactura);
				 PosHDelivery.setInt(2, MemCode);
				 PosHDelivery.setDate(3, DiaApertura);
				 PosHDelivery.setInt(4, 0);
				 PosHDelivery.setInt(5, 1);
				 PosHDelivery.setInt(6, -1);
				 
				 PosHDelivery.execute();
				 PosHDelivery.close();
			 }catch(Exception e)
			 {
				 System.out.println("Error insertando informacion en PosHDelivery " + e.getMessage());
			 }
			 
			 //Insertando nueva transacciÃ³n en DBA.POSDETAIL
			 RunPosDetail(con);
			 
			//Borrando registro en dba.Tabinfo
			 try{
				 PreparedStatement BorrarTabInfo = con.prepareStatement("DELETE FROM DBA.TABINFO WHERE TRANSACT = ?");
				 BorrarTabInfo.setInt(1, NumFactura);
				 
				 BorrarTabInfo.execute();
				 BorrarTabInfo.close();
			 }catch(Exception e)
			 {
				 System.out.println("Error borrando información de  TABINFO " + e.getMessage());
			 }
			 
			
			 
			 //Actualizando registro en dba.Member del pedidos realizados
			 
			 try{
				 ts = new Timestamp(System.currentTimeMillis());
				 System.out.println("Timestamp #5: "+ts);
				 String NumDeliverys = "SELECT a.LastTrans,a.LastTrans2,(SELECT COUNT(*) FROM dba.PosHDelivery h WHERE a.memcode = h.memcode) "
						 + "FROM dba.Member a WHERE a.MEMCODE = ?";
				 
				 PreparedStatement RegisInMember = con.prepareStatement(NumDeliverys);
				 RegisInMember.setInt(1, MemCode);
				 rs = RegisInMember.executeQuery();
				 rs.next();
				 int LastTrans2 = rs.getInt(1);
				 int LastTrans3 = rs.getInt(2);
				 int NumberDeliverys = rs.getInt(3);
				 RegisInMember.close();
				 rs.close();
				 
				 RegisInMember = con.prepareStatement("UPDATE dba.Member SET LastTrans=?,LastTrans2=?,LastTrans3=?,NumDeliverys=?,LastOrderDate=? WHERE MemCode =?");
				 RegisInMember.setInt(1, NumFactura);
				 RegisInMember.setInt(2, LastTrans2);
				 RegisInMember.setInt(3, LastTrans3);
				 RegisInMember.setInt(4, NumberDeliverys);
				 RegisInMember.setTimestamp(5, ts);
				 RegisInMember.setInt(6, MemCode);
				 RegisInMember.executeQuery();
				 RegisInMember.close();
			 }catch(Exception e)
			 {
				 System.out.println("Error actualizando último pedido en tabla member " + e.getMessage());
			 }
			 
			 
			 //Imprimiendo Facturas
			 Invoice(con,NumFactura,777,MemCode,MethodNum);
			 		 
			 //Receta y Ajuste de Inventario
			 Recipe(con,pruebaPedido,NumFactura);
			 
			 ts = new Timestamp(System.currentTimeMillis());
			 System.out.println("Timestamp Final: "+ts);
			 System.out.println("=====Listo=====");
			 
			 listPosdetail.clear();
			 try
			 {
				 con.close();
			 }catch(Exception e)
			 {
				 System.out.println("Error realizando el cerrado de la conexión " + e.getMessage());
			 }
			 
			 InsertarPedidoControl(idpedido,NumFactura, dsnODBC);
			 
		
		 return(NumFactura);
	}

	
	public int actualizarCrearCliente(int MemCode, Cliente MiembroEnviado, String dsnODBC)
	{
		 int idClienteTienda = 0;
		 ConexionBaseDatos conexion = new ConexionBaseDatos();
		 Connection con = conexion.obtenerConexionBDTienda(dsnODBC);
		 java.sql.Date DiaApertura;
		 Statement state;
		 try
		 {
			 state = con.createStatement();
		 }catch(Exception e)
		 {
			 System.out.println("Problema creando el statement " + e.getMessage());
			 return(0);
		 }
		 
		 ResultSet rs;
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		 Date parsedDate = new Date();
		 try
		 {
			 parsedDate = dateFormat.parse("1899-12-30 00:00:00.000");
		 }catch(Exception e)
		 {
			 System.out.println("Problemas en parseo de fecha parseDate "+ e.getMessage());
		 }
		 Timestamp Inicialts = new java.sql.Timestamp(parsedDate.getTime());
		 System.out.println("Fecha Inicial Sistema: "+Inicialts);
		 
		 Timestamp ts = new Timestamp(System.currentTimeMillis());
		 CallableStatement proc;
		//MemCode: Codigo del miembro o cliente
//		Cliente(int idcliente, String telefono, String nombres, String apellidos, String nombreCompania,
//				String direccion, String municipio, int idMunicipio, float latitud, float lontitud, String zonaDireccion,
//				String observacion, String tienda, int idtienda) 
		
	    // Se prepara la sentencia para realizar la inserción del cliente en caso de que sea necesario 
		PreparedStatement Member = null;
		String SqlInsertMember = "INSERT INTO DBA.Member(MEMCODE,FIRSTNAME,LASTNAME,ADRESS1,ADRESS2,CITY,PROV,STARTDATE,"
				+"EXPDATE,ANNIVER,HOMETELE,LASTVISIT,MEMDIS,CARDNUM,GROUPNUM,ISACTIVE,Country,"
				+"UpdateStatus,Directions,CompanyName,Type,HasBioReg,NoSolicit)"
				+"VALUES (?,?,?,?,?,?,'Antioquia',?,?,?,?,?,0,?,0,1,'',1,?,?,0,0,0)";
		String SqlUpdateMember = "UPDATE DBA.Member SET FIRSTNAME=?,LASTNAME=?,ADRESS1=?,ADRESS2=?,CITY=?,"
				+ "Directions=?,CompanyName=?,HOMETELE=? WHERE MEMCODE=?";
		
		
		
		//Si existe Memcode>0 y Bool = False **no hacer nada
		//Si existe Memcode>0 y Bool = True **Actualizar
		//No existe Memcode = 0 y ignora Bool **Crea
		
		try{
			if (MemCode == 0) {
				parsedDate = dateFormat.parse("2040-12-31 00:00:00.000");
				Timestamp LastDayTS = new java.sql.Timestamp(parsedDate.getTime());
								
				proc = con.prepareCall("{call DBA.GetNextAutoInc(?,?)}");
				proc.registerOutParameter(1, Types.INTEGER);
				proc.setString(2, "GETNEXT_MEMBER");
				proc.execute();
				MemCode = proc.getInt(1);
				proc.close();
				
				Member = con.prepareStatement(SqlInsertMember);
				Member.setInt(1, MemCode);
				Member.setString(2, MiembroEnviado.getNombres());
				Member.setString(3, MiembroEnviado.getApellidos());
				Member.setString(4, MiembroEnviado.getDireccion());
				Member.setString(5, MiembroEnviado.getZonaDireccion());//revisar
				Member.setString(6, MiembroEnviado.getMunicipio());
				Member.setTimestamp(7, ts);
				Member.setTimestamp(8, LastDayTS);
				Member.setTimestamp(9, ts);
				Member.setString(10, MiembroEnviado.getTelefono());
				Member.setTimestamp(11, ts);
				Member.setString(12, Integer.toString(MemCode));
				Member.setString(13, MiembroEnviado.getObservacion());
				Member.setString(14, MiembroEnviado.getNombreCompania());
				
				Member.execute();
				Member.close();
				
				MiembroEnviado.setMemcode(MemCode);
				idClienteTienda = MemCode;
				
			}else 
			{
				Member = con.prepareStatement(SqlUpdateMember);
				Member.setString(1, MiembroEnviado.getNombres());
				Member.setString(2, MiembroEnviado.getApellidos());
				Member.setString(3, MiembroEnviado.getDireccion());
				Member.setString(4, MiembroEnviado.getZonaDireccion());//revisar
				Member.setString(5, MiembroEnviado.getMunicipio());
				Member.setString(6, MiembroEnviado.getObservacion());
				Member.setString(7, MiembroEnviado.getNombreCompania());
				Member.setString(8, MiembroEnviado.getTelefono());
				Member.setInt(9, MemCode);
				idClienteTienda = MemCode;
				Member.execute();
				Member.close();
			}
			
		}catch(Exception e)
		{
			System.out.println("Se tuvieron inconvenientes en la actualización/insercion del cliente " +  e.getMessage());
			
		}
		return(idClienteTienda);
	}
}
