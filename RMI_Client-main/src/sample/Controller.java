package sample;

import com.rmi.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    Registry req;
    RMI rmi;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private GridPane grid;
    @FXML
    private TextField txtseats;
    @FXML
    private TextField txtprice;
    @FXML
    private TextField txtname;
    @FXML
    private Button btn;
    @FXML
    private ComboBox<SexModel> sex;
    @FXML
    private TextField txtphone;
    @FXML
    private TextField txtaddress;
    @FXML
    private Button btnpay;
    @FXML
    private Button btnsreachts;

    private int idtau;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn.setDisable(true);
        btnpay.setDisable(true);
        try {
            req = LocateRegistry.getRegistry(2000);
            rmi= (RMI) req.lookup("quanlyvetau");
            setSex();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void setSex(){
        ObservableList<SexModel> list = FXCollections.observableArrayList();
        list.add(new SexModel(0,"Nam"));
        list.add(new SexModel(1,"Nữ"));
        sex.setItems(list);
    }

    public void loadToa(){
        grid.getChildren().clear();
        int soToa = convertNametoSoToa(comboBox.getValue());
        int id = convertNametoId(comboBox.getValue());
        try {
            List<ToaModel> toas = rmi.findAllToa();
            for (int j = 0 ;j <4; j++){
                for (int i = 1;i <= (soToa/4) ; i++){
                    int dem = i + (soToa/4)*j;
                    Button Toa = new Button(String.valueOf(dem));
                    Toa.setMaxSize(80,30);

                    for (ToaModel item:toas){
                        Toa.setOnAction(event1 -> {
                            txtseats.setText(String.valueOf(dem));
                            txtprice.setText(String.valueOf(item.getGiaVe()));
                            btnpay.setDisable(false);
                            idtau = id;
                            System.out.println(idtau);
                        });
                        if(item.getIdtau() == id){
                            if (item.getSoGhe()==dem){
                                Toa.setDisable(true);
                            }
                        }
                    }
//                Toa.setDisable(true);
                    grid.add(Toa,j,i-1);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getToa(ActionEvent event){

        loadToa();

    }
    public int isertPhieu(){
        try {
            PhieuDatVeModel phieu = new PhieuDatVeModel();
            phieu.setName(new RandomString().generateRandomString());
            phieu.setIdToa(Integer.parseInt(txtseats.getText()));
            return rmi.insertPhieu(phieu);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int isertKhachHang(){
        try {
            KhachHangModel khach = new KhachHangModel();
            khach.setHoTen(txtname.getText());
            khach.setGioiTinh((byte)sex.getValue().getId());
            khach.setSdt(txtphone.getText());
            khach.setMaPhieuint(isertPhieu());
            return rmi.insertKhachHang(khach);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void pay(ActionEvent event){
        if (txtname.getText().equals("") || txtphone.getText().equals("") || txtaddress.getText().equals("")){
            Notification.NotifError("Erro","Vui lÃ²ng nháº­p");
            return;
        }
        ToaModel toa = new ToaModel();
        toa.setSoGhe(Integer.parseInt(txtseats.getText()));
        toa.setGiaVe(Long.parseLong(txtprice.getText()));
        toa.setIdtau(idtau);
        try {
            int ok = rmi.insertToa(toa);
//            int phieu = isertPhieu();
            int khach = isertKhachHang();
            if (ok == 1  && khach>0){
                System.out.println("thÃ nh cÃ´ng");
                loadToa();
                Notification.NotifSuccess("Susscess","");
                txtseats.setText("");
                txtprice.setText("");
                txtname.setText("");
                sex.setValue(null);
                txtphone.setText("");
                txtaddress.setText("");
            }else {
                System.out.println("tháº¥t báº¡i");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getTau(ActionEvent event) {
        comboBox.setValue(null);
        String date = datePicker.getEditor().getText();
        System.out.println(date);
        try {
            List<TauModel> list = rmi.findAll();
            ObservableList<String> listTau = FXCollections.observableArrayList();
            for(TauModel item :list){
                if (date.equals(item.getLichTrinh().getNgayDi())){
                    listTau.add(item.getTenTau());
                }
            }
            btn.setDisable(false);
            comboBox.setItems(listTau);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void SearchTrainStation(ActionEvent event) throws IOException {
//        Node node = (Node) event.getSource();
        Stage stage = new Stage();

        //stage.setMaximized(true);
        stage.close();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("TrainStation.fxml")));
        stage.setScene(scene);
        stage.setTitle("TrainStation");
        stage.show();
    }

    public int convertNametoSoToa(String name){
        try {
            List<TauModel> list = rmi.findAll();
            for (TauModel item:list){
                if (name.equals(item.getTenTau())){
                    return item.getSoToa();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int convertNametoId(String name){
        try {
            List<TauModel> list = rmi.findAll();
            for (TauModel item:list){
                if (name.equals(item.getTenTau())){
                    return item.getId();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
