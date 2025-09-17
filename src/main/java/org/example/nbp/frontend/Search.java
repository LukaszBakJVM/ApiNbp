package org.example.nbp.frontend;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.example.nbp.RatesServices;
import org.example.nbp.dto.ResponseAllSavedRates;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Route("all")
public class Search extends VerticalLayout {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.of("UTC"));
    private final RatesServices ratesServices;


    private final Grid<ResponseAllSavedRates> grid;


    public Search(RatesServices ratesServices) {
        this.ratesServices = ratesServices;

        grid = new Grid<>(ResponseAllSavedRates.class, false);
        grid.addColumn(ResponseAllSavedRates::currency).setHeader("Waluta");
        grid.addColumn(ResponseAllSavedRates::value).setHeader("Kurs");
        grid.addColumn(item -> formatter.format(item.date())).setHeader("Data").setSortable(true).setComparator(ResponseAllSavedRates::date);

        RouterLink index = new RouterLink("Powrót na strone głowną", IndexView.class);
        index.getStyle().set("font-weight", "bold");


        Button searchButton = new Button("Wyświetl wszystkie", e -> allData());


        add(searchButton, grid,index);
    }


    private void allData() {
        ratesServices.allSavedRates().collectList().subscribe(rates -> getUI().ifPresent(ui -> ui.access(() -> grid.setItems(rates))));

    }
}

