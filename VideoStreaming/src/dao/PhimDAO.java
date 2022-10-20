package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import model.Phim;

public class PhimDAO extends DAO {
    public PhimDAO(){
        super();
    }
    public ArrayList<Phim> findAll(){
        ArrayList <Phim> list=new ArrayList();
        String sql = "SELECT * FROM phim";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Phim p = new Phim();
                p.setId(rs.getInt("id"));
                p.setLinkAnh(rs.getString("linkAnh"));
                p.setLinkVideo(rs.getString("linkVideo"));
                p.setMoTa(rs.getString("moTa"));
                p.setNamSX(rs.getString("namSX"));
                p.setNoiSX(rs.getString("noiSX"));
                p.setTen(rs.getString("ten"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<Phim> searchPhimKey(String key) {
        ArrayList<Phim> result = new ArrayList<Phim>();
        String sql = "SELECT * FROM phim WHERE ten LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Phim p = new Phim();
                p.setId(rs.getInt("id"));
                p.setLinkAnh(rs.getString("linkAnh"));
                p.setLinkVideo(rs.getString("linkVideo"));
                p.setMoTa(rs.getString("moTa"));
                p.setNamSX(rs.getString("namSX"));
                p.setNoiSX(rs.getString("noiSX"));
                p.setTen(rs.getString("ten"));
                result.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
