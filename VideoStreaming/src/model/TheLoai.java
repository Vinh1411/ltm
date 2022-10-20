
package model;

public class TheLoai {
    private int id;
    private int idPhim;
    private String ten;
    private String moTa;

    public TheLoai() {
    }

    public TheLoai(int id, int idPhim, String ten, String moTa) {
        this.id = id;
        this.idPhim = idPhim;
        this.ten = ten;
        this.moTa = moTa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPhim() {
        return idPhim;
    }

    public void setIdPhim(int idPhim) {
        this.idPhim = idPhim;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    
    
}
