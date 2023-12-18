package de.ostfalia.application.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "Talsperren")
public class Talsperre {

    @Id
    @Column(name = "ID")
    long id;
    @NotEmpty
    @Column(name = "Name")
    String name;
    @Column(name = "InfoURL")
    String img;
    @Column(name = "ImageID")
    String imgID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }
    public long getId() {
        return id;
    }
}
