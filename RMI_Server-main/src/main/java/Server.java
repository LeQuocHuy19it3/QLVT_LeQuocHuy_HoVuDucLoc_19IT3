import com.rmi.*;
import utils.AbstractDB;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements RMI {
    private Connection conn;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;
    public Server() throws RemoteException{
        super();
    }
    @Override
    public List<TauModel> findAll() throws RemoteException {
        List<TauModel> taus = new ArrayList<>();
        conn = AbstractDB.getConnection();

        String sql = "select * from Tau inner join LichTrinh where lichTrinh = lid ;";
        try {
            ps =conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TauModel tau = new TauModel(rs.getInt("taiid"),
                        rs.getString("tenTau"),
                        rs.getInt("soToa"),
                        new LichTrinhModel(rs.getInt("lid"),
                                rs.getString("ngayDi"),
                                rs.getString("ngayDen")));
                taus.add(tau);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        return taus;
    }

    @Override
    public List<ToaModel> findAllToa() throws RemoteException {
        List<ToaModel> toas = new ArrayList<>();
        conn = AbstractDB.getConnection();

        String sql = "select * from Toa inner join Tau where taiid = id_tau";
        try {
            ps =conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                ToaModel toa = new ToaModel(rs.getInt("toaid"),
                        rs.getInt("soGhe"),
                        rs.getLong("giaVe"),
                        rs.getInt("id_tau"));
                toas.add(toa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        return toas;
    }

    @Override
    public int insertToa(ToaModel toa) throws RemoteException {
        int success = 1;
        conn = AbstractDB.getConnection();

        String sql = "insert into Toa(soGhe, giaVe, id_tau)" +
                " VALUES ('"+toa.getSoGhe()+"','"+toa.getGiaVe()+"','"+toa.getIdtau()+"')";
        try {
            ps = conn.prepareStatement(sql);
            success = ps.executeUpdate();

        } catch (SQLException e) {
            success = 2;
            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return success;
    }

    @Override
    public int insertPhieu(PhieuDatVeModel phieuDatVeModel) throws RemoteException {
        conn = AbstractDB.getConnection();
        ResultSet resultSet = null;
        int id =0;

        String sql = "insert into PhieuDatVe(name, id_toa) VALUES ('"+phieuDatVeModel.getName()+"','"+
                phieuDatVeModel.getIdToa()+"')";
        try {
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return id;
    }

    @Override
    public int insertKhachHang(KhachHangModel khachHangModel) throws RemoteException {
        conn = AbstractDB.getConnection();
        ResultSet resultSet = null;
        int id =0;

        String sql = "insert into KhachHang(hoTen, gioiTinh, sdt, id_phieu)" +
                " VALUES ('"+khachHangModel.getHoTen()+"','"
                +khachHangModel.isGioiTinh()+"','"+khachHangModel.getSdt()
                +"','"+khachHangModel.getMaPhieuint()+"')";
        try {
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return id;
    }

    @Override
    public List<GaModel> findAllGa() throws RemoteException {
        List<GaModel> gas = new ArrayList<>();
        conn = AbstractDB.getConnection();

        String sql = "select * from Ga;";
        try {
            ps =conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                GaModel ga = new GaModel(rs.getInt("gid"),rs.getString("tenGa"));
                gas.add(ga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return gas;
    }

    @Override
    public List<TauModel> findTauByGa(int i) throws RemoteException {
        List<TauModel> taus = new ArrayList<>();
        conn = AbstractDB.getConnection();

        String sql = "select * from Tau inner join LichTrinh where lichTrinh=lid and id_ga="+i+"";
        try {
            ps =conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TauModel tau = new TauModel(rs.getInt("taiid"),
                        rs.getString("tenTau"),
                        rs.getInt("soToa"),
                        new LichTrinhModel(rs.getInt("lid"),
                                rs.getString("ngayDi"),
                                rs.getString("ngayDen")),rs.getInt("id_ga"));
                taus.add(tau);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && st != null && conn != null) {
                try {
                    rs.close();
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return taus;
    }

    public static void main(String[] args) {
//        try {
//            Server sv = new Server();
//            List<GaModel> list= sv.findAllGa();
//            for (GaModel item : list){
//                System.out.println(item.getName());
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        try{
            Registry req = LocateRegistry.createRegistry(2000);
            req.rebind("quanlyvetau",new Server());
            System.out.println("server running.....");
        }catch (Exception e){
            e.getMessage();
        }
    }
}
