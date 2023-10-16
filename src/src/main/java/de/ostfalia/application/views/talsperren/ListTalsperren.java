package de.ostfalia.application.views.talsperren;

import de.ostfalia.application.data.entity.Talsperre;
import de.ostfalia.application.data.service.TalsperrenService;
import de.ostfalia.application.views.BasicLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("/")
public class ListTalsperren extends BasicLayout {
    private TalsperrenService service;
                                    // false => automatisches setzen der Ueberschriften ausschalten
    private Grid<Talsperre> grid = new Grid<>(Talsperre.class, false);
    ListTalsperren(TalsperrenService service){
        this.service = service;

        createContent();
        // hauptinhalt der webseite setzen
        this.setContent(grid);
        this.getTitle().setText("Talsperren");

    }

    /**
     * Grid mit den Talsperrendaten fuellen
     */
    public void createContent(){
        grid.setSizeFull();
        // ueberschriftennamen muessen exakt wie Properties in der  Enitity heissen
        // getter und setter muessen gesetzt sein
        List<Talsperre> sperren =service.getAll();
        // beim setzen der items werden die spalten automatisch generiert
        // spaltentitel => alle attribute der entity
        grid.setItems(sperren);
        grid.addColumn(Talsperre::getName).setHeader("Name");

        // fotospalte eigens definieren
        grid.addColumn(new ComponentRenderer<>(sperre -> {
            Image image = new Image("/images/"+sperre.getImgID()+".jpg", sperre.getName());
            image.setMaxHeight("150px");
            image.setMaxWidth("250px");
            return image;
        })).setHeader("Image");
        grid.addColumn(new ComponentRenderer<>(sperre -> {
            Button b = new Button("Details zu " + sperre.getName(), buttonClickEvent -> {
                this.getUI().ifPresent(ui ->
                        ui.navigate(TalsperreDetail.class, sperre.getId()));
            });

            return b;
        }));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

    }
}
