package de.ostfalia.application.views.talsperren;

import com.storedobject.chart.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.ostfalia.application.data.entity.Talsperre;
import de.ostfalia.application.data.entity.Talsperrendaten;
import de.ostfalia.application.data.service.TalsperrenService;
import de.ostfalia.application.views.BasicLayout;

import java.util.List;

@Route(value = "/detail")
@PageTitle("detailseite")
public class
TalsperreDetail extends BasicLayout implements HasUrlParameter<Long> {
    private TalsperrenService service;
    private Talsperre talsperre;

    TalsperreDetail(TalsperrenService service){
        this.service = service;

    }
    /**
     *
    wird automatisch nach den Kontruktor aufgerufen
     */
    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        talsperre = service.getTalsperreById(parameter);


        VerticalLayout layout = new VerticalLayout();


        layout.add(soChart(talsperre.getId()));
        layout.add(soChart21Days(talsperre.getId()));


        setContent(layout);

        this.getTitle().setText("Detailseite");


    }
    public SOChart soChart21Days(Long id) {

        SOChart soChart = new SOChart();
        soChart.setSize("100%", "500px");
        String title = "Talsperre " + (talsperre == null ? "" : talsperre.getName()) + " - Letzte 21 Tage";
        soChart.add(new Title(title));

        // Daten für die letzten 21 Tage abrufen
        List<Talsperrendaten> data = service.getDatenByIDLast21Days(id);

        TimeData xValues = new TimeData();
        Data abgabe = new Data();
        Data zufluss = new Data(), inhalt = new Data();

        for (int x = 0; x < data.size(); x++) {
            zufluss.add(data.get(x).getZufluss());
            inhalt.add(data.get(x).getStauinhalt());
            abgabe.add(data.get(x).getAbgabe());
            xValues.add(data.get(x).getTstamp());
        }

        // Line chart mit x und y werten füllen
        LineChart lineAbgabe = new LineChart(xValues, abgabe);
        lineAbgabe.setName("Wasserabgabe");
        LineChart lineZufluss = new LineChart(xValues, zufluss);
        lineZufluss.setName("Zufluss");
        LineChart lineInhalt = new LineChart(xValues, inhalt);
        lineInhalt.setName("Inhalt");

        // x Achse für das Frontend
        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(21); // Daten für 21 Tage
        xAxis.setName("Datum");
        // y Achse im Frontend
        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName("Wassermenge");
        // Rechtwinklige Achsen
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        // Line chart im rechtwinkligen Koordinatensystem zeichnen
        lineAbgabe.plotOn(rc);
        lineZufluss.plotOn(rc);
        lineInhalt.plotOn(rc);

        // Line charts der umfassenden Eltern Komponente hinzufügen
        soChart.add(lineAbgabe);
        soChart.add(lineZufluss);
        soChart.add(lineInhalt);

        return soChart;
    }


    public SOChart soChart(Long id){

        SOChart soChart = new SOChart();
        soChart.setSize("100%", "500px");
        String title ="Talsperre " + (talsperre==null ? "" : talsperre.getName());
        soChart.add(new Title(title));
        // Let us define some inline data.
        List<Talsperrendaten> data = service.getDatenByIDLast24H(id);
        TimeData xValues = new TimeData();

        Data abgabe = new Data();
        Data zufluss = new Data(), inhalt = new Data();

        for(int x = 0; x < data.size(); x++) {
            zufluss.add(data.get(x).getZufluss());
            inhalt.add(data.get(x).getStauinhalt());
            abgabe.add(data.get(x).getAbgabe());
            xValues.add(data.get(x).getTstamp());
        }

    // Line chart mit x und y werten fuellen
        LineChart lineAbgabe = new LineChart(xValues, abgabe);
        lineAbgabe.setName("Wasserabgabe");
        LineChart lineZufluss = new LineChart(xValues,zufluss);
        lineZufluss.setName("Zufluss");
        LineChart lineInhalt = new LineChart(xValues, inhalt);
        lineInhalt.setName("Inhalt");

        // x achse fuer das frontend
        XAxis xAxis = new XAxis(DataType.DATE);
        xAxis.setDivisions(24);// 24 stunden
        xAxis.setName("Uhrzeit");
        // y achse im frontend
        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName("Wassermenge");
        // rechtwinklige achsen
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        // line chart im rechtwinkligen koordinatensystem zeichnen
        lineAbgabe.plotOn(rc);
        lineZufluss.plotOn(rc);
        lineInhalt.plotOn(rc);

        // line charts der umfassenden Eltern Componente hinzufuegen
        soChart.add(lineAbgabe);
        soChart.add(lineZufluss);
        soChart.add(lineInhalt);

        return  soChart;
    }

}
