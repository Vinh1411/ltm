package model;

public class Phim {
    private int id;
    private String ten;
    private String linkVideo;
    private String linkAnh;
    private String moTa;
    private String namSX;
    private String noiSX;

    public Phim() {
    }

    public Phim(int id, String ten, String linkVideo, String linkAnh, String moTa, String namSX, String noiSX) {
        this.id = id;
        this.ten = ten;
        this.linkVideo = linkVideo;
        this.linkAnh = linkAnh;
        this.moTa = moTa;
        this.namSX = namSX;
        this.noiSX = noiSX;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getNamSX() {
        return namSX;
    }

    public void setNamSX(String namSX) {
        this.namSX = namSX;
    }

    public String getNoiSX() {
        return noiSX;
    }

    public void setNoiSX(String noiSX) {
        this.noiSX = noiSX;
    }
}
